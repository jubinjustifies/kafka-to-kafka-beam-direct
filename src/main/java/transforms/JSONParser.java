package transforms;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.CreditFacilityLimit;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

public class JSONParser extends DoFn<CreditFacilityLimit, KV<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSONParser.class);


//    private static final Gson GSON = new GsonBuilder()
//            .setDateFormat("yyyy-MM-dd")
//            .serializeNulls()
//            .create();


    @ProcessElement
    public void processElement(@Element CreditFacilityLimit input, OutputReceiver<KV<String, String>> output) {
        ObjectMapper objectMapper = new ObjectMapper();
        UUID uuid = UUID.randomUUID();
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        String json;
        try {
            LOGGER.info("Credit Facility Limit received: {}", input);

            json = objectMapper.writeValueAsString(input);
            LOGGER.info("Created Json String: {}", json);

            output.output(KV.of(uuid.toString(),json));
        } catch (Exception ex) {
            LOGGER.error("Unable to create JSON String", ex);
        }
    }

}


