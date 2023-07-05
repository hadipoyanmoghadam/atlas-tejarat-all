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
public class SourceServices implements Serializable {
    private short sourceId;
    private short id;
    private String name;
    private String description;

    public short getSourceId() {
        return sourceId;
    }

    public void setSourceId(short sourceId) {
        this.sourceId = sourceId;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CMServer)) return false;
        SourceServices castOther = (SourceServices) other;
        return new EqualsBuilder()
                .append(this.getId(), castOther.getId())
                .append(this.getSourceId(), castOther.getSourceId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getId())
                .append(getSourceId())
                .toHashCode();
    }
}