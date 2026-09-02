package pipelines;

import com.google.pubsub.v1.PubsubMessage;
import constants.CommonConstants;
import exceptions.Failure;
import exceptions.KafkaIOException;
import models.CreditFacilityLimit;
import models.ErrorInfo;
import options.ConsumerPipelineOptions;
import org.apache.beam.repackaged.core.org.apache.commons.lang3.ObjectUtils;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.coders.*;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.vendor.grpc.v1p26p0.com.google.protobuf.ByteString;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import coders.FailsafeElementCoder;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public abstract class IngestionPipeline {

    public static final Logger LOGGER = LoggerFactory.getLogger(IngestionPipeline.class);

    /**
     * This is the definition of the testTransform used for testing purposes.
     *
     * @param kafkaMessages The kafka messages in key-value form.
     * @param options       The options for running the pipeline.
     */
    public abstract PCollection<String> testTransformation(PCollection<KV<String, String>> kafkaMessages,
                                                                        ConsumerPipelineOptions options);


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
                                                 ConsumerPipelineOptions options) throws IOException;

    /**
     * This is the method that is being called by the pipeline extending this class to run the Pipeline.
     *
     * @param options The Options that are required to run the Pipeline.
     * @return PipelineResult
     */
    public PipelineResult run(ConsumerPipelineOptions options) throws IOException {
        try {
            LOGGER.info("Creating Pipeline");

            Pipeline pipeline = Pipeline.create(options);

            registerCoder(pipeline);

            LOGGER.info("Coder registration completed and Starting to read Kafka Messages");

            PCollection<KV<String, String>> kafkaMessages = readKafkaMessages(pipeline, options);

            LOGGER.info("Received kafka message, initiating transformations");

            //basicTransform Run
//        PCollection<KV<String, String>> transformedKafkaMessages = basicTransformation(kafkaMessages, options);

            //Kafka To Kafka Run
        PCollection<KV<String, String>> transformedCollection = initiateTransformations(kafkaMessages, options);

        PCollection<Long> count = transformedCollection.apply(Count.globally());
            count.apply("LogParsedMessages", MapElements.into(TypeDescriptor.of(String.class))
                            .via(jsonRecord -> {
                                LOGGER.info("Count: {}", jsonRecord);
                                return String.valueOf(jsonRecord);
                            }));
//        writeValidKafkaMessages(transformedCollection, options);

            //Test Transform Run
//            PCollection<String> pubSubMessages = readPubSubMessages(pipeline, options);
//            writeValidKafkaMessages(pubSubMessages.apply("AddKey",
//                    ParDo.of(new DoFn<String, KV<String, String>>() {
//                        @ProcessElement
//                        public void processElement(ProcessContext c)
//                                throws Exception {
//                            c.output(KV.of("IN", c.element()));
//                        }
//                    })), options);
//            writeValidPubSubMessages(pubSubMessages, options);

            return pipeline.run();
        }catch (Throwable throwable){
            LOGGER.error("Pipeline failed: {}", throwable.getMessage());
//            final Failure failure = Failure.from("IngestionPipeline Step", null, throwable);
            return null;
        }
    }

    /**
     * This method is used to read kafka messages using the Pipeline options defined.
     *
     * @param pipeline The pipeline Object Passed
     * @param options  The custom Pipeline options passed.
     * @return PCollection of key-value Kafka messages
     * @exception IOException
     */
    public PCollection<KV<String, String>> readKafkaMessages(Pipeline pipeline,
                                                             ConsumerPipelineOptions options) throws IOException {

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
     * @exception IOException
     */
    public void writeValidKafkaMessages(PCollection<KV<String, String>> validCollection, ConsumerPipelineOptions options) throws IOException {
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
     * @exception IOException
     */
    public void writeInvalidKafkaMessages(PCollection<Failure> failedCollection, ConsumerPipelineOptions options) throws IOException {
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
    public PCollection<String> readPubSubMessages(Pipeline pipeline,
                                                  ConsumerPipelineOptions options) throws IOException {

        return pipeline.apply(
                "ReadFromPubSub", PubsubIO.readStrings()
                        .fromTopic(options.getPubsubInputTopic())
        );
    }

    public void writeValidPubSubMessages(PCollection<String> input,
                                         ConsumerPipelineOptions options) throws IOException {

        input.apply("WriteValidToPubSub", PubsubIO.writeStrings().to(options.getPubsubOutputTopic()));
    }

    public void writeInvalidPubSubMessages(PCollection<Failure> input,
                                    ConsumerPipelineOptions options) throws IOException {

        input.apply(ToString.elements())
                .apply("WriteInvalidToPubSub", PubsubIO.writeStrings().to(options.getPubsubOutputTopic()));
    }

    public void publishTextToKafka(String message, ConsumerPipelineOptions options) throws IOException {

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CommonConstants.BOOTSTRAP_SERVER);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        // create a producer record
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<>(UUID.randomUUID().toString(), message);
        // send data - asynchronous
        producer.send(producerRecord);

        // flush data - synchronous
        producer.flush();

        // flush and close producer
        producer.close();
    }

//    public void publishTextToPubSub(String message, ConsumerPipelineOptions options) throws IOException {
//
//        PubsubMessage pubsubMessage =
//                PubsubMessage.newBuilder().setData(ByteString.copyFromUtf8(message)).build();
//    }
}
