package branch.dpi.atlas.model.tj.entity;

import dpi.atlas.model.tj.entity.FaultLog;
import dpi.atlas.service.cfs.common.CFSConstants;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class BlockReport implements Serializable {

    private long amount;
    private String desc;
    private String date;
    private String branchCode;
    private String blockRow;
    private String chnUser;


    /**
     * default constructor
     */
    public BlockReport() {
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBlockRow() {
        return blockRow;
    }

    public void setBlockRow(String blockRow) {
        this.blockRow = blockRow;
    }

    public String getChnUser() {
        return chnUser;
    }

    public void setChnUser(String chnUser) {
        this.chnUser = chnUser;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("BlockRow", getBlockRow())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof FaultLog)) return false;
        BlockReport castOther = (BlockReport) other;
        return new EqualsBuilder()
                .append(this.getBlockRow(), castOther.getBlockRow())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getBlockRow())
                .toHashCode();
    }
}