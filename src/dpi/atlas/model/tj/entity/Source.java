package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Sep 2, 2006
 * Time: 10:36:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class Source implements Serializable {
    private short ID;
    private String name;
    private String name2;
    private String branchNo;

    public short getID() {
        return ID;
    }

    public void setID(short ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getBranchNo() {
        return branchNo;
    }

    public void setBranchNo(String branchNo) {
        this.branchNo = branchNo;
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CMServer)) return false;
        Source castOther = (Source) other;
        return new EqualsBuilder()
                .append(this.getID(), castOther.getID())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getID())
                .toHashCode();
    }
}