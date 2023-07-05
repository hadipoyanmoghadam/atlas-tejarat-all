package dpi.atlas.model.tj.entity;

import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.IAccount;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


public class Branch implements IAccount, Serializable {

    private String branchCode;
    private int regionCode;
    private String accountNo;
    private String creationDate;
    private String creationTime;

    private String batch_credit_acc;
    private String branch_credit_acc;
    private String center_credit_acc;
    private String e_conflict_acc;
    private String status;
    private String feeAccountNo;
    private String rtgsAccountNo;
    private String achAccountNo;

    public Branch(String branchCode, String accountNo) {
        this.branchCode = branchCode;
        this.accountNo = accountNo;
    }

    public Branch(String branchCode, int regionCode, String accountNo, String creationDate, String creationTime) {
        this.branchCode = branchCode;
        this.regionCode = regionCode;
        this.accountNo = accountNo;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
    }

    public Branch(String branchCode, String branch_credit_acc, String center_credit_acc, String e_conflict_acc) {
        this.branchCode = branchCode;
        this.branch_credit_acc = branch_credit_acc;
        this.center_credit_acc = center_credit_acc;
        this.e_conflict_acc = e_conflict_acc;
    }

    public Branch(String branchCode, String branch_credit_acc, String center_credit_acc, String e_conflict_acc, String accountNo) {
        this.branchCode = branchCode;
        this.branch_credit_acc = branch_credit_acc;
        this.center_credit_acc = center_credit_acc;
        this.e_conflict_acc = e_conflict_acc;
        this.accountNo=accountNo;
    }
    public Branch(String branchCode, String branch_credit_acc, String center_credit_acc, String e_conflict_acc, String accountNo, String batch_credit_acc) {
        this.branchCode = branchCode;
        this.branch_credit_acc = branch_credit_acc;
        this.center_credit_acc = center_credit_acc;
        this.e_conflict_acc = e_conflict_acc;
        this.accountNo=accountNo;
        this.batch_credit_acc=batch_credit_acc;
    }

    public Branch() {
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

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public int getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(int regionCode) {
        this.regionCode = regionCode;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public long getAccountBalance(){return 0;};

    public void setAccountBalance(long accountBalance){};

    public String getBranch_credit_acc() {
        return branch_credit_acc;
    }

    public String getCenter_credit_acc() {
        return center_credit_acc;
    }

    public String getE_conflict_acc() {
        return e_conflict_acc;
    }

    public String getStatus() {
        return status;
    }
    public String getBatch_credit_acc() {
        return batch_credit_acc;
    }

    public String getFeeAccountNo() {
        return feeAccountNo;
    }

    public void setFeeAccountNo(String feeAccountNo) {
        this.feeAccountNo = feeAccountNo;
    }

    public String getRtgsAccountNo() {
        return rtgsAccountNo;
    }

    public void setRtgsAccountNo(String rtgsAccountNo) {
        this.rtgsAccountNo = rtgsAccountNo;
    }

    public String getAchAccountNo() {
        return achAccountNo;
    }

    public void setAchAccountNo(String achAccountNo) {
        this.achAccountNo = achAccountNo;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("branchCode", getBranchCode())
                .append("accountNo", getAccountNo())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Branch)) return false;
        Branch castOther = (Branch) other;
        return new EqualsBuilder()
                .append(this.getBranchCode(), castOther.getBranchCode())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getBranchCode())
                .toHashCode();
    }

    public AccountData getAccountData() {
        return new AccountData(accountNo, AccountData.ACCOUNT_CFS_BRANCH, AccountData.ACCOUNT_BOTH_CREDIT_DEBIT, this);
    }


}
