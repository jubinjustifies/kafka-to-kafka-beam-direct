package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class CreditFacilityLimit implements Serializable
{

    @JsonProperty("data")
    private PayloadData data;
    @JsonProperty("channel")
    private String channel;

    @JsonProperty("data")
    public PayloadData getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(PayloadData data) {
        this.data = data;
    }

    @JsonProperty("channel")
    public String getChannel() {
        return channel;
    }

    @JsonProperty("channel")
    public void setChannel(String channel) {
        this.channel = channel;
    }

}

