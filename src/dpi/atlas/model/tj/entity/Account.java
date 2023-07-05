package dpi.atlas.model.tj.entity;

import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.IAccount;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * @author Hibernate CodeGenerator
 */
public class Account implements IAccount, Serializable {

    /** identifier field */
//    private Long accountPk;

    /**
     * nullable persistent field
     */
    private String customerId;

    private String sgb_branch_id;

    private String sparrow_branch_id;

    /**
     * nullable persistent field
     */
    private String accountNo;

    /**
     * nullable persistent field
     */
    private String accountType;

    /**
     * nullable persistent field
     */
    private int accountSrc;

    /**
     * nullable persistent field
     */
    private long accountStatus;

    /**
     * nullable persistent field
     */
    private long accountBalance;

    private long totalBlockedAmount;

    private int hostID;

    /** nullable persistent field */
//    private Long merchantId;

    /**
     * nullable persistent field
     */
    private String creationDate;

    /**
     * nullable persistent field
     */
    private String creationTime;

    private int lockStatus;

    private String eStatus;

    private String accountTitle;

    private String currency;

    private String accountOpenerName;

    private int withdrawType;
    /**
     * full constructor
     */
    public Account(String accountNo, String customerId, String accountType, int accountSrc, long accountStatus, long accountBalance, /*long merchantId, */String creationDate, String creationTime,/*, dpi.atlas.model.tj.entity.Branch branch, Set customerAccounts, Set devices, Set imdsByChargeAccountPk, Set imdsByAccountPk, Set cardAccounts, dpi.atlas.model.tj.entity.Customer customer*/String sgb_branch_id, String sparrow_branch_id, int hostID) {
        this.accountNo = accountNo;
        this.customerId = customerId;
        this.accountType = accountType;
        this.accountSrc = accountSrc;
        this.accountStatus = accountStatus;
        this.accountBalance = accountBalance;
//        this.merchantId = merchantId;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.sparrow_branch_id = sparrow_branch_id;
        this.sgb_branch_id = sgb_branch_id;
        this.hostID = hostID;
//        this.branch = branch;
//        this.customerAccounts = customerAccounts;
//        this.devices = devices;
//        this.imdsByChargeAccountPk = imdsByChargeAccountPk;
//        this.imdsByAccountPk = imdsByAccountPk;
//        this.cardAccounts = cardAccounts;
//1386/04/03 Boroon commented to minimize db access - START
//        this.customer = customer;
//1386/04/03 Boroon commented to minimize db access - END
    }

    public Account(String accountNo, String customerId, String accountType, int accountSrc, long accountStatus, long accountBalance, /*long merchantId, */String creationDate, String creationTime,/*, dpi.atlas.model.tj.entity.Branch branch, Set customerAccounts, Set devices, Set imdsByChargeAccountPk, Set imdsByAccountPk, Set cardAccounts, dpi.atlas.model.tj.entity.Customer customer*/String sgb_branch_id, String sparrow_branch_id, int hostID, long totalBlockedAmount) {
        this.accountNo = accountNo;
        this.customerId = customerId;
        this.accountType = accountType;
        this.accountSrc = accountSrc;
        this.accountStatus = accountStatus;
        this.accountBalance = accountBalance;
//        this.merchantId = merchantId;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.sparrow_branch_id = sparrow_branch_id;
        this.sgb_branch_id = sgb_branch_id;
        this.hostID = hostID;
        this.totalBlockedAmount = totalBlockedAmount;
        this.accountTitle="0";
    }

    public Account(String accountNo, String customerId, String accountType, int accountSrc, long accountStatus, long accountBalance, /*long merchantId, */String creationDate, String creationTime,String sgb_branch_id, String sparrow_branch_id, int hostID, long totalBlockedAmount,String accountTitle,String currency,String accountOpenerName,int withdrawType) {
        this.accountNo = accountNo;
        this.customerId = customerId;
        this.accountType = accountType;
        this.accountSrc = accountSrc;
        this.accountStatus = accountStatus;
        this.accountBalance = accountBalance;
//        this.merchantId = merchantId;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.sparrow_branch_id = sparrow_branch_id;
        this.sgb_branch_id = sgb_branch_id;
        this.hostID = hostID;
        this.totalBlockedAmount = totalBlockedAmount;
        this.accountTitle=accountTitle;
        this.currency=currency;
        this.accountOpenerName=accountOpenerName;
        this.withdrawType=withdrawType;
    }

    /**
     * default constructor
     */
    public Account() {
    }

