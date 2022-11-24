package pipelines;

import exceptions.Failure;
import exceptions.KafkaIOException;
import models.CreditFacilityLimit;
import models.ErrorInfo;
import options.ConsumerPipelineOptions;
import org.apache.beam.repackaged.core.org.apache.commons.lang3.ObjectUtils;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.coders.*;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.transforms.ToString;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import coders.FailsafeElementCoder;
import pardos.ReadFromKafka;
import java.io.IOException;
import java.util.Map;

public abstract class IngestionPipeline {

    public static final Logger LOGGER = LoggerFactory.getLogger(IngestionPipeline.class);

    /**
     * This is the definition of the basicTransform which would be implemented by pipeline extending this Class.
     *
     * @param kafkaMessages The kafka messages in key-value form.
     * @param options       The options for running the pipeline.
     */
    public abstract PCollection<KV<String, String>> basicTransformation(PCollection<KV<String, String>> kafkaMessages,
                                                                        ConsumerPipelineOptions options);


    /**
     * This is the definition of the initiateTransform which would be implemented by pipeline extending this Class.
     *
     * @param kafkaMessages The kafka messages in key-value form.
     * @param options       The options for running the pipeline.
     */
    public abstract PCollection<KV<String, String>> initiateTransformations(PCollection<KV<String, String>> kafkaMessages,
                                                 ConsumerPipelineOptions options);

    /**
     * This is the method that is being called by the pipeline extending this class to run the Pipeline.
     *
     * @param options The Options that are required to run the Pipeline.
     * @return PipelineResult
     */
    public PipelineResult run(ConsumerPipelineOptions options) throws IOException {

        LOGGER.info("Creating Pipeline");

        Pipeline pipeline = Pipeline.create(options);

        registerCoder(pipeline);

        LOGGER.info("Coder registration completed and Starting to read Kafka Messages");

        PCollection<KV<String, String>> kafkaMessages = readKafkaMessages(pipeline, options);

        LOGGER.info("Received kafka message, initiating transformations");

//        PCollection<KV<String, String>> transformedKafkaMessages = basicTransformation(kafkaMessages, options);

        PCollection<KV<String, String>> transformedCollection = initiateTransformations(kafkaMessages, options);

        writeValidKafkaMessages(transformedCollection, options);

        return pipeline.run();
    }

    /**
     * This method is used to read kafka messages using the Pipeline options defined.
     *
     * @param pipeline The pipeline Object Passed
     * @param options  The custom Pipeline options passed.
     * @return PCollection of key-value Kafka messages
     */
    public PCollection<KV<String, String>> readKafkaMessages(Pipeline pipeline,
                                                             ConsumerPipelineOptions options) {

        if(options.getInputTopic() != null) {
            if (ObjectUtils.isEmpty(options.getReadStartTime())) {
                return pipeline.apply(
                        "ReadFromKafka",
                        KafkaIO.<String, String>read()
                                .withKeyDeserializer(StringDeserializer.class)
                                .withValueDeserializer(StringDeserializer.class)
                                .withBootstrapServers(options.getKafkaServer())
                                .withTopic(options.getInputTopic())
                                .withoutMetadata()
                );
            } else {
                Instant startReadTime = Instant.parse(options.getReadStartTime());
                return pipeline.apply(
                        "ReadFromKafkaWithStartReadTime",
                        KafkaIO.<String, String>read()
                                .withKeyDeserializer(StringDeserializer.class)
                                .withValueDeserializer(StringDeserializer.class)
                                .withBootstrapServers(options.getKafkaServer())
                                .withTopic(options.getInputTopic())
                                .withStartReadTime(startReadTime)
                                .withoutMetadata()
                );
            }
        } else {
            throw new KafkaIOException("Input Topic is not configured.");
        }
    }

    /**
     * This method is used to publish valid data to kafka using the Pipeline options defined.
     *
     * @param validCollection The pipeline Object Passed
     * @param options  The custom Pipeline options passed.
     */
    public void writeValidKafkaMessages(PCollection<KV<String, String>> validCollection, ConsumerPipelineOptions options){
        validCollection.apply("WriteValidToKafka",
                KafkaIO.<String, String> write()
                        .withBootstrapServers(
                                options.getKafkaServer())
                        .withTopic(options.getValidOutputTopic())
                        .withKeySerializer(
                                org.apache.kafka.common.serialization.StringSerializer.class)
                        .withValueSerializer(
                                org.apache.kafka.common.serialization.StringSerializer.class));
    }

    /**
     * This method is used to publish failed data to kafka using the Pipeline options defined.
     *
     * @param failedCollection The pipeline Object Passed
     * @param options  The custom Pipeline options passed.
     */
    public void writeInvalidKafkaMessages(PCollection<Failure> failedCollection, ConsumerPipelineOptions options){
        failedCollection.apply(ToString.elements()).apply("WriteInvalidToKafka",
                KafkaIO.<String, String> write()
                        .withBootstrapServers(
                                options.getKafkaServer())
                        .withTopic(options.getInvalidOutputTopic())
                        .withKeySerializer(
                                org.apache.kafka.common.serialization.StringSerializer.class)
                        .withValueSerializer(
                                org.apache.kafka.common.serialization.StringSerializer.class).values());
    }


    /**
     * Method to register coder
     *
     * @param pipeline
     */
    public void registerCoder(Pipeline pipeline) {
        CoderRegistry coderRegistry = pipeline.getCoderRegistry();

        FailsafeElementCoder<KV<String, String>, String> coderKafka = FailsafeElementCoder
                .of(KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of()), StringUtf8Coder.of());

        coderRegistry.registerCoderForType(coderKafka.getEncodedTypeDescriptor(), coderKafka);

        FailsafeElementCoder<Map<String, String>, Map<String, String>> coderMap =
                FailsafeElementCoder.of(MapCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of()),
                        MapCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of()));

        coderRegistry.registerCoderForType(coderMap.getEncodedTypeDescriptor(), coderMap);

        FailsafeElementCoder<CreditFacilityLimit, CreditFacilityLimit> CFL_builder_coder =
                FailsafeElementCoder.of(SerializableCoder.of(CreditFacilityLimit.class), SerializableCoder.of(CreditFacilityLimit.class));

        coderRegistry.registerCoderForType(CFL_builder_coder.getEncodedTypeDescriptor(),
                CFL_builder_coder);

        FailsafeElementCoder<ErrorInfo, ErrorInfo> error_builder_coder =
                FailsafeElementCoder.of(SerializableCoder.of(ErrorInfo.class),
                        SerializableCoder.of(ErrorInfo.class));

        coderRegistry.registerCoderForType(error_builder_coder.getEncodedTypeDescriptor(),
                error_builder_coder);

    }




    /**
     * This method is used to read kafka messages using the Pipeline options defined.
     *
     * @param pipeline The pipeline Object Passed
     * @param options  The custom Pipeline options passed.
     * @return PCollection of key-value Kafka messages
     */
    public PCollection<KV<String, String>> readKafkaMessagesNew(Pipeline pipeline,
                                                             ConsumerPipelineOptions options) throws IOException {

        ReadFromKafka readFromKafka = new ReadFromKafka();
//        return pipeline.apply(ParDo.of(readFromKafka).withOutputTags(readFromKafka.getOutputTag(), TupleTagList.of(readFromKafka.getFailuresTag())));
        return null;
    }
}
