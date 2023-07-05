package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Sep 18, 2006
 * Time: 12:24:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerInfo {
    private String customerId;
    private String firstName;
    private String lastName;
    private String nid;
    private Long bplace;
    private String address1;
    private String address2;
    private String homePhone;
    private String officePhone;
    private String creationDate;
    private String creationTime;
    private int templateID;
    private String ibCountry;
    private Long ibStyle;

    private long serviceId;
    private String pin;
    private String tPin;
    private long ibLang;

    private String accountNo;
    private String accountType;
    private int accountSrc;
    private long accountStatus;
    private long accountBalance;
    private int hostID;
    private String creationDateAccount;
    private String creationTimeAccount;
    private int lockStatus;

    /**
     * full constructor
     */
    public CustomerInfo(Customer customer, CustomerService customerService, Account account) {
        customerId = customer.getCustomerId();
        firstName = customer.getFirstName();
        lastName = customer.getLastName();
        nid = customer.getNid();
        bplace = customer.getBplace();
        address1 = customer.getAddress1();
        address2 = customer.getAddress2();
        homePhone = customer.getHomePhone();
        officePhone = customer.getOfficePhone();
        creationDate = customer.getCreationDate();
        creationTime = customer.getCreationTime();
        templateID = customer.getTemplateID();
        ibCountry = customer.getIbCountry();
        ibStyle = customer.getIbStyle();
        serviceId = customerService.getServiceId();
        pin = customerService.getPin();
        tPin = customerService.gettPin();
        ibLang = customerService.getLang();

        accountNo = account.getAccountNo();
        accountType = account.getAccountType();
        accountSrc = account.getAccountSrc();
        accountStatus = account.getAccountStatus();
        accountBalance = account.getAccountBalance();
        hostID = account.getHostID();
        creationDateAccount = account.getCreationDate();
        creationTimeAccount = account.getCreationTime();
        lockStatus = account.getLockStatus();
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public Long getBplace() {
        return bplace;
    }

    public void setBplace(Long bplace) {
        this.bplace = bplace;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
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

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public String getIbCountry() {
        return ibCountry;
    }

    public void setIbCountry(String ibCountry) {
        this.ibCountry = ibCountry;
    }

    public Long getIbStyle() {
        return ibStyle;
    }

    public void setIbStyle(Long ibStyle) {
        this.ibStyle = ibStyle;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String gettPin() {
        return tPin;
    }

    public void settPin(String tPin) {
        this.tPin = tPin;
    }

    public long getIbLang() {
        return ibLang;
    }

    public void setIbLang(long ibLang) {
        this.ibLang = ibLang;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public int getAccountSrc() {
        return accountSrc;
    }

    public void setAccountSrc(int accountSrc) {
        this.accountSrc = accountSrc;
    }

    public long getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(long accountStatus) {
        this.accountStatus = accountStatus;
    }

    public long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(long accountBalance) {
        this.accountBalance = accountBalance;
    }

    public int getHostID() {
        return hostID;
    }

    public void setHostID(int hostID) {
        this.hostID = hostID;
    }

    public String getCreationDateAccount() {
        return creationDateAccount;
    }

    public void setCreationDateAccount(String creationDateAccount) {
        this.creationDateAccount = creationDateAccount;
    }

    public String getCreationTimeAccount() {
        return creationTimeAccount;
    }

    public void setCreationTimeAccount(String creationTimeAccount) {
        this.creationTimeAccount = creationTimeAccount;
    }

    public int getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(int lockStatus) {
        this.lockStatus = lockStatus;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("customerId", getCustomerId())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CustomerInfo)) return false;
        CustomerInfo castOther = (CustomerInfo) other;
        return new EqualsBuilder()
                .append(this.getCustomerId(), castOther.getCustomerId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getCustomerId())
                .toHashCode();
    }
}