    /**
     * minimal constructor
     */
//1386/04/03 Boroon commented to minimize db access - START
//    public Account(/*dpi.atlas.model.tj.entity.Branch branch, Set customerAccounts, Set devices, Set imdsByChargeAccountPk, Set imdsByAccountPk, Set cardAccounts*/ dpi.atlas.model.tj.entity.Customer customer) {
////        this.branch = branch;
////        this.customerAccounts = customerAccounts;
////        this.devices = devices;
////        this.imdsByChargeAccountPk = imdsByChargeAccountPk;
////        this.imdsByAccountPk = imdsByAccountPk;
////        this.cardAccounts = cardAccounts;
//        this.customer = customer;
//    }
//1386/04/03 Boroon commented to minimize db access - END

//    public long getAccountPk() {
//        return this.accountPk;
//    }
//
//    public void setAccountPk(long accountPk) {
//        this.accountPk = accountPk;
//    }
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountNo() {
        return this.accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public int getAccountSrc() {
        return this.accountSrc;
    }

    public void setAccountSrc(int accountSrc) {
        this.accountSrc = accountSrc;
    }

    public long getAccountStatus() {
        return this.accountStatus;
    }

    public void setAccountStatus(long accountStatus) {
        this.accountStatus = accountStatus;
    }

    public long getAccountBalance() {
        return this.accountBalance;
    }

    public void setAccountBalance(long accountBalance) {
        this.accountBalance = accountBalance;
    }

//    public long getMerchantId() {
//        return this.merchantId;
//    }
//
//    public void setMerchantId(long merchantId) {
//        this.merchantId = merchantId;
//    }

    public int getHostID() {
        return hostID;
    }

    public void setHostID(int hostID) {
        this.hostID = hostID;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getSgb_branch_id() {
        return sgb_branch_id;
    }

    public void setSgb_branch_id(String sgb_branch_id) {
        this.sgb_branch_id = sgb_branch_id;
    }

    public String getSparrow_branch_id() {
        return sparrow_branch_id;
    }

    public void setSparrow_branch_id(String sparrow_branch_id) {
        this.sparrow_branch_id = sparrow_branch_id;
    }

//    public dpi.atlas.model.tj.entity.Branch getBranch() {
//        return this.branch;
//    }
//
//    public void setBranch(dpi.atlas.model.tj.entity.Branch branch) {
//        this.branch = branch;
//    }

//    public Set getCustomerAccounts() {
//        return this.customerAccounts;
//    }
//
//    public void setCustomerAccounts(Set customerAccounts) {
//        this.customerAccounts = customerAccounts;
//    }

//    public Set getDevices() {
//        return this.devices;
//    }
//
//    public void setDevices(Set devices) {
//        this.devices = devices;
//    }

//    public Set getImdsByChargeAccountPk() {
//        return this.imdsByChargeAccountPk;
//    }
//
//    public void setImdsByChargeAccountPk(Set imdsByChargeAccountPk) {
//        this.imdsByChargeAccountPk = imdsByChargeAccountPk;
//    }
//
//    public Set getImdsByAccountPk() {
//        return this.imdsByAccountPk;
//    }
//
//    public void setImdsByAccountPk(Set imdsByAccountPk) {
//        this.imdsByAccountPk = imdsByAccountPk;
//    }

//    public Set getCardAccounts() {
//        return this.cardAccounts;
//    }
//
//    public void setCardAccounts(Set cardAccounts) {
//        this.cardAccounts = cardAccounts;
//    }

    public int getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(int lockStatus) {
        this.lockStatus = lockStatus;
    }

    public boolean isLocked() {
        return (lockStatus == 9);
    }


    public String geteStatus() {
        return eStatus;
    }

    public void seteStatus(String eStatus) {
        this.eStatus = eStatus;
    }

    public String getAccountTitle() {
        return accountTitle;
    }

    public void setAccountTitle(String accountTitle) {
        this.accountTitle = accountTitle;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccountOpenerName() {
        return accountOpenerName;
    }

    public void setAccountOpenerName(String accountOpenerName) {
        this.accountOpenerName = accountOpenerName;
    }

    public int getWithdrawType() {
        return withdrawType;
    }

    public void setWithdrawType(int withdrawType) {
        this.withdrawType = withdrawType;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("customerId", getCustomerId())
                .append("accountType", getAccountType())
                .append("accountNo", getAccountNo())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Account)) return false;
        Account castOther = (Account) other;
        return new EqualsBuilder()
                .append(this.getCustomerId(), castOther.getCustomerId())
                .append(this.getAccountType(), castOther.getAccountType())
                .append(this.getAccountNo(), castOther.getAccountNo())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getCustomerId())
                .append(getAccountType())
                .append(getAccountNo())
                .toHashCode();
    }

    public dpi.atlas.service.cfs.common.AccountData getAccountData() {
//        return new dpi.atlas.service.cfs.common.AccountData(accountNo, AccountData.ACCOUNT_CFS_CUSTOMER, dpi.atlas.service.cfs.common.AccountData.ACCOUNT_BOTH_CREDIT_DEBIT, getAccountBalance(), this);
        int isCreditorDebit;
        if (getAccountBalance() >= 0)
            isCreditorDebit = dpi.atlas.service.cfs.common.AccountData.ACCOUNT_CREDIT;
        else
            isCreditorDebit = dpi.atlas.service.cfs.common.AccountData.ACCOUNT_DEBIT;

        return new dpi.atlas.service.cfs.common.AccountData(accountNo, AccountData.ACCOUNT_CFS_CUSTOMER, isCreditorDebit , getAccountBalance(), accountType, totalBlockedAmount,this);
    }
}
