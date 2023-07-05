package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Sep 3, 2006
 * Time: 10:24:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerService implements Serializable {
    private String customerId;
    private long serviceId;
    private String pin;
    private String tPin;
    private long lang;
    
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public long getServiceId() {
        return serviceId;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String gettPin() {
        return tPin;
    }

    public void settPin(String tPin) {
        this.tPin = tPin;
    }

    public long getLang() {
        return lang;
    }

    public void setLang(long lang) {
        this.lang = lang;
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CustomerService)) return false;
        CustomerService castOther = (CustomerService) other;
        return new EqualsBuilder()
                .append(this.getCustomerId(), castOther.getCustomerId())
                .append(this.getServiceId(), castOther.getServiceId())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getCustomerId())
                .append(getServiceId())
                .toHashCode();
    }
}
