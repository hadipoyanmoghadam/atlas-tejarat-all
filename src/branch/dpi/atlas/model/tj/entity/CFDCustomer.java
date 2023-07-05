package branch.dpi.atlas.model.tj.entity;


import java.util.Vector;

/**
 * User: Behnaz
 * Date: Feb 6, 2012
 * Time: 9:55:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class CFDCustomer extends CFDRequestBase {
    CFDCustomerInfo customerInfo;
    Vector accounts;
    CFDPan pan;

    public CFDCustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CFDCustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public Vector getAccounts() {
        return accounts;
    }

    public void setAccounts(Vector accounts) {
        this.accounts = accounts;
    }

    public CFDPan getPan() {
        return pan;
    }

    public void setPan(CFDPan pan) {
        this.pan = pan;
    }
}

