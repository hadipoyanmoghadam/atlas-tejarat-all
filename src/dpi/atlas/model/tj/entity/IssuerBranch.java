package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * @author Hibernate CodeGenerator
 */
public class IssuerBranch implements Serializable {

    /**
     * identifier field
     */
    private String iin;

    /**
     * identifier field
     */
    private String branchId;

    /**
     * nullable persistent field
     */
    private String branchName;

    /**
     * nullable persistent field
     */
    private String branchNameFarsi;

    /**
     * nullable persistent field
     */
    private String refTelNo;

    /**
     * nullable persistent field
     */
    private String contact;

    /**
     * nullable persistent field
     */
    private Integer status;

    /**
     * nullable persistent field
     */
    private String effectDate;

    /**
     * nullable persistent field
     */
    private String lastGeneratedPan;

    /**
     * nullable persistent field
     */
    private String address;

    /**
     * nullable persistent field
     */
    private Integer cityFK;

    /** full constructor */

    /**
     * default constructor
     */
    public IssuerBranch() {
    }


    public String getIin() {
        return this.iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getBranchId() {
        return this.branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getBranchNameFarsi() {
        return this.branchNameFarsi;
    }

    public void setBranchNameFarsi(String branchNameFarsi) {
        this.branchNameFarsi = branchNameFarsi;
    }

    public String getRefTelNo() {
        return this.refTelNo;
    }

    public void setRefTelNo(String refTelNo) {
        this.refTelNo = refTelNo;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getEffectDate() {
        return this.effectDate;
    }

    public void setEffectDate(String effectDate) {
        this.effectDate = effectDate;
    }

    public String getLastGeneratedPan() {
        return this.lastGeneratedPan;
    }

    public void setLastGeneratedPan(String lastGeneratedPan) {
        this.lastGeneratedPan = lastGeneratedPan;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCityFK() {
        return this.cityFK;
    }

    public void setCityFK(Integer cityFK) {
        this.cityFK = cityFK;
    }


    public String toString() {
        return new ToStringBuilder(this)
                .append("iin", getIin())
                .append("branchId", getBranchId())
                .toString();
    }

    public boolean equals(java.lang.Object other) {
        if ((this == other)) return true;
        if (!(other instanceof IssuerBranch)) return false;
        IssuerBranch castOther = (IssuerBranch) other;
        return new EqualsBuilder()
                .append(this.getIin(), castOther.getIin())
                .append(this.getBranchId(), castOther.getBranchId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getIin())
                .append(getBranchId())
                .toHashCode();
    }

}
