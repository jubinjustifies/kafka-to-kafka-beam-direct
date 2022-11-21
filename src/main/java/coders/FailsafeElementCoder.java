package coders;

import util.FailsafeElement;
import org.apache.beam.sdk.coders.*;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeParameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * The {@link FailsafeElementCoder} encodes and decodes {@link FailsafeElement} objects.
 *
 * <p>This coder is necessary until Avro supports parameterized types (<a
 * href="https://issues.apache.org/jira/browse/AVRO-1571">AVRO-1571</a>) without requiring to
 * explicitly specifying the schema for the type.
 *
 * @param <T> The type of the original payload to be encoded.
 * @param <t> The type of the current payload to be encoded.
 */
public class FailsafeElementCoder<T, t>
        extends CustomCoder<FailsafeElement<T, t>> {

  private static final NullableCoder<String> STRING_CODER = NullableCoder.of(StringUtf8Coder.of());
  private final Coder<T> originalPayloadCoder;
  private final Coder<t> currentPayloadCoder;

  private FailsafeElementCoder(
          Coder<T> originalPayloadCoder, Coder<t> currentPayloadCoder) {
    this.originalPayloadCoder = originalPayloadCoder;
    this.currentPayloadCoder = currentPayloadCoder;
  }

  public Coder<T> getOriginalPayloadCoder() {
    return originalPayloadCoder;
  }

  public Coder<t> getCurrentPayloadCoder() {
    return currentPayloadCoder;
  }

  public static <T, t> FailsafeElementCoder<T, t> of(
          Coder<T> originalPayloadCoder, Coder<t> currentPayloadCoder) {
    return new FailsafeElementCoder<>(originalPayloadCoder, currentPayloadCoder);
  }

  @Override
  public void encode(FailsafeElement<T, t> value, OutputStream outStream)
          throws IOException {
    if (value == null) {
      throw new CoderException("The FailsafeElementCoder cannot encode a null object!");
    }

    originalPayloadCoder.encode(value.getOriginalPayload(), outStream);
    currentPayloadCoder.encode(value.getPayload(), outStream);
    STRING_CODER.encode(value.getErrorMessage(), outStream);
    STRING_CODER.encode(value.getStacktrace(), outStream);
  }

  @Override
  public FailsafeElement<T, t> decode(InputStream inStream) throws IOException {

    T originalPayload = originalPayloadCoder.decode(inStream);
    t currentPayload = currentPayloadCoder.decode(inStream);
    String errorMessage = STRING_CODER.decode(inStream);
    String stacktrace = STRING_CODER.decode(inStream);

    return FailsafeElement.of(originalPayload, currentPayload)
            .setErrorMessage(errorMessage)
            .setStacktrace(stacktrace);
  }

  @Override
  public List<? extends Coder<?>> getCoderArguments() {
    return Arrays.asList(originalPayloadCoder, currentPayloadCoder);
  }

  @Override
  public TypeDescriptor<FailsafeElement<T, t>> getEncodedTypeDescriptor() {
    return new TypeDescriptor<FailsafeElement<T, t>>() {}.where(
            new TypeParameter<T>() {}, originalPayloadCoder.getEncodedTypeDescriptor())
            .where(new TypeParameter<t>() {}, currentPayloadCoder.getEncodedTypeDescriptor());
  }
}
