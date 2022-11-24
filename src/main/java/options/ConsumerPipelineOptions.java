package options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface ConsumerPipelineOptions extends PipelineOptions{

    @Description("Kafka Bootstrap Servers")
    @Default.String("localhost:9092")
    String getKafkaServer();

    void setKafkaServer(String value);

    @Description("Kafka Input Topic Name")
    @Default.String("input-events")
    String getInputTopic();

    void setInputTopic(String value);

    @Description("Kafka Valid Output Topic Name")
    @Default.String("output-events")
    String getValidOutputTopic();

    void setValidOutputTopic(String value);

    @Description("Kafka Invalid Output Topic Name")
    @Default.String("error-events")
    String getInvalidOutputTopic();

    void setInvalidOutputTopic(String value);

    @Description("Read Start Time")
    String getReadStartTime();
    void setReadStartTime(String readStartTime);

    @Description("Pipeline duration to wait until finish in seconds")
    @Default.Long(-1)
    Long getDuration();

    void setDuration(Long duration);

}
