package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class PayloadData implements Serializable {

    @JsonProperty("schema")
    private String schema;
    @JsonProperty("payload")
    private Payload payload;
    @JsonProperty("event")
    private Event event;

    @JsonProperty("schema")
    public String getSchema() {
        return schema;
    }

    @JsonProperty("schema")
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @JsonProperty("payload")
    public Payload getPayload() {
        return payload;
    }

    @JsonProperty("payload")
    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @JsonProperty("event")
    public Event getEvent() {
        return event;
    }

    @JsonProperty("event")
    public void setEvent(Event event) {
        this.event = event;
    }

}
