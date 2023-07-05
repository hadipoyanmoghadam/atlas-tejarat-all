package dpi.atlas.model.tj.entity;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Sep 20, 2007
 * Time: 9:28:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomerAccount implements Serializable {
    private long balance;
    private long subsidy_amount;


    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getSubsidy_amount() {
        return subsidy_amount;
    }

    public void setSubsidy_amount(long subsidy_amount) {
        this.subsidy_amount = subsidy_amount;
    }
}
