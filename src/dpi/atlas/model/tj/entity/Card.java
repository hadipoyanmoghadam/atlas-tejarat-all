package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * @author Hibernate CodeGenerator
 */
public class Card implements Serializable {

    /** identifier field */
//    private Long cardPk;

    /**
     * persistent field
     */
    private String cardNo;

    /**
     * persistent field
     */
    private String sequenceNo;

    /**
     * nullable persistent field
     */
    private String creationDate;

    /**
     * nullable persistent field
     */
    private String creationTime;
    private String origEditDate;
    private String customerID;

    private int templateID;

    /**
     * default constructor
     */
    public Card() {
    }

    /**
     * minimal constructor
     */
    public Card(String cardNo, String sequenceNo ) {
        this.cardNo = cardNo;
        this.sequenceNo = sequenceNo;
    }

    public Card(String cardNo, String sequenceNo, String creationDate, String creationTime) {
        this.cardNo = cardNo;
        this.sequenceNo = sequenceNo;
        this.creationDate = creationDate;
        this.creationTime = creationTime;
    }
    

    public String getCardNo() {
        return this.cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getSequenceNo() {
        return this.sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
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

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public String getOrigEditDate() {
        return origEditDate;
    }

    public void setOrigEditDate(String origEditDate) {
        this.origEditDate = origEditDate;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("cardNo", getCardNo())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Card)) return false;
        Card castOther = (Card) other;
        return new EqualsBuilder()
                .append(this.getCardNo(), castOther.getCardNo())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getCardNo())
                .toHashCode();
    }

}
