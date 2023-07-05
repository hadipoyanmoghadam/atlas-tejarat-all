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
public class Device implements IAccount, Serializable {

    /** identifier field */
//    private Long devicePk;

    /**
     * nullable persistent field
     */
    private String iin;

    /**
     * nullable persistent field
     */
    private String branchId;

    /**
     * nullable persistent field
     */
    private String deviceCode;

    /**
     * nullable persistent field
     */
    private int deviceType;

    /**
     * nullable persistent field
     */
    private String accountNo;

    /**
     * nullable persistent field
     */
    private long accountBalance;

    /**
     * nullable persistent field
     */
    private String creationDate;

    /**
     * nullable persistent field
     */
    private String creationTime;

    /** persistent field */
//1386/04/03 Boroon commented to minimize db access - START
//    private dpi.atlas.model.tj.entity.Branch branch;
//1386/04/03 Boroon commented to minimize db access - END

    /** persistent field */
//    private dpi.atlas.model.tj.entity.Account account;

    /**
     * full constructor
     */
//1386/04/03 Boroon changed to minimize db access - START
//    public Device(String iin, String branchId, String deviceCode, int deviceType, String accountNo, long accountBalance, String creationDate, String creationTime, dpi.atlas.model.tj.entity.Branch branch/*, dpi.atlas.model.tj.entity.Account account*/) {
    public Device(String iin, String branchId, String deviceCode, int deviceType, String accountNo, long accountBalance, String creationDate, String creationTime/*, dpi.atlas.model.tj.entity.Branch branch, dpi.atlas.model.tj.entity.Account account*/) {
//1386/04/03 Boroon changed to minimize db access - END
        this.iin = iin;
        this.branchId = branchId;
        this.deviceCode = deviceCode;
        this.deviceType = deviceType;
        this.accountNo = accountNo;
        this.accountBalance = accountBalance;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
//1386/04/03 Boroon commented to minimize db access - START
//        this.branch = branch;
//1386/04/03 Boroon commented to minimize db access - END        
//        this.account = account;
    }

    /**
     * default constructor
     */
    public Device() {
    }

    /**
     * minimal constructor
     */
//1386/04/03 Boroon commented to minimize db access - START
//    public Device(dpi.atlas.model.tj.entity.Branch branch/*, dpi.atlas.model.tj.entity.Account account*/) {
//        this.branch = branch;
////        this.account = account;
//    }
//1386/04/03 Boroon commented to minimize db access - END    
    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public int getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public long getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(long accountBalance) {
        this.accountBalance = accountBalance;
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

//1386/04/03 Boroon commented to minimize db access - START
//    public dpi.atlas.model.tj.entity.Branch getBranch() {
//        return this.branch;
//    }
//
//    public void setBranch(dpi.atlas.model.tj.entity.Branch branch) {
//        this.branch = branch;
//    }
//1386/04/03 Boroon commented to minimize db access - END    

//    public dpi.atlas.model.tj.entity.Account getAccount() {
//        return this.account;
//    }
//
//    public void setAccount(dpi.atlas.model.tj.entity.Account account) {
//        this.account = account;
//    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("iin", getIin())
                .append("deviceCode", getDeviceCode())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Device)) return false;
        Device castOther = (Device) other;
        return new EqualsBuilder()
                .append(this.getIin(), castOther.getIin())
                .append(this.getDeviceCode(), castOther.getDeviceCode())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getIin())
                .append(getDeviceCode())
                .toHashCode();
    }

    public AccountData getAccountData() {
        return new AccountData(accountNo, AccountData.ACCOUNT_CFS_DEVICE, AccountData.ACCOUNT_BOTH_CREDIT_DEBIT, this);
    }


}
