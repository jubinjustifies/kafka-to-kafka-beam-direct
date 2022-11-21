package util;


import com.google.common.base.MoreObjects;
import coders.FailsafeElementCoder;
import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.DefaultCoder;

import java.util.Objects;

/**
 * The {@link FailsafeElement} class holds the current value and original value of a record within a
 * pipeline. This class allows pipelines to not lose valuable information about an incoming record
 * throughout the processing of that record. The use of this class allows for more robust
 * dead-letter strategies as the original record information is not lost throughout the pipeline and
 * can be output to a dead-letter in the event of a failure during one of the pipelines transforms.
 */
@DefaultCoder(FailsafeElementCoder.class)
public class FailsafeElement<T, t> {

    private final T originalPayload;
    private final t payload;
    @Nullable private String errorMessage;
    @Nullable private String stacktrace;

    private FailsafeElement(T originalPayload, t payload) {
        this.originalPayload = originalPayload;
        this.payload = payload;
    }

    public static <T, t> FailsafeElement<T, t> of(
            T originalPayload, t currentPayload) {
        return new FailsafeElement<>(originalPayload, currentPayload);
    }

    public static <T, t> FailsafeElement<T, t> of(
            FailsafeElement<T, t> other) {
        return new FailsafeElement<>(other.originalPayload, other.payload)
                .setErrorMessage(other.getErrorMessage())
                .setStacktrace(other.getStacktrace());
    }

    public T getOriginalPayload() {
        return originalPayload;
    }

    public t getPayload() {
        return payload;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public FailsafeElement<T, t> setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public FailsafeElement<T, t> setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final FailsafeElement other = (FailsafeElement) obj;
        return Objects.deepEquals(this.originalPayload, other.getOriginalPayload())
                && Objects.deepEquals(this.payload, other.getPayload())
                && Objects.deepEquals(this.errorMessage, other.getErrorMessage())
                && Objects.deepEquals(this.stacktrace, other.getStacktrace());
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalPayload, payload, errorMessage, stacktrace);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("originalPayload", originalPayload)
                .add("payload", payload)
                .add("errorMessage", errorMessage)
                .add("stacktrace", stacktrace)
                .toString();
    }
}
