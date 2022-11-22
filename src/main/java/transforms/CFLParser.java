package transforms;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.CreditFacilityLimit;
import models.Event;
import models.Payload;
import models.PayloadData;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CFLParser extends DoFn<String , CreditFacilityLimit> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CFLParser.class);

//    private static final Gson GSON = new GsonBuilder()
//            .setDateFormat("yyyy-MM-dd")
//            .serializeNulls()
//            .create();


    @ProcessElement
    public void processElement(@Element String input, OutputReceiver<CreditFacilityLimit> output) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
            CreditFacilityLimit creditFacilityLimit = objectMapper.readValue(input,CreditFacilityLimit.class);

//            CreditFacilityLimit creditFacilityLimit = GSON.fromJson(input,CreditFacilityLimit.class);
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


