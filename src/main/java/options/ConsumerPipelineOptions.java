package options;

import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;

public interface ConsumerPipelineOptions extends PipelineOptions{

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

    @Description("Read Start Time")
    String getReadStartTime();
    void setReadStartTime(String readStartTime);

    @Description("Pipeline duration to wait until finish in seconds")
    @Default.Long(-1)
    Long getDuration();

    void setDuration(Long duration);

}
