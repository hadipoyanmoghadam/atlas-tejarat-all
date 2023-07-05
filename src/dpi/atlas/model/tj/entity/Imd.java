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
public class Imd implements Serializable, IAccount {

    private String imdPk;

    private String imdName;

    private String debitAccountNo;

    private String creditAccountNo;

    private Long transBaseFee;

    private Long transMinFee;

    private Long transMaxFee;

    private Long transPercentageFee;

    private Long loroBaseFee;

    private Long loroMinFee;

    private Long loroMaxFee;

    private Long loroPercentageFee;

    private String creationDate;

    private String creationTime;

    private String feedebitAccountNo;

    private String issuerBranch;

    private Long nftWageFee;

    private Float nftWagePercentage;

    private Long nftPgWageFee;

    private Float nftPgWagePercentage;

    private Long nbalWageFee;

    private Float nbalWagePercentage;

    private Long nftPosWageFee;

    private Float nftPosWagePercentage;

    private Long nftMpWageFee;

    private Float nftMpWagePercentage;

    private Long nftKioskWageFee;

    private Float nftKioskWagePercentage;

    private Long scshrPbWageFee;

    private Long nbalPosWageFee;
    private Long nbalPBWageFee;
    private Long nbalKSWageFee;
    private Long nbalMPWageFee;
    private Long nbalSIWageFee;
    private Long nbalPGWageFee;



        /**
     * full constructor
     */
    public Imd(String imdPk, String imdName, String debitAccountNo, Long transBaseFee, Long transMinFee, Long transMaxFee, Long transPercentageFee, Long loroBaseFee, Long loroMinFee, Long loroMaxFee, Long loroPercentageFee, String creationDate, String creationTime, String accountNo, String chargeAccountNo/*, dpi.atlas.model.tj.entity.Account accountByChargeAccountPk, dpi.atlas.model.tj.entity.Account accountByAccountPk*/) {
        this.imdPk = imdPk;
        this.imdName = imdName;
        this.debitAccountNo = debitAccountNo;
        this.transBaseFee = transBaseFee;
        this.transMinFee = transMinFee;
        this.transMaxFee = transMaxFee;
        this.transPercentageFee = transPercentageFee;
        this.loroBaseFee = loroBaseFee;
        this.loroMinFee = loroMinFee;
        this.loroMaxFee = loroMaxFee;
        this.loroPercentageFee = loroPercentageFee;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.creditAccountNo = accountNo;
        this.feedebitAccountNo = chargeAccountNo;
    }

    /**
     * default constructor
     */
    public Imd() {
    }

    public String getIssuerBranch() {
        return issuerBranch;
    }

    public void setIssuerBranch(String issuerBranch) {
        this.issuerBranch = issuerBranch;
    }




    public String getImdPk() {
        return this.imdPk;
    }

    public void setImdPk(String imdPk) {
        this.imdPk = imdPk;
    }

    public String getImdName() {
        return this.imdName;
    }

    public void setImdName(String imdName) {
        this.imdName = imdName;
    }

    public String getDebitAccountNo() {
        return debitAccountNo;
    }

    public void setDebitAccountNo(String debitAccountNo) {
        this.debitAccountNo = debitAccountNo;
    }

    public Long getTransBaseFee() {
        return this.transBaseFee;
    }

    public void setTransBaseFee(Long transBaseFee) {
        this.transBaseFee = transBaseFee;
    }

    public Long getTransMinFee() {
        return this.transMinFee;
    }

    public void setTransMinFee(Long transMinFee) {
        this.transMinFee = transMinFee;
    }

    public Long getTransMaxFee() {
        return this.transMaxFee;
    }

    public void setTransMaxFee(Long transMaxFee) {
        this.transMaxFee = transMaxFee;
    }

    public Long getTransPercentageFee() {
        return this.transPercentageFee;
    }

    public void setTransPercentageFee(Long transPercentageFee) {
        this.transPercentageFee = transPercentageFee;
    }

    public Long getLoroBaseFee() {
        return this.loroBaseFee;
    }

    public void setLoroBaseFee(Long loroBaseFee) {
        this.loroBaseFee = loroBaseFee;
    }

    public Long getLoroMinFee() {
        return this.loroMinFee;
    }

    public void setLoroMinFee(Long loroMinFee) {
        this.loroMinFee = loroMinFee;
    }

    public Long getLoroMaxFee() {
        return this.loroMaxFee;
    }

    public void setLoroMaxFee(Long loroMaxFee) {
        this.loroMaxFee = loroMaxFee;
    }

    public Long getLoroPercentageFee() {
        return this.loroPercentageFee;
    }

