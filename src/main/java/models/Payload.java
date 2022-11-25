package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class Payload implements Serializable {

    @JsonProperty("Customer_ID")
    private String CustomerID;
    @JsonProperty("CRAG_ID")
    private String CragId;
    @JsonProperty("ORG_ID")
    private String OrgId;
    @JsonProperty("CreditProposalID")
    private String CreditProposalID;
    @JsonProperty("FacilityID")
    private String FacilityID;
    @JsonProperty("FacilityLendingType")
    private String FacilityLendingType;
    @JsonProperty("FacilityProductType")
    private String FacilityProductType;
    @JsonProperty("ProductCategory")
    private String ProductCategory;
    @JsonProperty("ProductGroup")
    private String ProductGroup;
    @JsonProperty("ProductSubType")
    private String ProductSubType;
    @JsonProperty("SourceSystem")
    private String SourceSystem;
    @JsonProperty("Currency")
    private String Currency;
    @JsonProperty("GrossAmt")
    private String GrossAmt;
    @JsonProperty("NetAmt")
    private String NetAmt;
    @JsonProperty("GrossAmtGBP")
    private String GrossAmtGBP;
    @JsonProperty("NetAmtGBP")
    private String NetAmtGBP;
    @JsonProperty("StartDate")
    private String StartDate;
    @JsonProperty("MaturityDate")
    private String MaturityDate;
    @JsonProperty("BankEntity")
    private String BankEntity;
    @JsonProperty("Status")
    private String Status;
    @JsonProperty("LastModifiedDate")
    private String lastModifiedDate;
    @JsonProperty("LastModifiedByID")
    private String lastModifiedByID;
    @JsonProperty("ChangeEventHeader")
    private ChangeEventHeaderEntity ChangeEventHeader;


    @JsonProperty("Customer_ID")
    public String getCustomerID() {
        return CustomerID;
    }

    @JsonProperty("Customer_ID")
    public void setCustomerID(String CustomerID) {
        this.CustomerID = CustomerID;
    }

    @JsonProperty("CRAG_ID")
    public String getCragId() {
        return CragId;
    }

    @JsonProperty("CRAG_ID")
    public void setCragId(String CragId) {
        this.CragId = CragId;
    }

    @JsonProperty("ORG_ID")
    public String getOrgId() {
        return OrgId;
    }

    @JsonProperty("ORG_ID")
    public void setOrgId(String OrgId) {
        this.OrgId = OrgId;
    }

    @JsonProperty("CreditProposalID")
    public String getCreditProposalID() {
        return CreditProposalID;
    }

    @JsonProperty("CreditProposalID")
    public void setCreditProposalID(String CreditProposalID) {
        this.CreditProposalID = CreditProposalID;
    }

    @JsonProperty("FacilityID")
    public String getFacilityID() {
        return FacilityID;
    }

    @JsonProperty("FacilityID")
    public void setFacilityID(String FacilityID) {
        this.FacilityID = FacilityID;
    }

    @JsonProperty("FacilityLendingType")
    public String getFacilityLendingType() {
        return FacilityLendingType;
    }

    @JsonProperty("FacilityLendingType")
    public void setFacilityLendingType(String FacilityLendingType) {
        this.FacilityLendingType = FacilityLendingType;
    }

    @JsonProperty("FacilityProductType")
    public String getFacilityProductType() {
        return FacilityProductType;
    }

    @JsonProperty("FacilityProductType")
    public void setFacilityProductType(String FacilityProductType) {
        this.FacilityProductType = FacilityProductType;
    }

    @JsonProperty("ProductCategory")
    public String getProductCategory() {
        return ProductCategory;
    }

    @JsonProperty("ProductCategory")
    public void setProductCategory(String ProductCategory) {
        this.ProductCategory = ProductCategory;
    }

    @JsonProperty("ProductGroup")
    public String getProductGroup() {
        return ProductGroup;
    }

    @JsonProperty("ProductGroup")
    public void setProductGroup(String ProductGroup) {
        this.ProductGroup = ProductGroup;
    }

    @JsonProperty("ProductSubType")
    public String getProductSubType() {
        return ProductSubType;
    }

    @JsonProperty("ProductSubType")
    public void setProductSubType(String ProductSubType) {
        this.ProductSubType = ProductSubType;
    }

    @JsonProperty("SourceSystem")
    public String getSourceSystem() {
        return SourceSystem;
    }

    @JsonProperty("SourceSystem")
    public void setSourceSystem(String SourceSystem) {
        this.SourceSystem = SourceSystem;
    }

    @JsonProperty("Currency")
    public String getCurrency() {
        return Currency;
    }

    @JsonProperty("Currency")
    public void setCurrency(String Currency) {
        this.Currency = Currency;
    }

    @JsonProperty("GrossAmt")
    public String getGrossAmt() {
        return GrossAmt;
    }

    @JsonProperty("GrossAmt")
    public void setGrossAmt(String GrossAmt) {
        this.GrossAmt = GrossAmt;
    }

    @JsonProperty("NetAmt")
    public String getNetAmt() {
        return NetAmt;
    }

    @JsonProperty("NetAmt")
    public void setNetAmt(String NetAmt) {
        this.NetAmt = NetAmt;
    }

    @JsonProperty("GrossAmtGBP")
    public String getGrossAmtGBP() {
        return GrossAmtGBP;
    }

    @JsonProperty("GrossAmtGBP")
    public void setGrossAmtGBP(String GrossAmtGBP) {
        this.GrossAmtGBP = GrossAmtGBP;
    }

    @JsonProperty("NetAmtGBP")
    public String getNetAmtGBP() {
        return NetAmtGBP;
    }

    @JsonProperty("NetAmtGBP")
    public void setNetAmtGBP(String NetAmtGBP) {
        this.NetAmtGBP = NetAmtGBP;
    }

    @JsonProperty("StartDate")
    public String getStartDate() {
        return StartDate;
    }

    @JsonProperty("StartDate")
    public void setStartDate(String StartDate) {
        this.StartDate = StartDate;
    }

    @JsonProperty("MaturityDate")
    public String getMaturityDate() {
        return MaturityDate;
    }

    @JsonProperty("MaturityDate")
    public void setMaturityDate(String MaturityDate) {
        this.MaturityDate = MaturityDate;
    }

    @JsonProperty("BankEntity")
    public String getBankEntity() {
        return BankEntity;
    }

    @JsonProperty("BankEntity")
    public void setBankEntity(String BankEntity) {
        this.BankEntity = BankEntity;
    }

    @JsonProperty("Status")
    public String getStatus() {
        return Status;
    }

    @JsonProperty("Status")
    public void setStatus(String Status) {
        this.Status = Status;
    }

    @JsonProperty("LastModifiedDate")
    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    @JsonProperty("LastModifiedDate")
    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @JsonProperty("LastModifiedByID")
    public String getLastModifiedByID() {
        return lastModifiedByID;
    }

    @JsonProperty("LastModifiedByID")
    public void setLastModifiedByID(String lastModifiedByID) {
        this.lastModifiedByID = lastModifiedByID;
    }

    @JsonProperty("ChangeEventHeader")
    public ChangeEventHeaderEntity getChangeEventHeader() {
        return ChangeEventHeader;
    }

    @JsonProperty("ChangeEventHeader")
    public void setChangeEventHeader(ChangeEventHeaderEntity changeEventHeader) {
        this.ChangeEventHeader = changeEventHeader;
    }
}
