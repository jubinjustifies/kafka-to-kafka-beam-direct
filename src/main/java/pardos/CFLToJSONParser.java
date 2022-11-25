package pardos;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.Failure;
import models.CreditFacilityLimit;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

public class CFLToJSONParser extends DoFn<CreditFacilityLimit, KV<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CFLToJSONParser.class);

    private final TupleTag<KV<String, String>> outputTag = new TupleTag<KV<String, String>>() {};
    private final TupleTag<Failure> failuresTag = new TupleTag<Failure>() {};

    @ProcessElement
    public void processElement(ProcessContext ctx) {

        UUID uuid = UUID.randomUUID();
        CreditFacilityLimit input = ctx.element();
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
            LOGGER.info("Credit Facility Limit received: {}", input);

            String json = objectMapper.writeValueAsString(input);
            LOGGER.info("Created Json String: {}", json);

            ctx.output(KV.of(uuid.toString(),json));
        } catch (Throwable throwable) {
            LOGGER.error("Unable to create JSON String: {}", throwable.getMessage());
            final Failure failure = Failure.from("CFLToJSONParser Step", input, throwable);
            ctx.output(failuresTag, failure);
        }
    }

    public TupleTag<KV<String, String>> getOutputTag() {
        return outputTag;
    }

    public TupleTag<Failure> getFailuresTag() {
        return failuresTag;
    }

}


