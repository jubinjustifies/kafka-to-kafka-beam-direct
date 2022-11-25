package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class Event implements Serializable
{

    @JsonProperty("replayId")
    private Integer replayId;

    @JsonProperty("replayId")
    public Integer getReplayId() {
        return replayId;
    }

    @JsonProperty("replayId")
    public void setReplayId(Integer replayId) {
        this.replayId = replayId;
    }


}
