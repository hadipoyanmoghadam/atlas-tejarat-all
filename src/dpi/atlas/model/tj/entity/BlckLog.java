package dpi.atlas.model.tj.entity;

import dpi.atlas.service.cfs.common.CFSConstants;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class BlckLog implements Serializable {

    private Long logId;
    private Short partNo;
    private Date txDateTime;
    private String sessionId;
    private String txCode;
    private String accountNo;
    private String cardNo ;
    private String amount;
    private String feeAmount;
    private String service;
    private Short blockType;
    private String stan;
    private String rrn;
    private String deviceCode;
    private String origDate;
    private String origTime;
    private char isReversalTxn;
    private int isReversed;


    /**
     * default constructor
     */
    public BlckLog() {
        isReversalTxn = '0';
        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.PARTITION_SIZE;
        try {
            partNo = new Short((short) part);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Short getPartNo() {
        return partNo;
    }

    public void setPartNo(Short partNo) {
        this.partNo = partNo;
    }

    public Date getTxDateTime() {
        return txDateTime;
    }

    public void setTxDateTime(Date txDateTime) {
        this.txDateTime = txDateTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTxCode() {
        return txCode;
    }

    public void setTxCode(String txCode) {
        this.txCode = txCode;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Short getBlockType() {
        return blockType;
    }

    public void setBlockType(Short blockType) {
        this.blockType = blockType;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getOrigDate() {
        return origDate;
    }

    public void setOrigDate(String origDate) {
        this.origDate = origDate;
    }

    public String getOrigTime() {
        return origTime;
    }

    public void setOrigTime(String origTime) {
        this.origTime = origTime;
    }

    public char getIsReversalTxn() {
        return isReversalTxn;
    }

    public void setIsReversalTxn(char reversalTxn) {
        isReversalTxn = reversalTxn;
    }

    public int getIsReversed() {
        return isReversed;
    }

    public void setIsReversed(int reversed) {
        isReversed = reversed;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("logId", getLogId())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof FaultLog)) return false;
        BlckLog castOther = (BlckLog) other;
        return new EqualsBuilder()
                .append(this.getLogId(), castOther.getLogId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getLogId())
                .toHashCode();
    }
}