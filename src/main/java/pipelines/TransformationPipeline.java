package pipelines;

import exceptions.Failure;
import org.apache.beam.sdk.values.*;
import pardos.CFLToJSONParser;
import models.CreditFacilityLimit;
import options.ConsumerPipelineOptions;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Values;
import org.joda.time.Duration;
import pardos.JSONToCFLParser;
import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

public class TransformationPipeline extends IngestionPipeline implements Serializable {

    public static void main(String[] args) throws IOException {

        ConsumerPipelineOptions options =
                PipelineOptionsFactory.fromArgs(args).withValidation()
                        .as(ConsumerPipelineOptions.class);

        TransformationPipeline pipeline = new TransformationPipeline();
        PipelineResult run = pipeline.run(options);
        run.waitUntilFinish(Duration.standardSeconds(options.getDuration()));
    }


    @Override
    public PCollection<KV<String, String>> basicTransformation(PCollection<KV<String, String>> kafkaMessages, ConsumerPipelineOptions options) {

        PCollection<String> payload = kafkaMessages.apply("ExtractPayload",
                        Values.<String>create())
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
                        if (c.element().contains(country)) {
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
    public PCollection<KV<String, String>> initiateTransformations(PCollection<KV<String, String>> kafkaMessages,
                                        ConsumerPipelineOptions options) {
        UUID uuid = UUID.randomUUID();

        LOGGER.info("{} - Transforming Pipeline with message {}", uuid, kafkaMessages);

        final PCollection<String> jsonMessage = kafkaMessages.apply(Values.<String>create());

        jsonMessage.apply("LogIngestedMessages", MapElements.into(TypeDescriptor.of(String.class))
                .via(message -> {
                    LOGGER.info("Ingested Messages: {}", message);
                    return message;
                }));

        LOGGER.info("Parsing JSON to CFL");
        JSONToCFLParser jsonToCFLParser = new JSONToCFLParser();
        final PCollectionTuple readStageTuple = jsonMessage
                .apply(ParDo.of(jsonToCFLParser)
                        .withOutputTags(jsonToCFLParser.getOutputTag(), TupleTagList.of(jsonToCFLParser.getFailuresTag())));

        PCollection<CreditFacilityLimit> creditFacilityLimit = readStageTuple.get(jsonToCFLParser.getOutputTag());
        PCollection<Failure> creditFacilityLimitFailure = readStageTuple.get(jsonToCFLParser.getFailuresTag());
        writeInvalidKafkaMessages(creditFacilityLimitFailure, options);

        LOGGER.info("Parsing CFL to JSON");
        CFLToJSONParser cflToJSONParser = new CFLToJSONParser();
        final PCollectionTuple writeStageTuple = creditFacilityLimit
                .apply(ParDo.of(cflToJSONParser)
                        .withOutputTags(cflToJSONParser.getOutputTag(), TupleTagList.of(cflToJSONParser.getFailuresTag())));

        PCollection<KV<String, String>> creditFacilityLimit2 = writeStageTuple.get(cflToJSONParser.getOutputTag());
        PCollection<Failure> creditFacilityLimitFailure2 = writeStageTuple.get(cflToJSONParser.getFailuresTag());
        writeInvalidKafkaMessages(creditFacilityLimitFailure2, options);

        creditFacilityLimit2.apply(Values.<String>create())
                .apply("LogParsedMessages", MapElements.into(TypeDescriptor.of(String.class))
                        .via(jsonRecord -> {
                            LOGGER.info("Parsed Messages: {}", jsonRecord);
                            return jsonRecord;
                }));

        return creditFacilityLimit2;
    }
}
