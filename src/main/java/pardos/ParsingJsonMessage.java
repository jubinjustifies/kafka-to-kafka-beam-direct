package pardos;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.Failure;
import models.CreditFacilityLimit;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParsingJsonMessage extends DoFn<String, CreditFacilityLimit> {

    public static TupleTag<Failure> failuresTag = new TupleTag<Failure>() {
    };
    public static TupleTag<CreditFacilityLimit> validTag = new TupleTag<CreditFacilityLimit>() {
    };
    private static final Logger LOG = LoggerFactory.getLogger(ParsingJsonMessage.class);

    public static PCollectionTuple process(PCollection<String> msgStrings) {
        return msgStrings.apply("Parsing Message", ParDo.of(new DoFn<String, CreditFacilityLimit>() {
            final ObjectMapper objectMapper = new ObjectMapper();
            CreditFacilityLimit creditFacilityLimit;
            String parseLine, json = null;

            @ProcessElement
            public void processElement(ProcessContext c) {
                parseLine = c.element();
                try {

                    LOG.info("Before parsing paylod received: {}", parseLine);
                    objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
                    creditFacilityLimit = objectMapper.readValue(parseLine, CreditFacilityLimit.class);
                    json = objectMapper.writeValueAsString(creditFacilityLimit);
                    LOG.info("After parsing paylod received: {}", json);
                    //LOG.info("creditFacilityLimit payload: {}", creditFacilityLimit.getData().getPayload());
                    c.output(creditFacilityLimit);

                } catch (Throwable throwable) {
                    LOG.error("Inside catch block Parseline and throwable: {}", throwable.getMessage());
                    Failure failure = new Failure("ParsingJsonMessage Class", parseLine, throwable);
                    c.output(failuresTag, failure);
                }
            }
        }).withOutputTags(
                validTag,
                TupleTagList.of(failuresTag)
        ));
    }
}