package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;

import dpi.atlas.service.cfs.common.CFSConstants;


/**
 * @author Hibernate CodeGenerator
 */
public class TxLog implements Serializable {

    /**
     * identifier field
     */
//    private long txLogPk;

    /**
     * nullable persistent field
     */
    private String sessionId;

    /**
     * nullable persistent field
     */
    private String messageId;

    /**
     * nullable persistent field
     */
    private String logTime;

    /**
     * nullable persistent field
     */
    private String logDate;


    /**
     * nullable persistent field
     */
    private String txType;

    /**
     * nullable persistent field
     */
    private String serviceType;

    /**
     * nullable persistent field
     */
    private String actionCode;

    /**
     * nullable persistent field
     */
    private String txString;

    /**
     * nullable persistent field
     */
    private Date txDateTime;

    /**
     * nullable persistent field
     */


    private long batchPk;
    private Integer partNo;

    /**
     * full constructor
     */


    public TxLog(String sessionId, String messageId, String logTime, String logDate, /*Integer direction, */String txType, String serviceType, String actionCode, String txString, Date txDateTime, long batchPk) {
        this.sessionId = sessionId;
        this.messageId = messageId;
        this.logTime = logTime;
        this.logDate = logDate;
        this.txType = txType;
        this.serviceType = serviceType;
        this.actionCode = actionCode;
        this.txString = txString;
        this.txDateTime = txDateTime;
        this.batchPk = batchPk;

        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.PARTITION_SIZE;
        partNo = new Integer(part);
    }

    /**
     * default constructor
     */
    public TxLog() {
        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.PARTITION_SIZE;
        partNo = new Integer(part);
    }

    /**
     * minimal constructor
     */

    public String getSessionId() {
        return this.sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }


    public String getTxType() {
        return this.txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getActionCode() {
        return this.actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getTxString() {
        return this.txString;
    }

    public void setTxString(String txString) {
        this.txString = txString;
    }

    public Date getTxDateTime() {
        return txDateTime;
    }

    public void setTxDateTime(Date txDateTime) {
        this.txDateTime = txDateTime;
    }

    public long getBatchPk() {
        return batchPk;
    }

    public void setBatchPk(long batchPk) {
        this.batchPk = batchPk;
    }

    public Integer getPartNo() {
        return partNo;
    }

    public void setPartNo(Integer partNo) {
        this.partNo = partNo;
    }

    public String toString() {
        return new ToStringBuilder(this)
//                .append("txLogPk", getTxLogPk())
                .append("sessionId", getSessionId())
                .append("messageId", getMessageId())
                .append("txDateTime", getTxDateTime())
                .toString();

    }


//    public boolean equals(Object other) {
//        if ((this == other)) return true;
//        if (!(other instanceof TxLog)) return false;
//        TxLog castOther = (TxLog) other;
//        return new EqualsBuilder()
//                .append(this.getTxLogPk(), castOther.getTxLogPk())
//                .isEquals();
//    }
//
//    public int hashCode() {
//        return new HashCodeBuilder()
//                .append(getTxLogPk())
//                .toHashCode();
//    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TxLog txLog = (TxLog) o;

        if (messageId != null ? !messageId.equals(txLog.messageId) : txLog.messageId != null) return false;
        if (sessionId != null ? !sessionId.equals(txLog.sessionId) : txLog.sessionId != null) return false;
        if (txDateTime != null ? !txDateTime.equals(txLog.txDateTime) : txLog.txDateTime != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (sessionId != null ? sessionId.hashCode() : 0);
        result = 31 * result + (messageId != null ? messageId.hashCode() : 0);
        result = 31 * result + (txDateTime != null ? txDateTime.hashCode() : 0);
        return result;
    }
}