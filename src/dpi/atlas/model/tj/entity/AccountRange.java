package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * @author Hibernate CodeGenerator
 */
public class AccountRange implements Serializable {

    /** identifier field */
//    private Long accountRangePk;

    /**
     * nullable persistent field
     */
    private String iin;

    /**
     * nullable persistent field
     */
    private String branchId;

    /**
     * nullable persistent field
     */
    private String range;

    /**
     * nullable persistent field
     */
    private int accountRangeType;

    /**
     * nullable persistent field
     */
    private String creationDate;

    /**
     * nullable persistent field
     */
    private String creationTime;

    /** persistent field */
//1386/04/03 Boroon commented to minimize db access - START
//    private dpi.atlas.model.tj.entity.Branch branch;
//1386/04/03 Boroon commented to minimize db access - END


    /**
     * default constructor
     */
    public AccountRange() {
    }

    /**
     * minimal constructor
     */
//1386/04/03 Boroon commented to minimize db access - START
//    public AccountRange(dpi.atlas.model.tj.entity.Branch branch) {
//        this.branch = branch;
//    }
//1386/04/03 Boroon commented to minimize db access - END    

//    public Long getAccountRangePk() {
//        return this.accountRangePk;
//    }
//
//    public void setAccountRangePk(Long accountRangePk) {
//        this.accountRangePk = accountRangePk;
//    }
    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public int getAccountRangeType() {
        return accountRangeType;
    }

    public void setAccountRangeType(int accountRangeType) {
        this.accountRangeType = accountRangeType;
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

//1386/04/03 Boroon commented to minimize db access - START
//    public dpi.atlas.model.tj.entity.Branch getBranch() {
//        return this.branch;
//    }
//
//    public void setBranch(dpi.atlas.model.tj.entity.Branch branch) {
//        this.branch = branch;
//    }
//1386/04/03 Boroon commented to minimize db access - END    

//    public String toString() {
//        return new ToStringBuilder(this)
//            .append("accountRangePk", getAccountRangePk())
//            .toString();
//    }
//
//    public boolean equals(Object other) {
//        if ( (this == other ) ) return true;
//        if ( !(other instanceof AccountRange) ) return false;
//        AccountRange castOther = (AccountRange) other;
//        return new EqualsBuilder()
//            .append(this.getAccountRangePk(), castOther.getAccountRangePk())
//            .isEquals();
//    }
//
//    public int hashCode() {
//        return new HashCodeBuilder()
//            .append(getAccountRangePk())
//            .toHashCode();
//    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("iin", getIin())
                .append("branchId", getBranchId())
                .append("range", getRange())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof AccountRange)) return false;
        AccountRange castOther = (AccountRange) other;
        return new EqualsBuilder()
                .append(this.getIin(), castOther.getIin())
                .append(this.getBranchId(), castOther.getBranchId())
                .append(this.getRange(), castOther.getRange())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getIin())
                .append(getBranchId())
                .append(getRange())
                .toHashCode();
    }


}
