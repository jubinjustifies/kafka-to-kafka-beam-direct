package exceptions;

/** Exception thrown when the kafka config is invalid. */
public class KafkaIOException extends IllegalArgumentException {
    public KafkaIOException(String msg) {
        super(msg);
    }

    public KafkaIOException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public KafkaIOException(Throwable cause) {
        super(cause);
    }
}