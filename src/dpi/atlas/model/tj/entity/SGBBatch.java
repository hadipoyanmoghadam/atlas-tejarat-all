package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;


/**
 * @author Hibernate CodeGenerator
 */
public class SGBBatch implements Serializable {

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
    private String batchStatus;


    private Date batchCloseDate;


    private String sgbFileDate;

    private Set sgbLogs;

    public SGBBatch(Date batchOpenDate, String batchStatus, Date batchCloseDate, String sgbFileDate, Set sgbLogs) {
        this.batchOpenDate = batchOpenDate;
        this.batchStatus = batchStatus;
        this.batchCloseDate = batchCloseDate;
        this.sgbFileDate = sgbFileDate;
        this.sgbLogs = sgbLogs;
    }

    public SGBBatch() {
    }

    /**
     * minimal constructor
     */
    public SGBBatch(Set sgbLogs) {
        this.sgbLogs = sgbLogs;
    }

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

    public String getBatchStatus() {
        return this.batchStatus;
    }

    public void setBatchStatus(String batchStatus) {
        this.batchStatus = batchStatus;
    }

    public Date getBatchCloseDate() {
        return this.batchCloseDate;
    }

    public void setBatchCloseDate(Date batchCloseDate) {
        this.batchCloseDate = batchCloseDate;
    }

    // 1385/01/28 Boroon Added to support multiple sgb file upload in one day - START
    public String getSgbFileDate() {
        return sgbFileDate;
    }

    public void setSgbFileDate(String sgbFileDate) {
        this.sgbFileDate = sgbFileDate;
    }
// 1385/01/28 Boroon Added to support multiple sgb file upload in one day - END

    public Set getSgbLogs() {
        return sgbLogs;
    }

    public void setSgbLogs(Set sgbLogs) {
        this.sgbLogs = sgbLogs;
    }


    public String toString() {
        return new ToStringBuilder(this)
                .append("batchPk", getBatchPk())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof SGBBatch)) return false;
        SGBBatch castOther = (SGBBatch) other;
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
