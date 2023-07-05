package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;


public class CustomerServiceNew implements Serializable {
    private String customerId;
    private String accountNo;
    private int hostId;
    private int status;
    private long templateId;
    private String pin;
    private String tPin;
    private long lang;
    private String nationalCode;
    private String creationDate;
    private String tmpUpdateDate;
    private String accountGroup;
    private int accountNature;
    private String smsNotification;
    private byte statusMelli;
    private String eStatus;
    private String serviceStatus;
    private String statusD;


    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public boolean isLocked() {
        return (status != 1);
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountGroup() {
        return accountGroup;
    }

    public void setAccountGroup(String accountGroup) {
        this.accountGroup = accountGroup;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
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

    public long getLang() {
        return lang;
    }

    public void setLang(long lang) {
        this.lang = lang;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getTmpUpdateDate() {
        return tmpUpdateDate;
    }

    public void setTmpUpdateDate(String tmpUpdateDate) {
        this.tmpUpdateDate = tmpUpdateDate;
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

    public byte getStatusMelli() {
        return statusMelli;
    }

    public void setStatusMelli(byte statusMelli) {
        this.statusMelli = statusMelli;
    }

    public String geteStatus() {
        return eStatus;
    }

    public void seteStatus(String eStatus) {
        this.eStatus = eStatus;
    }

    public String getStatusD() {
        return statusD;
    }

    public void setStatusD(String statusD) {
        this.statusD = statusD;
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CustomerServiceNew)) return false;
        CustomerServiceNew castOther = (CustomerServiceNew) other;
        return new EqualsBuilder()
                .append(this.getAccountNo(), castOther.getAccountNo())
                .append(this.getHostId(), castOther.getHostId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getAccountNo())
                .append(getHostId())
                .toHashCode();
    }
}
