package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


public class GiftCard implements Serializable {

    private String branchCode;
    private String cardNo;
    private String srcAccountNo;
    private String destAccountNo;
    private String docNo;
    private String reqNo;
    private String nationalcode;
    private String isDone;
    private String date;
    private String time;
    private String amount;

    public GiftCard() {
    }

    public GiftCard(String branchCode, String nationalcode, String docNo) {
        this.branchCode = branchCode;
        this.nationalcode = nationalcode;
        this.docNo = docNo;
    }

    public GiftCard(String branchCode, String cardNo, String srcAccountNo, String destAccountNo, String docNo, String isDone, String date, String time,String amount) {
        this.branchCode = branchCode;
        this.cardNo = cardNo;
        this.srcAccountNo = srcAccountNo;
        this.destAccountNo = destAccountNo;
        this.docNo = docNo;
        this.isDone = isDone;
        this.date = date;
        this.time = time;
        this.amount = amount;
    }
    public GiftCard(String branchCode, String docNo, String date, String amount) {
        this.branchCode = branchCode;
        this.docNo = docNo;
        this.date = date;
        this.amount = amount;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getSrcAccountNo() {
        return srcAccountNo;
    }

    public void setSrcAccountNo(String srcAccountNo) {
        this.srcAccountNo = srcAccountNo;
    }

    public String getDestAccountNo() {
        return destAccountNo;
    }

    public void setDestAccountNo(String destAccountNo) {
        this.destAccountNo = destAccountNo;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getReqNo() {
        return reqNo;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo;
    }

    public String getNationalcode() {
        return nationalcode;
    }

    public void setNationalcode(String nationalcode) {
        this.nationalcode = nationalcode;
    }

    public String getIsDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("branchCode", getBranchCode())
                .append("docNo", getDocNo())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof GiftCard)) return false;
        GiftCard castOther = (GiftCard) other;
        return new EqualsBuilder()
                .append(this.getBranchCode(), castOther.getBranchCode())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getBranchCode())
                .toHashCode();
    }

}