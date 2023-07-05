package dpi.atlas.model.tj.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;


public class CustomerTmplSrv implements Serializable {

    private int templateID;
    private int serviceID;
    private String txID;
    private String txValueID;
    private String txValue;

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

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public String getTxValueID() {
        return txValueID;
    }

    public void setTxValueID(String txValueID) {
        this.txValueID = txValueID;
    }

    public String getTxValue() {
        return txValue;
    }

    public void setTxValue(String txValue) {
        this.txValue = txValue;
    }

    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof CustomerTmplSrv)) return false;
        CustomerTmplSrv castOther = (CustomerTmplSrv) other;
        return new EqualsBuilder()
                .append(this.getTemplateID(), castOther.getTemplateID())
                .append(this.getServiceID(), castOther.getServiceID())
                .append(this.getTxID(), castOther.getTxID())
                .append(this.getTxValueID(), castOther.getTxValueID())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getTemplateID())
                .append(getServiceID())
                .append(getTxID())
                .append(getTxValueID())
                .toHashCode();
    }
}
