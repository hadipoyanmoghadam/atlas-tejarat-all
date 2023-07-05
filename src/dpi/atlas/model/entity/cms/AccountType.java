package dpi.atlas.model.entity.cms;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: mb
 * Date: Sep 24, 2007
 * Time: 4:23:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccountType implements Serializable {

    private String id;
    private String description;
    private String hostAccType1;
    private String hostAccType2;
    private Long minBalance;
    private Long withdrawLimit;

    public AccountType(String id, String description, String hostAccType1, String hostAccType2, Long minBalance, Long withdrawLimit) {
        this.id= id;
        this.description = description;
        this.hostAccType1 = hostAccType1;
        this.hostAccType2 = hostAccType2;
        this.minBalance = minBalance;
        this.withdrawLimit = withdrawLimit;
    }

   public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMinBalance() {
        return minBalance;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("id", getId())
                .toString();
    }

    public boolean equals(java.lang.Object other) {
        if ((this == other)) return true;
        if (!(other instanceof AccountType)) return false;
        AccountType castOther = (AccountType) other;
        return new EqualsBuilder()
                .append(this.getId(), castOther.getId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .toHashCode();
    }

}
