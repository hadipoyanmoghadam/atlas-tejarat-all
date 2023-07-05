package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

import java.io.Serializable;
import java.util.Calendar;

import dpi.atlas.service.cfs.common.CFSConstants;


/**
 * @author Hibernate CodeGenerator
 */
public class SGBLog implements Serializable {

    /**
     * identifier field
     */
    private Long logId;

    /**
     * identifier field
     */
    private String accountGroup;

    /**
     * identifier field
     */
    private String bankCode;

    /**
     * identifier field
     */
    private String accountNo;

    /**
     * identifier field
     */
    private String debitCredit;

    /**
     * identifier field
     */
    private String amount;

    /**
     * identifier field
     */
    private String effectiveDate;

    /**
     * identifier field
     */
    private String operationCode;

    /**
     * identifier field
     */
    private String documentNo;

    /**
     * identifier field
     */
    private String branchCode;

    /**
     * identifier field
     */
    private String journalNo;

    /**
     * identifier field
     */
    private String accountBranchCode;

    /**
     * identifier field
     */
    private String time;

    /**
     * identifier field
     */
    private String terminalAccount;

    private String cardNo ;

    /**
     * nullable persistent field
     */
    private String creationDate;

    /**
     * nullable persistent field
     */
    private String creationTime;

    /**
     * nullable persistent field
     */
    private String sgbFileDate;

    /**
     * nullable persistent field
     */
    private String processStatus;

    /**
     * persistent field
     */
    private long sgb_Batch_pk ;

    private dpi.atlas.model.tj.entity.SGBBatch sgbBatch;

    private Short partNo;

    /**
     * full constructor
     */
    public SGBLog(String accountGroup, String bankCode, String accountNo, String debitCredit, String amount,
                  String effectiveDate, String operationCode, String documentNo, String branchCode, String journalNo,
                  String accountBranchCode, String time, String terminalAccount, String creationDate, String creationTime,
                  String sgbFileDate, String processStatus, SGBBatch sgbBatch) {
        this.accountGroup = accountGroup;
        this.bankCode = bankCode;
        this.accountNo = accountNo;
        this.debitCredit = debitCredit;
        this.amount = amount;
        this.effectiveDate = effectiveDate;
        this.operationCode = operationCode;
        this.documentNo = documentNo;
        this.branchCode = branchCode;
        this.journalNo = journalNo;
        this.accountBranchCode = accountBranchCode;
        this.time = time;
        this.terminalAccount = terminalAccount;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.sgbFileDate = sgbFileDate;
        this.processStatus = processStatus;
        this.sgbBatch = sgbBatch;
        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.PARTITION_SIZE;
        try {
            partNo = new Short((short) part);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public SGBLog(String accountGroup, String bankCode, String accountNo, String debitCredit, String amount,
                  String effectiveDate, String operationCode, String documentNo, String branchCode, String journalNo,
                  String accountBranchCode, String time, String terminalAccount, String cardNo, String creationDate, String creationTime, long logId,
                  String sgbFileDate, String processStatus, long sgb_Batch_pk) {


        this.accountGroup = accountGroup;
        this.bankCode = bankCode;
        this.accountNo = accountNo;
        this.debitCredit = debitCredit;
        this.amount = amount;
        this.effectiveDate = effectiveDate;
        this.operationCode = operationCode;
        this.documentNo = documentNo;
        this.branchCode = branchCode;
        this.journalNo = journalNo;
        this.accountBranchCode = accountBranchCode;
        this.time = time;
        this.terminalAccount = terminalAccount;
        this.cardNo = cardNo;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.logId = new Long(logId);
        this.sgbFileDate = sgbFileDate;
        this.processStatus = processStatus;
        this.sgb_Batch_pk = sgb_Batch_pk;
    }

    /**
     * default constructor
     */
    public SGBLog() {
        int part = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % CFSConstants.PARTITION_SIZE;
        if (part == 0)
            part = CFSConstants.PARTITION_SIZE;
        try {
            partNo = new Short((short) part);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    /**
     * minimal constructor
     */
    public SGBLog(SGBBatch sgbBatch) {
        this.sgbBatch = sgbBatch;
    }


    public Short getPartNo() {
        return partNo;
    }

    public void setPartNo(Short partNo) {
        this.partNo = partNo;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getAccountGroup() {
        return accountGroup;
    }

    public void setAccountGroup(String accountGroup) {
        this.accountGroup = accountGroup;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getDebitCredit() {
        return debitCredit;
    }

    public void setDebitCredit(String debitCredit) {
        this.debitCredit = debitCredit;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getJournalNo() {
        return journalNo;
    }

    public void setJournalNo(String journalNo) {
        this.journalNo = journalNo;
    }

    public String getAccountBranchCode() {
        return accountBranchCode;
    }

    public void setAccountBranchCode(String accountBranchCode) {
        this.accountBranchCode = accountBranchCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTerminalAccount() {
        return terminalAccount;
    }

    public void setTerminalAccount(String terminalAccount) {
        this.terminalAccount = terminalAccount;
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

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getSgbFileDate() {
        return sgbFileDate;
    }

    public void setSgbFileDate(String sgbFileDate) {
        this.sgbFileDate = sgbFileDate;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public SGBBatch getSgbBatch() {
        return sgbBatch;
    }

    public void setSgbBatch(SGBBatch sgbBatch) {
        this.sgbBatch = sgbBatch;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("logId", getLogId())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof dpi.atlas.model.tj.entity.FaultLog)) return false;
        SGBLog castOther = (SGBLog) other;
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