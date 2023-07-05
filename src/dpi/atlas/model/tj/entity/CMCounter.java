package dpi.atlas.model.tj.entity;

/**
 * @author Amir hosein Ahmadi.
 *         Date: Jun 10, 2007
 *         Time: 6:25:09 PM
 *         M.S. Student of Sharif University
 */

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * @author Hibernate CodeGenerator
 */
public class CMCounter implements Serializable {

    /**
     * identifier field
     */
    private String name;

    /**
     * identifier field
     */
    private String days;

    /**
     * nullable persistent field
     */
    private Long counter;

    /**
     * full constructor
     */
    public CMCounter(String name, String days, Long counter) {
        this.name = name;
        this.days = days;
        this.counter = counter;
    }

    /**
     * default constructor
     */
    public CMCounter() {
    }

    /**
     * minimal constructor
     */
    public CMCounter(String name, String days) {
        this.name = name;
        this.days = days;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDays() {
        return this.days;
    }

    public void setDays(String days) {
        this.days = days;
    }


    public Long getCounter() {
        return counter;
    }

    public void setCounter(Long counter) {
        this.counter = counter;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("name", getName())
                .append("days", getDays())
                .toString();
    }

    public boolean equals(java.lang.Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CMCounter)) return false;
        CMCounter castOther = (CMCounter) other;
        return new EqualsBuilder()
                .append(this.getName(), castOther.getName())
                .append(this.getDays(), castOther.getDays())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getName())
                .append(getDays())
                .toHashCode();
    }

}
