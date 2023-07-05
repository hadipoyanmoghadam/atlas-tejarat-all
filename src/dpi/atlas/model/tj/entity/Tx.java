package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.Calendar;

import dpi.atlas.service.cfs.common.CFSConstants;

/**
 * @author Hibernate CodeGenerator
 */
public class Tx implements Serializable {
    //Table name
    public static final String TABLE_NAME = "TBCFSTX";  

    //attributes
    private String txPk;
    private String sessionId;
    private String messageId;
    private String txCode;
    private String txSrc;
    private long amount;
    private String currency;
    private String srcAccountNo;
    private String destAccountNo;
    private String totalDestAccNo;
    private String deviceCode;
    private String cardNo;
    private String cardSequenceNo;
    private String txSequenceNumber;
    private String creationDate;
    private String creationTime;
    private String acquirer;
    private String srcBranchId="";
    private Date txDateTime;
    private String txOrigDate;
    private String txOrigTime;
    private int isReversed;
    private int isCutovered;
    private String sgbActionCode;
    private String sgbBranchId;
    private long batchPk;
    private long src_account_balance;
    private long dest_account_balance;
    private String origTxPk = "";
    private char isReversalTxn;
    private int isReversalTx;
    private String description = "";
    private long feeAmount;
    private Integer partNo;
    private String branchDocNo = "";
    private long lastTransLimit;
    private String messageSeq = "";
    private String CRDB = "";
    private String RRN;
    private String merchantTerminalId;
    private String txMessage;
    private String id1;
    private String id2;
    private String fPayCode;
    private String sPayCode;
    private String hostCode;
    private String cardType;
    private String cbPayId;
    private String terminalType;
    private String settlementDate;
    private String extraInfo = "";
    private long srcCardBalance;
    private long destCardBalance;
    private String srcSMSRegister;
    private String destSMSRegister;

    public Tx() {
        isReversalTxn = '0';
        isReversalTx = 0;

        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.CFSTX_PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.CFSTX_PARTITION_SIZE;
        partNo = part;
        id1 = "0";
        sPayCode = "";
        hostCode="";
        cardType="0";
        cardSequenceNo = "";
        terminalType = "";
        settlementDate = "";
        cbPayId = "";
    }


    public String getTxPk() {
        return txPk;
    }

    public void setTxPk(String txPk) {
        this.txPk = txPk;
    }


    public String getCRDB() {
        return CRDB;
    }

    public void setCRDB(String CRDB) {
        this.CRDB = CRDB;
    }

    public String getMessageSeq() {
        return messageSeq;
    }

    public void setMessageSeq(String messageSeq) {
        this.messageSeq = messageSeq;
    }

