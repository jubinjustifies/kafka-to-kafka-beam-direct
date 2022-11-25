package pardos;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.Failure;
import models.CreditFacilityLimit;
import models.Event;
import models.Payload;
import models.PayloadData;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.TupleTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONToCFLParser extends DoFn<String , CreditFacilityLimit> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONToCFLParser.class);

    private final TupleTag<CreditFacilityLimit> outputTag = new TupleTag<CreditFacilityLimit>() {};
    private final TupleTag<Failure> failuresTag = new TupleTag<Failure>() {};


    @ProcessElement
    public void processElement(ProcessContext ctx) {
        String input = ctx.element();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
            CreditFacilityLimit creditFacilityLimit = objectMapper.readValue(input,CreditFacilityLimit.class);

            PayloadData payloadData = creditFacilityLimit.getData();
            Payload payload = payloadData.getPayload();
            Event event = payloadData.getEvent();

            LOGGER.info("PayloadData received: {}", payloadData);
            LOGGER.info("Payload received: {}", payload);
            LOGGER.info("Event received: {}", event);
            LOGGER.info("Channel received: {}", creditFacilityLimit.getChannel());
            LOGGER.error("JSON parsed to CFL object");

            ctx.output(creditFacilityLimit);
        } catch (Throwable throwable) {
            LOGGER.error("Unable to parse JSON: {}", throwable.getMessage());
            final Failure failure = Failure.from("JSONToCFLParser Step", input, throwable);
            ctx.output(failuresTag, failure);
        }
    }

    public TupleTag<CreditFacilityLimit> getOutputTag() {
        return outputTag;
    }

    public TupleTag<Failure> getFailuresTag() {
        return failuresTag;
    }

}


