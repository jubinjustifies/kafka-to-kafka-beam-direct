package model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import lombok.Data;
import java.io.Serializable;


@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@DefaultCoder(AvroCoder.class)
public class PayloadData implements Serializable {

    private String schema;
    private Payload payload;
    private Event event;

}
