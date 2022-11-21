package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo implements Serializable {

    private String message;

    private String stepName;

    private String dataflow;

    private String error;
}

