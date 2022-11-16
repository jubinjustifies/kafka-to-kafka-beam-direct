package model;

import lombok.Builder;
import lombok.Data;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;
import java.io.Serializable;

@Data
@Builder
@DefaultCoder(AvroCoder.class)
public class Payload implements Serializable {
//    private ChangeEventHeaderEntity ChangeEventHeader;

    private String LLC_BI_Customer_ID;

    private String LLC_BI_CRAG_ID;

    private String LLC_BI_ORG_ID;

    private String LLC_BI_CreditProposalID;

    private String LLC_BI_FacilityID;

    private String LLC_BI_FacilityLendingType;

    private String LLC_BI_FacilityProductType;

    private String LLC_BI_ProductCategory;

    private String LLC_BI_ProductGroup;

    private String LLC_BI_ProductSubType;

    private String LLC_BI_SourceSystem;

    private String LLC_BI_Currency;

    private String LLC_BI_GrossAmt;

    private String LLC_BI_NetAmt;

    private String LLC_BI_GrossAmtGBP;

    private String LLC_BI_NetAmtGBP;

    private String LLC_BI_StartDate;

    private String LLC_BI_MaturityDate;

    private String LLC_BI_BankEntity;

    private String LLC_BI_Status;

    private String LastModifiedDate;

    private String LastModifiedByID;

}