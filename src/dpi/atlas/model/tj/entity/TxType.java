package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * @author Hibernate CodeGenerator
 */
public class TxType implements Serializable {

    /**
     * nullable persistent field
     */
    private String txCode;

    /**
     * nullable persistent field
     */
    private String txDesc;

    /**
     * nullable persistent field
     */
    private String spwMessageType;

    /**
     * nullable persistent field
     */
    private String spwTransType;

    /**
     * nullable persistent field
     */
    private String sgbTxCode;

    /**
     * nullable persistent field
     */
    private String debitCredit;
    private String spwChannelCode;

    /**
     * full constructor
     */
    public TxType(String txCode, String txDesc, String spwMessageType, String spwTransType, String sgbTxCode, String debitCredit) {
        this.txCode = txCode;
        this.txDesc = txDesc;
        this.spwMessageType = spwMessageType;
        this.spwTransType = spwTransType;
        this.sgbTxCode = sgbTxCode;
        this.debitCredit = debitCredit;
    }

    public TxType(String sgbTxCode, String spwChannelCode) {
           this.sgbTxCode = sgbTxCode;
           this.spwChannelCode = spwChannelCode;
       }

    /**
     * default constructor
     */
    public TxType() {
    }

    public String getTxCode() {
        return txCode;
    }

    public void setTxCode(String txCode) {
        this.txCode = txCode;
    }

    public String getTxDesc() {
        return txDesc;
    }

    public void setTxDesc(String txDesc) {
        this.txDesc = txDesc;
    }

    public String getSpwMessageType() {
        return spwMessageType;
    }

    public void setSpwMessageType(String spwMessageType) {
        this.spwMessageType = spwMessageType;
    }

    public String getSpwTransType() {
        return spwTransType;
    }

    public void setSpwTransType(String spwTransType) {
        this.spwTransType = spwTransType;
    }

    public String getSgbTxCode() {
        return sgbTxCode;
    }

    public void setSgbTxCode(String sgbTxCode) {
        this.sgbTxCode = sgbTxCode;
    }

    public String getDebitCredit() {
        return debitCredit;
    }

    public void setDebitCredit(String debitCredit) {
        this.debitCredit = debitCredit;
    }

    public String getSpwChannelCode() {
        return spwChannelCode;
    }

    public void setSpwChannelCode(String spwChannelCode) {
        this.spwChannelCode = spwChannelCode;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("txCode", getTxCode())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof TxType)) return false;
        TxType castOther = (TxType) other;
        return new EqualsBuilder()
                .append(this.getTxCode(), castOther.getTxCode())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getTxCode())
                .toHashCode();
    }

}