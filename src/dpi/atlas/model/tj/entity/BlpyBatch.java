package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Hibernate CodeGenerator
 */
public class BlpyBatch implements Serializable {

    /**
     * identifier field
     */
    private Long batchPk;

    /**
     * nullable persistent field
     */
    private Date batchOpenDate;

    /**
     * nullable persistent field
     */
    private int batchStatus;

    /**
     * nullable persistent field
     */
    private Date batchCloseDate;

    /** persistent field */
//    private Set txs;

    /**
     * full constructor
     */
    public BlpyBatch(Date batchOpenDate, int batchStatus, Date batchCloseDate/*, Set txs*/) {
        this.batchOpenDate = batchOpenDate;
        this.batchStatus = batchStatus;
        this.batchCloseDate = batchCloseDate;
//        this.txs = txs;
    }

    /**
     * default constructor
     */
    public BlpyBatch() {
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

    public Date getBatchOpenDate() {
        return this.batchOpenDate;
    }

    public void setBatchOpenDate(Date batchOpenDate) {
        this.batchOpenDate = batchOpenDate;
    }

    public int getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(int batchStatus) {
        this.batchStatus = batchStatus;
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
        if (!(other instanceof BlpyBatch)) return false;
        BlpyBatch castOther = (BlpyBatch) other;
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
