package functions;

import com.google.gson.*;
import model.CreditFacilityLimit;
import model.Event;
import model.Payload;
import model.PayloadData;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONToCFLParser extends DoFn<String , CreditFacilityLimit> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONToCFLParser.class);

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .serializeNulls()
            .create();


    @ProcessElement
    public void processElement(@Element String input, OutputReceiver<CreditFacilityLimit> output) {
        try {

            CreditFacilityLimit creditFacilityLimit = GSON.fromJson(input,CreditFacilityLimit.class);
            PayloadData payloadData = creditFacilityLimit.getData();
            Payload payload = payloadData.getPayload();
            Event event = payloadData.getEvent();

            LOGGER.info("PayloadData received: {}", payloadData);
            LOGGER.info("Payload received: {}", payload);
            LOGGER.info("Event received: {}", event);
            LOGGER.info("Channel received: {}", creditFacilityLimit.getChannel());

            output.output(creditFacilityLimit);
        } catch (Exception ex) {
            LOGGER.error("Unable to parse JSON", ex);
        }
    }

}


