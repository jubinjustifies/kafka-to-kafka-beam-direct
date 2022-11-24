package model;

import adapters.EmptyStringAsNullTypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@DefaultCoder(AvroCoder.class)
public class CreditFacilityLimit implements Serializable {
    private PayloadData data;
    private String channel;

}
