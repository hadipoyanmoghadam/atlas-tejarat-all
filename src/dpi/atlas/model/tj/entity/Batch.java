package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;


/**
 * @author Hibernate CodeGenerator
 */
public class Batch implements Serializable {

    /**
     * identifier field
     */
    private Long batchPk;

    /**
     * nullable persistent field
     */
    private int batchType;

    /**
     * nullable persistent field
     */
    private Date batchOpenDate;

    /**
     * nullable persistent field
     */
    private int cfsStatus;

    private int sgbStatus;

    private int faraStatus;

    /**
     * nullable persistent field
     */
    private Date batchCloseDate;

    /** persistent field */
//    private Set txs;

    /**
     * full constructor
     */
    public Batch(Long batchPk) {
        this.batchPk= batchPk;
        this.cfsStatus = 0;
        this.faraStatus = 0;
        this.sgbStatus = 0;

    }

    public Batch(int batchType, Date batchOpenDate, Date batchCloseDate, int cfsStatus, int sgbStatus, int faraStatus) {
        this.batchType = batchType;
        this.batchOpenDate = batchOpenDate;
        this.batchCloseDate = batchCloseDate;
        this.cfsStatus = cfsStatus;
        this.faraStatus = faraStatus;
        this.sgbStatus = sgbStatus;
//        this.txs = txs;
    }

    /**
     * default constructor
     */
    public Batch() {
    }

    /**
     * minimal constructor
     */
//    public Batch(Set txs) {
//        this.txs = txs;
//    }
    public Long getBatchPk() {
        return this.batchPk;
    }

    public void setBatchPk(Long batchPk) {
        this.batchPk = batchPk;
    }


    public int getBatchType() {
        return batchType;
    }

    public void setBatchType(int batchType) {
        this.batchType = batchType;
    }

    public Date getBatchOpenDate() {
        return this.batchOpenDate;
    }

    public void setBatchOpenDate(Date batchOpenDate) {
        this.batchOpenDate = batchOpenDate;
    }

    public int getCfsStatus() {
        return cfsStatus;
    }

    public void setCfsStatus(int cfsStatus) {
        this.cfsStatus = cfsStatus;
    }


    public int getSgbStatus() {
        return sgbStatus;
    }

    public void setSgbStatus(int sgbStatus) {
        this.sgbStatus = sgbStatus;
    }

    public int getFaraStatus() {
        return faraStatus;
    }

    public void setFaraStatus(int faraStatus) {
        this.faraStatus = faraStatus;
    }

    public Date getBatchCloseDate() {
        return this.batchCloseDate;
    }

    public void setBatchCloseDate(Date batchCloseDate) {
        this.batchCloseDate = batchCloseDate;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("batchPk", getBatchPk())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Batch)) return false;
        Batch castOther = (Batch) other;
        return new EqualsBuilder()
                .append(this.getBatchPk(), castOther.getBatchPk())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getBatchPk())
                .toHashCode();
    }

}
