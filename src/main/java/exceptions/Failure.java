package exceptions;

import java.io.Serializable;
import java.util.Arrays;

public class Failure implements Serializable {
    private final String pipelineStep;
    private final String inputElement;
    private final Throwable throwable;

    public Failure(String pipelineStep, String inputElement, Throwable throwable) {
        this.pipelineStep = pipelineStep;
        this.inputElement = inputElement;
        this.throwable = throwable;
    }

    public static <T> Failure from(final String pipelineStep,
                                            final T element,
                                            final Throwable throwable) {
        return new Failure(pipelineStep, element.toString(), throwable);
    }

    @Override
    public String toString() {
        return "{" +
                "\npipelineStep: " + pipelineStep +
                "\ninputElement: " + inputElement +
                "\nexception: " + throwable.getMessage() +
                "\nstackTrace: " + Arrays.toString(throwable.getStackTrace()) +
                "\n}";
    }
}