    public void setLoroPercentageFee(Long loroPercentageFee) {
        this.loroPercentageFee = loroPercentageFee;
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

    public String getCreditAccountNo() {
        return creditAccountNo;
    }

    public void setCreditAccountNo(String creditAccountNo) {
        this.creditAccountNo = creditAccountNo;
    }

    public String getFeedebitAccountNo() {
        return feedebitAccountNo;
    }

    public void setFeedebitAccountNo(String feedebitAccountNo) {
        this.feedebitAccountNo = feedebitAccountNo;
    }

    public Long getNftWageFee() {
        return nftWageFee;
    }

    public void setNftWageFee(Long nftWageFee) {
        this.nftWageFee = nftWageFee;
    }


    public Long getNftPgWageFee() {
        return nftPgWageFee;
    }


    public Long getNftPosWageFee() {
        return nftPosWageFee;
    }

    public void setNftPosWageFee(Long nftPosWageFee) {
        this.nftPosWageFee = nftPosWageFee;
    }

    public Float getNftPosWagePercentage() {
        return nftPosWagePercentage;
    }

    public void setNftPosWagePercentage(Float nftPosWagePercentage) {
        this.nftPosWagePercentage = nftPosWagePercentage;
    }

    public void setNftPgWageFee(Long nftPgWageFee) {
        this.nftPgWageFee = nftPgWageFee;
    }

    public Float getNftPgWagePercentage() {
        return nftPgWagePercentage;
    }

    public void setNftPgWagePercentage(Float nftPgWagePercentage) {
        this.nftPgWagePercentage = nftPgWagePercentage;
    }

    public Long getNftKioskWageFee() {
        return nftKioskWageFee;
    }

    public void setNftKioskWageFee(Long nftKioskWageFee) {
        this.nftKioskWageFee = nftKioskWageFee;
    }

    public Float getNftKioskWagePercentage() {
        return nftKioskWagePercentage;
    }

    public void setNftKioskWagePercentage(Float nftKioskWagePercentage) {
        this.nftKioskWagePercentage = nftKioskWagePercentage;
    }

    public Long getNftMpWageFee() {
        return nftMpWageFee;
    }

    public void setNftMpWageFee(Long nftMpWageFee) {
        this.nftMpWageFee = nftMpWageFee;
    }

    public Float getNftMpWagePercentage() {
        return nftMpWagePercentage;
    }

    public void setNftMpWagePercentage(Float nftMpWagePercentage) {
        this.nftMpWagePercentage = nftMpWagePercentage;
    }

    public Float getNftWagePercentage() {
        return nftWagePercentage;
    }

    public void setNftWagePercentage(Float nftWagePercentage) {
        this.nftWagePercentage = nftWagePercentage;
    }

    public Long getNbalWageFee() {
        return nbalWageFee;
    }

    public void setNbalWageFee(Long nbalWageFee) {
        this.nbalWageFee = nbalWageFee;
    }

    public Float getNbalWagePercentage() {
        return nbalWagePercentage;
    }

    public void setNbalWagePercentage(Float nbalWagePercentage) {
        this.nbalWagePercentage = nbalWagePercentage;
    }

    public Long getScshrPbWageFee() {
        return scshrPbWageFee;
    }

    public void setScshrPbWageFee(Long scshrPbWageFee) {
        this.scshrPbWageFee = scshrPbWageFee;
    }

    public Long getNbalPosWageFee() {
        return nbalPosWageFee;
    }

    public void setNbalPosWageFee(Long nbalPosWageFee) {
        this.nbalPosWageFee = nbalPosWageFee;
    }

    public Long getNbalPBWageFee() {
        return nbalPBWageFee;
    }

    public void setNbalPBWageFee(Long nbalPBWageFee) {
        this.nbalPBWageFee = nbalPBWageFee;
    }

    public Long getNbalKSWageFee() {
        return nbalKSWageFee;
    }

    public void setNbalKSWageFee(Long nbalKSWageFee) {
        this.nbalKSWageFee = nbalKSWageFee;
    }

    public Long getNbalMPWageFee() {
        return nbalMPWageFee;
    }

    public void setNbalMPWageFee(Long nbalMPWageFee) {
        this.nbalMPWageFee = nbalMPWageFee;
    }

    public Long getNbalSIWageFee() {
        return nbalSIWageFee;
    }

    public void setNbalSIWageFee(Long nbalSIWageFee) {
        this.nbalSIWageFee = nbalSIWageFee;
    }

    public Long getNbalPGWageFee() {
        return nbalPGWageFee;
    }

    public void setNbalPGWageFee(Long nbalPGWageFee) {
        this.nbalPGWageFee = nbalPGWageFee;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("imdPk", getImdPk())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Imd)) return false;
        Imd castOther = (Imd) other;
        return new EqualsBuilder()
                .append(this.getImdPk(), castOther.getImdPk())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getImdPk())
                .toHashCode();
    }


    public dpi.atlas.service.cfs.common.AccountData getAccountData(int creditDebit) {
        if (creditDebit == dpi.atlas.service.cfs.common.AccountData.ACCOUNT_CREDIT)
            return new AccountData(creditAccountNo, dpi.atlas.service.cfs.common.AccountData.ACCOUNT_CFS_IMD, dpi.atlas.service.cfs.common.AccountData.ACCOUNT_CREDIT, this);
        else if (creditDebit == AccountData.ACCOUNT_DEBIT)
            return new dpi.atlas.service.cfs.common.AccountData(debitAccountNo, dpi.atlas.service.cfs.common.AccountData.ACCOUNT_CFS_IMD, AccountData.ACCOUNT_DEBIT, this);
        else
            throw new RuntimeException("Illegal Operation");
    }

    public String getAccountNo() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setAccountNo(String accountNo) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getAccountBalance() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setAccountBalance(long accountBalance) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
