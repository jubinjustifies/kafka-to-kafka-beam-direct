package models;

import lombok.Data;
import java.io.Serializable;

@Data
public class ErrorInfo implements Serializable {

    private String message;

    private String stepName;

    private String dataflow;

    private String error;
}
