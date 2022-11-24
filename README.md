# Kafka To Kafka Beam Direct
Processing message from one local kafka topic to another local kafka topic using local Apache Beam pipeline


### Prerequisites -
* Install local kafka.
* Create 3 topics (input-events, output-events, error-events).

### Execution steps -
* Execute the TransformationPipeline with arguments.
* Publish messages in input topic.

### Result -
* Invalid data will publish message in error-events.
* Valid data will publish the data in output-events.