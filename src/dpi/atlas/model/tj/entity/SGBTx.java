package dpi.atlas.model.tj.entity;

import dpi.atlas.service.cfs.common.CFSConstants;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: Pcd Iran
 * Date: May 6, 2008
 * Time: 5:10:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class SGBTx implements Serializable {
    //related Table name
    public static final String TABLE_NAME = "TBSGBTX";
    //atts
    private String txPK;
    private String sessionId;
    private String txSeq;
    private String txCode;
    private String txSrc;
    private String currency;
    private String srcAccNo;
    private String destAccNo;
    private String deviceCode;
    private String creationDate;
    private String creationTime;
    private String acquirer;
    private String srcBranchId;
    private String txOrigDate;
    private String txOrigTime;
    private String feeAccNo;
    private String SGBActionCode;
    private String SGBBranchId;
    private String isReversalTxn;
    private String origTxPK;        
    private Date txDateTime;
    private String amount;
    private String batchPK;
    private String feeAmount;
    private String srcAccBalance;
    private String partNo;
    private String reversed;
    private String isCutOvered;
    private String description;
    private String EBNK_message_sequence;
    private String fPayCode;
    private String sPayCode;
    private String cbPayId;

    //getters
    public String getTxPK() {
        return txPK;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getTxSeq() {
        return txSeq;
    }

    public String getTxCode() {
        return txCode;
    }

    public String getTxSrc() {
        return txSrc;
    }

    public String getCurrency() {
        return currency;
    }

    public String getSrcAccNo() {
        return srcAccNo;
    }

    public String getDestAccNo() {
        return destAccNo;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public String getAcquirer() {
        return acquirer;
    }

    public String getSrcBranchId() {
        return srcBranchId;
    }

    public String getTxOrigDate() {
        return txOrigDate;
    }

    public String getTxOrigTime() {
        return txOrigTime;
    }

    public String getFeeAccNo() {
        return feeAccNo;
    }

    public String getSGBActionCode() {
        return SGBActionCode;
    }

    public String getSGBBranchId() {
        return SGBBranchId;
    }

    public String getReversalTxn() {
        return isReversalTxn;
    }

    public String getOrigTxPK() {
        return origTxPK;
    }

    public Date getTxDateTime() {
        return txDateTime;
    }

    public String getAmount() {
        return amount;
    }

    public String getBatchPK() {        
        return batchPK;
    }

    public String getFeeAmount() {
        return feeAmount;
    }

    public String getSrcAccBalance() {
        return srcAccBalance;
    }

    public String getPartNo() {
        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.CFSTX_PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.CFSTX_PARTITION_SIZE;
        partNo = new Integer(part).toString();
        return partNo;
    }

    public String getReversed() {
        return reversed;
    }

    public String getCutOvered() {
        return isCutOvered;
    }

    //setters
    public void setTxPK(String txPK) {
        this.txPK = txPK;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setTxSeq(String txSeq) {
        this.txSeq = txSeq;
    }

    public void setTxCode(String txCode) {
        this.txCode = txCode;
    }

    public void setTxSrc(String txSrc) {
        this.txSrc = txSrc;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setSrcAccNo(String srcAccNo) {
        this.srcAccNo = srcAccNo;
    }

    public void setDestAccNo(String destAccNo) {
        this.destAccNo = destAccNo;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }

    public void setSrcBranchId(String srcBranchId) {
        this.srcBranchId = srcBranchId;
    }

    public void setTxOrigDate(String txOrigDate) {
        this.txOrigDate = txOrigDate;
    }

    public void setTxOrigTime(String txOrigTime) {
        this.txOrigTime = txOrigTime;
    }

    public void setFeeAccNo(String feeAccNo) {
        this.feeAccNo = feeAccNo;
    }

    public void setSGBActionCode(String SGBActionCode) {
        this.SGBActionCode = SGBActionCode;
    }

    public void setSGBBranchId(String SGBBranchId) {
        this.SGBBranchId = SGBBranchId;
    }

    public void setReversalTxn(String reversalTxn) {
        isReversalTxn = reversalTxn;
    }

    public void setOrigTxPK(String origTxPK) {
        this.origTxPK = origTxPK;
    }

    public void setTxDateTime(Date txDateTime) {
        this.txDateTime = txDateTime;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setBatchPK(String batchPK) {
        this.batchPK = batchPK;
    }

    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }

    public void setSrcAccBalance(String srcAccBalance) {
        this.srcAccBalance = srcAccBalance;
    }

    public void setPartNo(String partNo) {
        this.partNo = partNo;
    }

    public void setReversed(String reversed) {
        this.reversed = reversed;
    }

    public void setCutOvered(String cutOvered) {
        isCutOvered = cutOvered;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEBNK_message_sequence() {
        return EBNK_message_sequence;
    }

    public void setEBNK_message_sequence(String EBNK_message_sequence) {
        this.EBNK_message_sequence = EBNK_message_sequence;
    }

    public String getfPayCode() {
        return fPayCode;
    }

    public void setfPayCode(String fPayCode) {
        this.fPayCode = fPayCode;
    }

    public String getsPayCode() {
        return sPayCode;
    }

    public void setsPayCode(String sPayCode) {
        this.sPayCode = sPayCode;
    }

    public String getCbPayId() {
        return cbPayId;
    }

    public void setCbPayId(String cbPayId) {
        this.cbPayId = cbPayId;
    }
}
