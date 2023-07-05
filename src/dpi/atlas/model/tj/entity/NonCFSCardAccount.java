package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


public class NonCFSCardAccount extends CardAccount {


    private String  nft_date;
    private long nft_amount;
    private String  pgnft_date;
    private long pgnft_amount;
    private String  nftpos_date;
    private long nftpos_amount;
    private String  mpnft_date;
    private long mpnft_amount;
    private String  ksnft_date;
    private long ksnft_amount;

    public NonCFSCardAccount(String pan, String accountType, String accountNo, Long maxTransLimit, long withdrawAmount, long PBwithdrawAmount, long STBwithdrawAmount, long nft_amount, long pgnft_amount, long nftpos_amount,  long mpnft_amount,  long ksnft_amount) {
        super(pan, accountType, accountNo, maxTransLimit, withdrawAmount, PBwithdrawAmount, STBwithdrawAmount);
        this.nft_amount = nft_amount;
        this.pgnft_amount = pgnft_amount;
        this.nftpos_amount = nftpos_amount;
        this.mpnft_amount = mpnft_amount;
        this.ksnft_amount = ksnft_amount;
    }

    /**
     * default constructor
     */
    public NonCFSCardAccount() {
    }

    public String getNft_date() {
        return nft_date;
    }

    public void setNft_date(String nft_date) {
        this.nft_date = nft_date;
    }

    public long getNft_amount() {
        return nft_amount;
    }

    public void setNft_amount(long nft_amount) {
        this.nft_amount = nft_amount;
    }

    public String getPgnft_date() {
        return pgnft_date;
    }

    public void setPgnft_date(String pgnft_date) {
        this.pgnft_date = pgnft_date;
    }

    public long getPgnft_amount() {
        return pgnft_amount;
    }

    public void setPgnft_amount(long pgnft_amount) {
        this.pgnft_amount = pgnft_amount;
    }

    public String getNftpos_date() {
        return nftpos_date;
    }

    public void setNftpos_date(String nftpos_date) {
        this.nftpos_date = nftpos_date;
    }

    public long getNftpos_amount() {
        return nftpos_amount;
    }

    public void setNftpos_amount(long nftpos_amount) {
        this.nftpos_amount = nftpos_amount;
    }

    public String getMpnft_date() {
        return mpnft_date;
    }

    public void setMpnft_date(String mpnft_date) {
        this.mpnft_date = mpnft_date;
    }

    public long getMpnft_amount() {
        return mpnft_amount;
    }

    public void setMpnft_amount(long mpnft_amount) {
        this.mpnft_amount = mpnft_amount;
    }

    public String getKsnft_date() {
        return ksnft_date;
    }

    public void setKsnft_date(String ksnft_date) {
        this.ksnft_date = ksnft_date;
    }

    public long getKsnft_amount() {
        return ksnft_amount;
    }

    public void setKsnft_amount(long ksnft_amount) {
        this.ksnft_amount = ksnft_amount;
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
        if (!(other instanceof NonCFSCardAccount)) return false;
        NonCFSCardAccount castOther = (NonCFSCardAccount) other;
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

}
