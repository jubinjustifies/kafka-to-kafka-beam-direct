package pipeline;

import com.google.gson.*;
import model.CreditFacilityLimit;
import options.ConsumerPipelineOptions;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.joda.time.Duration;
import functions.JSONToCFLParser;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

public class SinkToKafkaTransformationPipeline extends KafkaToSinkIngestionPipeline implements Serializable {

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .serializeNulls()
            .create();


    public static void main(String[] args) throws IOException {

        ConsumerPipelineOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation()
                        .as(ConsumerPipelineOptions.class);

        SinkToKafkaTransformationPipeline pipeline = new SinkToKafkaTransformationPipeline();
        PipelineResult run = pipeline.run(options);
        run.waitUntilFinish(Duration.standardSeconds(options.getDuration()));
    }


    @Override
    public PCollection<KV<String, String>> basicTransformation(PCollection<KV<String, String>> kafkaMessages, ConsumerPipelineOptions options) {

        PCollection<String> payload = kafkaMessages.apply("ExtractPayload",
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

        return eventsInIndiaKV;
    }

    @Override
    public void initiateTransformations(PCollection<KV<String, String>> kafkaMessages,
                                        ConsumerPipelineOptions options) {
        UUID uuid = UUID.randomUUID();


        LOGGER.info("{} - Transforming Pipeline with message {}", uuid, kafkaMessages);

        PCollection<String> jsonMessage = kafkaMessages.apply("Get message contents", Values.<String>create());

        jsonMessage.apply("Log messages", MapElements.into(TypeDescriptor.of(String.class))
                .via(message -> {
                    LOGGER.info("Received: {}", message);
                    return message;
                }));

        PCollection<CreditFacilityLimit> creditFacilityLimit = jsonMessage.apply(ParDo.of(new JSONToCFLParser()));

//        PCollection<CreditFacilityLimit> creditFacilityLimit = jsonMessage.apply("Parse JSON",
//                MapElements.into(TypeDescriptor.of(CreditFacilityLimit.class))
//                        .via(message -> GSON.fromJson(message, CreditFacilityLimit.class)));


        LOGGER.info("##########Log CFL message#############");

//        creditFacilityLimit.apply(ParDo.of(new CFLToJSONParser()));

        LOGGER.info("##########End of Pipeline#############");
//        creditFacilityLimit.apply("Log CFL Messages", MapElements.into(TypeDescriptor.of(CreditFacilityLimit.class))
//                .via(jsonRecord -> {
//                    LOGGER.info("Received After parsing: {}", jsonRecord);
//                    return jsonRecord;
//                }));

    }

}
