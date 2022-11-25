package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class ChangeEventHeaderEntity implements Serializable {

    @JsonProperty("commitNumber")
    private Long commitNumber;
    @JsonProperty("commitUser")
    private String commitUser;
    @JsonProperty("sequenceNumber")
    private Integer sequenceNumber;
    @JsonProperty("entityName")
    private String entityName;
    @JsonProperty("changeType")
    private String changeType;
    @JsonProperty("changeFields")
    private String[] changeFields = null;
    @JsonProperty("changeOrigin")
    private String changeOrigin;
    @JsonProperty("transactionKey")
    private String transactionKey;
    @JsonProperty("commitTimestamp")
    private Long commitTimestamp;
    @JsonProperty("recordIds")
    private String[] recordIds = null;

    @JsonProperty("commitNumber")
    public Long getCommitNumber() {
        return commitNumber;
    }

    @JsonProperty("commitNumber")
    public void setCommitNumber(Long commitNumber) {
        this.commitNumber = commitNumber;
    }

    @JsonProperty("commitUser")
    public String getCommitUser() {
        return commitUser;
    }

    @JsonProperty("commitUser")
    public void setCommitUser(String commitUser) {
        this.commitUser = commitUser;
    }

    @JsonProperty("sequenceNumber")
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    @JsonProperty("sequenceNumber")
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @JsonProperty("entityName")
    public String getEntityName() {
        return entityName;
    }

    @JsonProperty("entityName")
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    @JsonProperty("changeType")
    public String getChangeType() {
        return changeType;
    }

    @JsonProperty("changeType")
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    @JsonProperty("changeFields")
    public String[] getChangeFields() {
        return changeFields;
    }

    @JsonProperty("changeFields")
    public void setChangeFields(String[] changeFields) {
        this.changeFields = changeFields;
    }

    @JsonProperty("changeOrigin")
    public String getChangeOrigin() {
        return changeOrigin;
    }

    @JsonProperty("changeOrigin")
    public void setChangeOrigin(String changeOrigin) {
        this.changeOrigin = changeOrigin;
    }

    @JsonProperty("transactionKey")
    public String getTransactionKey() {
        return transactionKey;
    }

    @JsonProperty("transactionKey")
    public void setTransactionKey(String transactionKey) {
        this.transactionKey = transactionKey;
    }

    @JsonProperty("commitTimestamp")
    public Long getCommitTimestamp() {
        return commitTimestamp;
    }

    @JsonProperty("commitTimestamp")
    public void setCommitTimestamp(Long commitTimestamp) {
        this.commitTimestamp = commitTimestamp;
    }

    @JsonProperty("recordIds")
    public String[] getRecordIds() {
        return recordIds;
    }

    @JsonProperty("recordIds")
    public void setRecordIds(String[] recordIds) {
        this.recordIds = recordIds;
    }

}
