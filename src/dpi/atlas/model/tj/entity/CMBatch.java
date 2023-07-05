package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Hibernate CodeGenerator
 */
public class CMBatch implements Serializable {

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
    private int batchStatus;

    /**
     * nullable persistent field
     */
    private Date batchCloseDate;

    private int hostId;

    private String serviceType;

    /** persistent field */
//    private Set txs;

    /**
     * full constructor
     */
    public CMBatch(int batchType, Date batchOpenDate, int batchStatus, Date batchCloseDate, String serviceType, int hostId){
        this.batchType = batchType;
        this.batchOpenDate = batchOpenDate;
        this.batchStatus = batchStatus;
        this.batchCloseDate = batchCloseDate;
        this.serviceType= serviceType;
        this.hostId= hostId;
//        this.txs = txs;
    }

    /**
     * default constructor
     */
    public CMBatch() {
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


    public int getHostId() {
        return hostId;
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
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


    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

//    public Set getTxs() {
//        return this.txs;
//    }
//
//    public void setTxs(Set txs) {
//        this.txs = txs;
//    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("batchPk", getBatchPk()).append("host_id", getHostId()).append("service_type", getServiceType())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CMBatch)) return false;
        CMBatch castOther = (CMBatch) other;
        return new EqualsBuilder()
                .append(this.getBatchPk(), castOther.getBatchPk()).append(this.getHostId(), castOther.getHostId()).append(this.getServiceType(), castOther.getServiceType())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getBatchPk()).append(getHostId()).append(getServiceType())
                .toHashCode();
    }

}
