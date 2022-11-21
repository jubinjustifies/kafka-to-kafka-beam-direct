package model;

import lombok.*;
import lombok.Data;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

import java.io.Serializable;
import java.util.List;


@Data
@Builder
@DefaultCoder(AvroCoder.class)
public class ChangeEventHeaderEntity implements Serializable {
    private Long commitNumber;

    private String commitUser;

    private int sequenceNumber;

    private String entityName;

    private String changeType;

    private List<String> changeFields;

    private String changeOrigin;

    private String transactionKey;

    private long commitTimestamp;

    private List<String> recordIds;

}
