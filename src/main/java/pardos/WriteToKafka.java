package pardos;

import exceptions.Failure;
import options.ConsumerPipelineOptions;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteToKafka extends DoFn<KV<String, String>, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WriteToKafka.class);

    private final TupleTag<String> outputTag = new TupleTag<String>() {};
    private final TupleTag<Failure> failuresTag = new TupleTag<Failure>() {};

    @ProcessElement
    public void processElement(ProcessContext ctx, ConsumerPipelineOptions options){
      KV<String, String> input = ctx.element();
      try {
          KafkaIO.<String, String> write()
                  .withBootstrapServers(
                          options.getKafkaServer())
                  .withTopic(options.getValidOutputTopic())
                  .withKeySerializer(
                          org.apache.kafka.common.serialization.StringSerializer.class)
                  .withValueSerializer(
                          org.apache.kafka.common.serialization.StringSerializer.class);
      }catch (Throwable throwable){
          LOGGER.error("Unable to write to Kafka topic: {}", options.getInvalidOutputTopic(), throwable);
          final Failure failure = Failure.from("WriteToKafka Step", input, throwable);
          ctx.output(failuresTag, failure);
      }
    }

    public TupleTag<String> getOutputTag() {
        return outputTag;
    }

    public TupleTag<Failure> getFailuresTag() {
        return failuresTag;
    }
}
