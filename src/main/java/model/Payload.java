package model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.beam.sdk.coders.AvroCoder;
import org.apache.beam.sdk.coders.DefaultCoder;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@DefaultCoder(AvroCoder.class)
public class Payload implements Serializable {
    //private ChangeEventHeaderEntity ChangeEventHeader;

    private String Customer_ID;

    private String CRAG_ID;

    private String ORG_ID;

    private String CreditProposalID;

    private String FacilityID;

    private String FacilityLendingType;

    private String FacilityProductType;

    private String ProductCategory;

    private String ProductGroup;

    private String ProductSubType;

    private String SourceSystem;

    private String Currency;

    private String GrossAmt;

    private String NetAmt;

    private String GrossAmtGBP;

    private String NetAmtGBP;

    private String StartDate;

    private String MaturityDate;

    private String BankEntity;

    private String Status;

    private String LastModifiedDate;

    private String LastModifiedByID;

}

