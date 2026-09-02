package pardos;

import exceptions.Failure;
import options.ConsumerPipelineOptions;
import org.apache.beam.repackaged.core.org.apache.commons.lang3.ObjectUtils;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadFromKafka extends DoFn<String, PTransform<PBegin, PCollection<KV<String, String>>>>{
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadFromKafka.class);

    private final TupleTag<PTransform<PBegin, PCollection<KV<String, String>>>> outputTag = new TupleTag<PTransform<PBegin, PCollection<KV<String, String>>>>() {};
    private final TupleTag<Failure> failuresTag = new TupleTag<Failure>() {};

    @ProcessElement
    public void processElement(ProcessContext ctx, ConsumerPipelineOptions options, Pipeline pipeline){
        String input = ctx.element();
        PTransform<PBegin, PCollection<KV<String, String>>> output;
        try {
            if (ObjectUtils.isEmpty(options.getReadStartTime())) {
                output = KafkaIO.<String, String> read()
                        .withKeyDeserializer(StringDeserializer.class)
                        .withValueDeserializer(StringDeserializer.class)
                        .withBootstrapServers(options.getKafkaServer())
                        .withTopic(options.getInputTopic())
                        .withoutMetadata();
            } else {
                Instant startReadTime = Instant.parse(options.getReadStartTime());
                output = KafkaIO.<String, String> read()
                        .withKeyDeserializer(StringDeserializer.class)
                        .withValueDeserializer(StringDeserializer.class)
                        .withBootstrapServers(options.getKafkaServer())
                        .withTopic(options.getInputTopic())
                        .withStartReadTime(startReadTime)
                        .withoutMetadata();
            }
            ctx.output(output);
        }catch (Throwable throwable){
            LOGGER.error("Unable to read from Kafka topic: {}", options.getInputTopic(), throwable);
            final Failure failure = Failure.from("ReadFromKafka Step", input, throwable);
            ctx.output(failuresTag, failure);
        }
    }

    public TupleTag<PTransform<PBegin, PCollection<KV<String, String>>>> getOutputTag() {
        return outputTag;
    }

    public TupleTag<Failure> getFailuresTag() {
        return failuresTag;
    }
}
