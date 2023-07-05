package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Oct 30, 2005
 * Time: 8:16:52 AM
 * To change this template use File | Settings | File Templates.
 */

public class CurrentValues {
    /**
     * nullable persistent field
     */
    private String itemKey;

    /**
     * nullable persistent field
     */
    private String itemValue;

    /**
     * full constructor
     */
    public CurrentValues(String itemKey, String itemValue) {
        this.itemKey = itemKey;
        this.itemValue = itemValue;
    }

    /**
     * default constructor
     */
    public CurrentValues() {
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("itemKey", getItemKey())
                .append("itemValue", getItemValue())
                .toString();
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof dpi.atlas.model.tj.entity.Account)) return false;
        CurrentValues castOther = (CurrentValues) other;
        return new EqualsBuilder()
                .append(this.getItemKey(), castOther.getItemKey())
                .append(this.getItemValue(), castOther.getItemValue())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getItemKey())
                .append(getItemValue())
                .toHashCode();
    }
}