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
public class CMServerServices implements Serializable {
    private long cmServerId;
    private short sourceId;
    private short serviceId;

    public long getCmServerId() {
        return cmServerId;
    }

    public void setCmServerId(long cmServerId) {
        this.cmServerId = cmServerId;
    }

    public short getSourceId() {
        return sourceId;
    }

    public void setSourceId(short sourceId) {
        this.sourceId = sourceId;
    }

    public short getServiceId() {
        return serviceId;
    }

    public void setServiceId(short serviceId) {
        this.serviceId = serviceId;
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CMServer)) return false;
        CMServerServices castOther = (CMServerServices) other;
        return new EqualsBuilder()
                .append(this.getServiceId(), castOther.getServiceId())
                .append(this.getSourceId(), castOther.getSourceId())
                .append(this.getCmServerId(), castOther.getCmServerId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getServiceId())
                .append(getSourceId())
                .append(getCmServerId())
                .toHashCode();
    }
}