    public String getSessionId() {
        return sessionId;
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

    public String getTxCode() {
        return txCode;
    }

    public void setTxCode(String txCode) {
        this.txCode = txCode;
    }

    public String getTxSrc() {
        return txSrc;
    }

    public void setTxSrc(String txSrc) {
        this.txSrc = txSrc;
    }

    public long getSrc_account_balance() {
        return src_account_balance;
    }

    public void setSrc_account_balance(long src_account_balance) {
        this.src_account_balance = src_account_balance;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSrcAccountNo() {
        return srcAccountNo;
    }

    public void setSrcAccountNo(String srcAccountNo) {
        this.srcAccountNo = srcAccountNo;
    }

    public String getDestAccountNo() {
        return destAccountNo;
    }

    public void setDestAccountNo(String destAccountNo) {
        this.destAccountNo = destAccountNo;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardSequenceNo() {
        return cardSequenceNo;
    }

    public void setCardSequenceNo(String cardSequenceNo) {
        this.cardSequenceNo = cardSequenceNo;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public String getOrigTxPk() {
        return origTxPk;
    }

    public void setOrigTxPk(String origTxPk) {
        this.origTxPk = origTxPk;
    }

    public char getIsReversalTxn() {
        return isReversalTxn;
    }

    public void setIsReversalTxn(char newIsReversalTxn) {
        isReversalTxn = newIsReversalTxn;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getAcquirer() {
        return acquirer;
    }

    public void setAcquirer(String acquirer) {
        this.acquirer = acquirer;
    }

    public String getSrcBranchId() {
        return srcBranchId;
    }

    public void setSrcBranchId(String srcBranchId) {
        this.srcBranchId = srcBranchId;
    }

    public Date getTxDateTime() {
        return txDateTime;
    }

    public void setTxDateTime(Date txDateTime) {
        this.txDateTime = txDateTime;
    }

    public String getTxOrigDate() {
        return txOrigDate;
    }

    public void setTxOrigDate(String txOrigDate) {
        this.txOrigDate = txOrigDate;
    }

    public String getTxOrigTime() {
        return txOrigTime;
    }

    public void setTxOrigTime(String txOrigTime) {
        this.txOrigTime = txOrigTime;
    }

    public int getIsReversed() {
        return isReversed;
    }

    public void setIsReversed(int reversed) {
        isReversed = reversed;
    }

    public int getIsCutovered() {
        return isCutovered;
    }

    public void setIsCutovered(int cutovered) {
        isCutovered = cutovered;
    }

    public String getSgbActionCode() {
        return sgbActionCode;
    }

    public void setSgbActionCode(String sgbActionCode) {
        this.sgbActionCode = sgbActionCode;
    }

    public String getSgbBranchId() {
        return sgbBranchId;
    }

    public void setSgbBranchId(String sgbBranchId) {
        this.sgbBranchId = sgbBranchId;
    }

    public long getBatchPk() {
        return batchPk;
    }

    public void setBatchPk(long batchPk) {
        this.batchPk = batchPk;
    }

    public String getDescription() {
        return description;
    }

    public long getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(long feeAmount) {
        this.feeAmount = feeAmount;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPartNo() {
        return partNo;
    }

    public void setPartNo(Integer partNo) {
        this.partNo = partNo;
    }


    public long getDest_account_balance() {
        return dest_account_balance;
    }

    public void setDest_account_balance(long dest_account_balance) {
        this.dest_account_balance = dest_account_balance;
    }

    public String getBranchDocNo() {
        return branchDocNo;
    }

    public void setBranchDocNo(String branchDocNo) {
        this.branchDocNo = branchDocNo;
    }


    public long getLastTransLimit() {
        return lastTransLimit;
    }

    public void setLastTransLimit(long lastTransLimit) {
        this.lastTransLimit = lastTransLimit;
    }


    public String getTotalDestAccNo() {
        return totalDestAccNo;
    }

    public void setTotalDestAccNo(String totalDestAccNo) {
        this.totalDestAccNo = totalDestAccNo;
    }

    public int getIsReversalTx() {
        return isReversalTx;
    }

    public void setIsReversalTx(int isReversalTx) {
        this.isReversalTx = isReversalTx;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("txPk", getTxPk()).append("CardNo", getCardNo()).append("DestAccountNo", getDestAccountNo()).append("MessageSeq", getMessageSeq()).append("SessionId", getSessionId()).append("SrcAccountNo", getSrcAccountNo()).append("TxSequenceNumber", getTxSequenceNumber())
                .toString();
    }


    public String getTxSequenceNumber() {
        return txSequenceNumber;
    }

    public void setTxSequenceNumber(String txSequenceNumber) {
        this.txSequenceNumber = txSequenceNumber;
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Tx)) return false;
        Tx castOther = (Tx) other;
        return new EqualsBuilder()
                .append(this.getTxPk(), castOther.getTxPk())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getTxPk())
                .toHashCode();
    }

    public void setRRN(String RRN) {
        this.RRN = RRN;
    }


    public String getRRN() {
        return RRN;
    }


    public String getMerchantTerminalId() {
        return merchantTerminalId;
    }

    public void setMerchantTerminalId(String merchantTerminalId) {
        this.merchantTerminalId = merchantTerminalId;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }


    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getTxMessage() {
        return txMessage;
    }

    public void setTxMessage(String txMessage) {
        this.txMessage = txMessage;
    }

//    public String getfPayCode() {
//        return fPayCode;
//    }

    public void setfPayCode(String fPayCode) {
//        this.fPayCode = fPayCode;
        this.id1 = fPayCode;
    }

    public String getsPayCode() {
        return sPayCode;
    }

    public void setsPayCode(String sPayCode) {
        this.sPayCode = sPayCode;
    }

    public String getHostCode() {
        return hostCode;
    }

    public void setHostCode(String hostCode) {
        this.hostCode = hostCode;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCbPayId() {
        return cbPayId;
    }

    public void setCbPayId(String cbPayId) {
        this.cbPayId = cbPayId;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public String getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public long getSrcCardBalance() {
        return srcCardBalance;
    }

    public void setSrcCardBalance(long srcCardBalance) {
        this.srcCardBalance = srcCardBalance;
    }

    public long getDestCardBalance() {
        return destCardBalance;
    }

    public void setDestCardBalance(long destCardBalance) {
        this.destCardBalance = destCardBalance;
    }

    public String getSrcSMSRegister() {
        return srcSMSRegister;
    }

    public void setSrcSMSRegister(String srcSMSRegister) {
        this.srcSMSRegister = srcSMSRegister;
    }

    public String getDestSMSRegister() {
        return destSMSRegister;
    }

    public void setDestSMSRegister(String destSMSRegister) {
        this.destSMSRegister = destSMSRegister;
    }
}
