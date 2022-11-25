package pipelines;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaToKafkaUsingBeamDirectRunner {

    private static final Logger LOG = LoggerFactory
            .getLogger(KafkaToKafkaUsingBeamDirectRunner.class);

    /**
     * Specific pipeline options.
     */
    public interface Options extends PipelineOptions {
        @Description("Kafka Bootstrap Servers")
        @Default.String("localhost:9092")
        String getKafkaServer();

        void setKafkaServer(String value);

        @Description("Kafka Topic Name")
        @Default.String("test-events")
        String getInputTopic();

        void setInputTopic(String value);

        @Description("Kafka Output Topic Name")
        @Default.String("test-events-output")
        String getOutputTopic();

        void setOutputTopic(String value);

        @Description("Pipeline duration to wait until finish in seconds")
        @Default.Long(-1)
        Long getDuration();

        void setDuration(Long duration);

    }

    public static void main(String[] args) throws Exception {
//        // Create and set your PipelineOptions.
//        DataflowPipelineOptions option = PipelineOptionsFactory.as(DataflowPipelineOptions.class);
//
//        // For cloud execution, set the Google Cloud project, staging location,
//        // and set DataflowRunner.
//        option.setProject("");
//        option.setStagingLocation("");
//        option.setRunner(DataflowRunner.class);
//        Options options = option.as(Options.class);
        Options options = PipelineOptionsFactory.fromArgs(args)
                .withValidation().as(Options.class);
        LOG.info(options.toString());
        System.out.println(options.toString());
        Pipeline pipeline = Pipeline.create(options);

        // now we connect to the queue and process every event
        PCollection<KV<String, String>> kafka = pipeline.apply(
                KafkaIO.<String, String> read()
                        .withKeyDeserializer(StringDeserializer.class)
                        .withValueDeserializer(StringDeserializer.class)
                        .withBootstrapServers(options.getKafkaServer())
                        .withTopic(options.getInputTopic())
                        .withoutMetadata()
//                .apply("ExtractPayload",
//                Values.<String> create())
        );

        PCollection<String> payload = kafka.apply("ExtractPayload",
                        Values.<String> create())
                .apply(ParDo.of(new DoFn<String, String>() {
            @ProcessElement
            public void processElement(ProcessContext c) {
                System.out.println(String.format("** element |%s| **",
                        c.element()));
                c.output(c.element());
            }
        }));

        // We filter the events for a given country (IN=India) and send them to their own Topic
        final String country = "india";
        PCollection<String> eventsInIndia = payload.apply("FilterByCountry",
            ParDo.of(new DoFn<String, String>() {
                @ProcessElement
                public void processElement(ProcessContext c) {
                    if (c.element().contains(country)){
                        c.output(c.element());
                    }

                }
            }));

        PCollection<KV<String, String>> eventsInIndiaKV = eventsInIndia
                .apply("ExtractSpecificPayload",
                    ParDo.of(new DoFn<String, KV<String, String>>() {
                        @ProcessElement
                        public void processElement(ProcessContext c)
                                throws Exception {
                            c.output(KV.of("IN", c.element()));
                        }
                    }));



        eventsInIndiaKV.apply("PublishToKafka",
                        KafkaIO.<String, String> write()
                                .withBootstrapServers(
                                        options.getKafkaServer())
                                .withTopic(options.getOutputTopic())
                                .withKeySerializer(
                                        org.apache.kafka.common.serialization.StringSerializer.class)
                                .withValueSerializer(
                                        org.apache.kafka.common.serialization.StringSerializer.class));
        PipelineResult run = pipeline.run();
        run.waitUntilFinish(Duration.standardSeconds(options.getDuration()));
    }
}

