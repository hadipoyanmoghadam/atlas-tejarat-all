package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


public class CardAccount implements Serializable {
    public static final String NON_CFS_CARD_ACCOUNTS_TABLE_NAME = "TBNONCFSCARDACC";
    public static final String CFS_CARD_ACCOUNTS_TABLE_NAME = "TBCFSCARDACCOUNT";


    private String pan;

    /**
     * persistent field
     */
    private String sequenceNo;

    /**
     * identifier field
     */
    private String accountType;

    /**
     * identifier field
     */
    private String accountNo;

    /**
     * nullable persistent field
     */
    private String creationDate;

    /**
     * nullable persistent field
     */
    private String creationTime;

    private String branchID;

    private Long maxTransLimit;
    private int status;

    private String  withdrawDate;
    private long withdrawAmount;
    private int series;

    private String  PBwithdrawDate;
    private long PBwithdrawAmount;
    private String  STBwithdrawDate;
    private long STBwithdrawAmount;
    private String cardType;
    private String smsRegister;

    /**
     * full constructor
     */
    public CardAccount(/*Long cardPk, Long accountPk,*/ String pan, String sequenceNo, String accountType, String accountNo, String creationDate, String creationTime/*, dpi.atlas.model.tj.entity.Card card, dpi.atlas.model.tj.entity.Account account*/) {
        this.pan = pan;
        this.sequenceNo = sequenceNo;
        this.accountType = accountType;
        this.accountNo = accountNo;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
    }

    public CardAccount(/*Long cardPk, Long accountPk,*/ String pan, String sequenceNo, String accountType, String accountNo, String creationDate, String creationTime/*, dpi.atlas.model.tj.entity.Card card, dpi.atlas.model.tj.entity.Account account*/, long maxTransLimit,int status) {
        this.pan = pan;
        this.sequenceNo = sequenceNo;
        this.accountType = accountType;
        this.accountNo = accountNo;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.maxTransLimit = new Long(maxTransLimit);
        this.status= status;
    }
     public CardAccount(/*Long cardPk, Long accountPk,*/ String pan, String sequenceNo, String accountType, String accountNo, String creationDate, String creationTime/*, dpi.atlas.model.tj.entity.Card card, dpi.atlas.model.tj.entity.Account account*/, long maxTransLimit,int status,int series) {
        this.pan = pan;
        this.sequenceNo = sequenceNo;
        this.accountType = accountType;
        this.accountNo = accountNo;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
        this.maxTransLimit = new Long(maxTransLimit);
        this.status= status;
        this.series=series;
    }

    public CardAccount(String pan, String accountType, String accountNo, Long maxTransLimit, long withdrawAmount, int series,  long PBwithdrawAmount, long STBwithdrawAmount) {
        this.pan = pan;
        this.accountType = accountType;
        this.accountNo = accountNo;
        this.maxTransLimit = maxTransLimit;
        this.withdrawAmount = withdrawAmount;
        this.series = series;
        this.PBwithdrawAmount = PBwithdrawAmount;
        this.STBwithdrawAmount = STBwithdrawAmount;
    }

    public CardAccount(String pan, String accountType, String accountNo, Long maxTransLimit, long withdrawAmount, long PBwithdrawAmount,long STBwithdrawAmount) {
        this.pan = pan;
        this.accountType = accountType;
        this.accountNo = accountNo;
        this.maxTransLimit = maxTransLimit;
        this.withdrawAmount = withdrawAmount;
        this.PBwithdrawAmount = PBwithdrawAmount;
        this.STBwithdrawAmount = STBwithdrawAmount;
    }

    /**
     * default constructor
     */
    public CardAccount() {
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
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

    /**
     * minimal constructor
     */
//    public CardAccount(Long cardPk, Long accountPk) {
//        this.cardPk = cardPk;
//        this.accountPk = accountPk;
//    }

//    public Long getCardPk() {
//        return this.cardPk;
//    }
//
//    public void setCardPk(Long cardPk) {
//        this.cardPk = cardPk;
//    }
//
//    public Long getAccountPk() {
//        return this.accountPk;
//    }
//
//    public void setAccountPk(Long accountPk) {
//        this.accountPk = accountPk;
//    }
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

//    public dpi.atlas.model.tj.entity.Card getCard() {
//        return this.card;
//    }
//
//    public void setCard(dpi.atlas.model.tj.entity.Card card) {
//        this.card = card;
//    }

//    public dpi.atlas.model.tj.entity.Account getAccount() {
//        return this.account;
//    }
//
//    public void setAccount(dpi.atlas.model.tj.entity.Account account) {
//        this.account = account;
//    }

    public String getBranchID() {
        return branchID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }


    public Long getMaxTransLimit() {
        return maxTransLimit;
    }

    public void setMaxTransLimit(Long maxTransLimit) {
        this.maxTransLimit = maxTransLimit;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getWithdrawDate() {
        return withdrawDate;
    }

    public void setWithdrawDate(String withdrawDate) {
        this.withdrawDate = withdrawDate;
    }

    public long getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(long withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }
    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    public String getPBwithdrawDate() {
        return PBwithdrawDate;
    }

    public void setPBwithdrawDate(String PBwithdrawDate) {
        this.PBwithdrawDate = PBwithdrawDate;
    }

    public long getPBwithdrawAmount() {
        return PBwithdrawAmount;
    }

    public void setPBwithdrawAmount(long PBwithdrawAmount) {
        this.PBwithdrawAmount = PBwithdrawAmount;
    }

    public String getSTBwithdrawDate() {
        return STBwithdrawDate;
    }

    public void setSTBwithdrawDate(String STBwithdrawDate) {
        this.STBwithdrawDate = STBwithdrawDate;
    }

    public long getSTBwithdrawAmount() {
        return STBwithdrawAmount;
    }

    public void setSTBwithdrawAmount(long STBwithdrawAmount) {
        this.STBwithdrawAmount = STBwithdrawAmount;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("pan", getPan())
                .append("sequenceNo", getSequenceNo())
                .append("accountType", getAccountType())
                .append("accountNo", getAccountNo())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CardAccount)) return false;
        CardAccount castOther = (CardAccount) other;
        return new EqualsBuilder()
                .append(this.getPan(), castOther.getPan())
                .append(this.getSequenceNo(), castOther.getSequenceNo())
                .append(this.getAccountType(), castOther.getAccountType())
                .append(this.getAccountNo(), castOther.getAccountNo())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getPan())
                .append(getSequenceNo())
                .append(getAccountType())
                .append(getAccountNo())
                .toHashCode();
    }

    public String getSmsRegister() {
        return smsRegister;
    }

    public void setSmsRegister(String smsRegister) {
        this.smsRegister = smsRegister;
    }
}
