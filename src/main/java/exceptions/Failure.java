package exceptions;

import java.io.Serializable;

public class Failure implements Serializable {
    private final String pipelineStep;
    private final String inputElement;
    private final Throwable exception;

    public Failure(String pipelineStep, String inputElement, Throwable exception) {
        this.pipelineStep = pipelineStep;
        this.inputElement = inputElement;
        this.exception = exception;
    }

    public static <T> Failure from(final String pipelineStep,
                                   final T element,
                                   final Throwable exception) {
        return new Failure(pipelineStep, element.toString(), exception);
    }
}
