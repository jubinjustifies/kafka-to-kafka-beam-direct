package pipeline;

import options.ConsumerPipelineOptions;
import org.apache.beam.repackaged.core.org.apache.commons.lang3.ObjectUtils;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class KafkaToSinkIngestionPipeline {

    public static final Logger LOGGER = LoggerFactory.getLogger(KafkaToSinkIngestionPipeline.class);

    /**
     * This is the definition of the basicTransform which would be implemented by pipeline extending this Class.
     *
     * @param kafkaMessages The kafka messages in key-value form.
     * @param options       The options for running the pipeline.
     */
    public abstract PCollection<KV<String, String>> basicTransformation(PCollection<KV<String, String>> kafkaMessages,
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

        //registerCoder(pipeline);

        LOGGER.info("Coder registration completed and Starting to read Kafka Messages");

        PCollection<KV<String, String>> kafkaMessages = readKafkaMessages(pipeline, options);

        LOGGER.info("Received kafka message, initiating transformations");

        PCollection<KV<String, String>> transformedKafkaMessages = basicTransformation(kafkaMessages, options);

        publishKafkaMessages(transformedKafkaMessages, options);

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
                                                             ConsumerPipelineOptions options) throws IOException {

        if (ObjectUtils.isEmpty(options.getReadStartTime())) {
            return pipeline.apply(
                    "ReadFromKafka",
                    KafkaIO.<String, String> read()
                            .withKeyDeserializer(StringDeserializer.class)
                            .withValueDeserializer(StringDeserializer.class)
                            .withBootstrapServers(options.getKafkaServer())
                            .withTopic(options.getInputTopic())
                            .withoutMetadata()
            );
        } else {
            Instant startReadTime = Instant.parse(options.getReadStartTime());
            return pipeline.apply(
                    "ReadFromKafka",
                    KafkaIO.<String, String> read()
                            .withKeyDeserializer(StringDeserializer.class)
                            .withValueDeserializer(StringDeserializer.class)
                            .withBootstrapServers(options.getKafkaServer())
                            .withTopic(options.getInputTopic())
                            .withStartReadTime(startReadTime)
                            .withoutMetadata()
            );
        }
    }

    /**
     * This method is used to publish kafka messages using the Pipeline options defined.
     *
     * @param kafkaMessages The pipeline Object Passed
     * @param options  The custom Pipeline options passed.
     */
    public void publishKafkaMessages(PCollection<KV<String, String>> kafkaMessages, ConsumerPipelineOptions options){
        kafkaMessages.apply("PublishToKafka",
                KafkaIO.<String, String> write()
                        .withBootstrapServers(
                                options.getKafkaServer())
                        .withTopic(options.getOutputTopic())
                        .withKeySerializer(
                                org.apache.kafka.common.serialization.StringSerializer.class)
                        .withValueSerializer(
                                org.apache.kafka.common.serialization.StringSerializer.class));
    }
}
