package branch.dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * User: R.Nasiri
 * Date: Feb 24, 2014
 * Time: 10:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class AccountHistory implements Serializable {

    private String customerId;

    private String accountType;

    private String accountNo;

    private int lockStatus;

    private String currency;

    private String accountTitle;

    private int accountStatus;

    private String creationDate;

    private String creationTime;

    private String origCreatDate;

    private String sparrow_branch_id;

    private String origEditDate;

    private String sgb_branch_id;

    private String nature;

    private String accountOpenerName;

    private int withdrawType;

    private String customerIdSrv;

    private int statusSrv;

    private String creationDateSrv;

    private int hostIDSrv;

    private int templateIdSrv;

    private String nationalCode;

    private String tmpUpdateDate;

    private String lastUsageTime;

    private int accountNature;

    private String smsNotification;

    private int statusMelli;

    private String cardNo;

    private long balance;

    private long subsidyAmount;

    private String eStatus;

    private String serviceStatus;

    private String revokeType;

    private String statusD;

    public AccountHistory(String customerId, String accountType, String accountNo, int lockStatus, String currency, String accountTitle,
                          int accountStatus, String creationDate, String creationTime, String origCreatDate, String sparrow_branch_id,
                          String origEditDate, String sgb_branch_id, String nature, String accountOpenerName, int withdrawType,
                          String customerIdSrv, int statusSrv, String creationDateSrv, int hostIDSrv, int templateIdSrv,
                          String nationalCode, String tmpUpdateDate, int accountNature, String smsNotification, int statusMelli) {
        this.customerId = customerId;
        this.accountType = accountType;
        this.accountNo = accountNo;
        this.lockStatus = lockStatus;
        this.currency = currency;
        this.accountTitle = accountTitle;
        this.accountStatus = accountStatus;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.origCreatDate = origCreatDate;
        this.sparrow_branch_id = sparrow_branch_id;
        this.origEditDate = origEditDate;
        this.sgb_branch_id = sgb_branch_id;
        this.nature = nature;
        this.accountOpenerName = accountOpenerName;
        this.withdrawType = withdrawType;
        this.customerIdSrv = customerIdSrv;
        this.statusSrv = statusSrv;
        this.creationDateSrv = creationDateSrv;
        this.hostIDSrv = hostIDSrv;
        this.templateIdSrv = templateIdSrv;
        this.nationalCode = nationalCode;
        this.tmpUpdateDate = tmpUpdateDate;
        this.accountNature = accountNature;
        this.smsNotification = smsNotification;
        this.statusMelli = statusMelli;
    }

    public AccountHistory() {
    }

    public AccountHistory(String customerId, String accountType, String accountNo, int lockStatus, String currency, String accountTitle,
                          int accountStatus, String creationDate, String creationTime, String origCreatDate, String sparrow_branch_id,
                          String origEditDate, String sgb_branch_id, String nature, String accountOpenerName, int withdrawType,
                          String customerIdSrv, int statusSrv, String creationDateSrv, int hostIDSrv, int templateIdSrv,
                          String nationalCode, String tmpUpdateDate, int accountNature, String smsNotification, int statusMelli,String cardNo,
                          long balance,long subsidyAmount,String eStatus,String serviceStatus,String revokeType,String statusD) {
        this.customerId = customerId;
        this.accountType = accountType;
        this.accountNo = accountNo;
        this.lockStatus = lockStatus;
        this.currency = currency;
        this.accountTitle = accountTitle;
        this.accountStatus = accountStatus;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.origCreatDate = origCreatDate;
        this.sparrow_branch_id = sparrow_branch_id;
        this.origEditDate = origEditDate;
        this.sgb_branch_id = sgb_branch_id;
        this.nature = nature;
        this.accountOpenerName = accountOpenerName;
        this.withdrawType = withdrawType;
        this.customerIdSrv = customerIdSrv;
        this.statusSrv = statusSrv;
        this.creationDateSrv = creationDateSrv;
        this.hostIDSrv = hostIDSrv;
        this.templateIdSrv = templateIdSrv;
        this.nationalCode = nationalCode;
        this.tmpUpdateDate = tmpUpdateDate;
        this.accountNature = accountNature;
        this.smsNotification = smsNotification;
        this.statusMelli = statusMelli;
        this.cardNo = cardNo;
        this.balance = balance;
        this.subsidyAmount = subsidyAmount;
        this.eStatus = eStatus;
        this.serviceStatus = serviceStatus;
        this.revokeType = revokeType;
        this.statusD = statusD;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public int getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(int lockStatus) {
        this.lockStatus = lockStatus;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAccountTitle() {
        return accountTitle;
    }

    public void setAccountTitle(String accountTitle) {
        this.accountTitle = accountTitle;
    }

    public int getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        this.accountStatus = accountStatus;
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

    public String getOrigCreatDate() {
        return origCreatDate;
    }

    public void setOrigCreatDate(String origCreatDate) {
        this.origCreatDate = origCreatDate;
    }

    public String getSparrow_branch_id() {
        return sparrow_branch_id;
    }

    public void setSparrow_branch_id(String sparrow_branch_id) {
        this.sparrow_branch_id = sparrow_branch_id;
    }

    public String getOrigEditDate() {
        return origEditDate;
    }

    public void setOrigEditDate(String origEditDate) {
        this.origEditDate = origEditDate;
    }

    public String getSgb_branch_id() {
        return sgb_branch_id;
    }

    public void setSgb_branch_id(String sgb_branch_id) {
        this.sgb_branch_id = sgb_branch_id;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
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

    public String getCustomerIdSrv() {
        return customerIdSrv;
    }

    public void setCustomerIdSrv(String customerIdSrv) {
        this.customerIdSrv = customerIdSrv;
    }

    public int getStatusSrv() {
        return statusSrv;
    }

    public void setStatusSrv(int statusSrv) {
        this.statusSrv = statusSrv;
    }

    public String getCreationDateSrv() {
        return creationDateSrv;
    }

    public void setCreationDateSrv(String creationDateSrv) {
        this.creationDateSrv = creationDateSrv;
    }

    public int getHostIDSrv() {
        return hostIDSrv;
    }

    public void setHostIDSrv(int hostIDSrv) {
        this.hostIDSrv = hostIDSrv;
    }

    public int getTemplateIdSrv() {
        return templateIdSrv;
    }

    public void setTemplateIdSrv(int templateIdSrv) {
        this.templateIdSrv = templateIdSrv;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getTmpUpdateDate() {
        return tmpUpdateDate;
    }

    public void setTmpUpdateDate(String tmpUpdateDate) {
        this.tmpUpdateDate = tmpUpdateDate;
    }

    public String getLastUsageTime() {
        return lastUsageTime;
    }

    public void setLastUsageTime(String lastUsageTime) {
        this.lastUsageTime = lastUsageTime;
    }

    public int getAccountNature() {
        return accountNature;
    }

    public void setAccountNature(int accountNature) {
        this.accountNature = accountNature;
    }

    public String getSmsNotification() {
        return smsNotification;
    }

    public void setSmsNotification(String smsNotification) {
        this.smsNotification = smsNotification;
    }

    public int getStatusMelli() {
        return statusMelli;
    }

    public void setStatusMelli(int statusMelli) {
        this.statusMelli = statusMelli;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getSubsidyAmount() {
        return subsidyAmount;
    }

    public void setSubsidyAmount(long subsidyAmount) {
        this.subsidyAmount = subsidyAmount;
    }

    public String geteStatus() {
        return eStatus;
    }

    public void seteStatus(String eStatus) {
        this.eStatus = eStatus;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getRevokeType() {
        return revokeType;
    }

    public void setRevokeType(String revokeType) {
        this.revokeType = revokeType;
    }

    public String getStatusD() {
        return statusD;
    }

    public void setStatusD(String statusD) {
        this.statusD = statusD;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("accountNo", getAccountNo())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof AccountHistory)) return false;
        AccountHistory castOther = (AccountHistory) other;
        return new EqualsBuilder()
                .append(this.getAccountNo(), castOther.getAccountNo())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getAccountNo())
                .toHashCode();
    }
}
