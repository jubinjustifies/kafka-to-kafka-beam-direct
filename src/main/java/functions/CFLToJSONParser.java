package functions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.CreditFacilityLimit;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.KV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CFLToJSONParser extends DoFn<CreditFacilityLimit, KV<String, String>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CFLToJSONParser.class);

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .serializeNulls()
            .create();


    @ProcessElement
    public void processElement(@Element CreditFacilityLimit input, OutputReceiver<KV<String, String>> output) {
        try {
            LOGGER.info("Channel received: {}", input);

            LOGGER.info("PayloadData received: {}", input.getData());
            LOGGER.info("Payload received: {}", input.getData().getPayload());
            LOGGER.info("Event received: {}", input.getData().getEvent());
            LOGGER.info("Channel received: {}", input.getChannel());

            output.output(KV.of("",""));
        } catch (Exception ex) {
            LOGGER.error("Unable to update mutation", ex);
        }
    }

}


