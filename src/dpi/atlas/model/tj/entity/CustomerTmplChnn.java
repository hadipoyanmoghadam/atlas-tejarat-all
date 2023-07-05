
package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
/**
 * Created by Behnaz (EBanking 86/12/04).
 * To change this template use File | Settings | File Templates.
 *
 */

public class CustomerTmplChnn implements Serializable {

    private int templateID;
    private int serviceID;
    private long services;
    private long minLimit;
    private long maxLimit;
    private long maxLimitNFT;

    public int getTemplateID() {
        return templateID;
    }

    public void setTemplateID(int templateID) {
        this.templateID = templateID;
    }

    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public long getServices() {
        return services;
    }

    public void setServices(long services) {
        this.services = services;
    }

    public long getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(long minLimit) {
        this.minLimit = minLimit;
    }

    public long getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(long maxLimit) {
        this.maxLimit = maxLimit;
    }

    public long getMaxLimitNFT() {
        return maxLimitNFT;
    }

    public void setMaxLimitNFT(long maxLimitNFT) {
        this.maxLimitNFT = maxLimitNFT;
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CustomerTmplChnn)) return false;
        CustomerTmplChnn castOther = (CustomerTmplChnn) other;
        return new EqualsBuilder()
                .append(this.getTemplateID(), castOther.getTemplateID())
                .append(this.getServiceID(), castOther.getServiceID())
                .append(this.getServices(), castOther.getServices())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getTemplateID())
                .append(getServiceID())
                .append(getServices())
                .toHashCode();
    }
}
