package dpi.atlas.service.cm.common;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Oct 21, 2006
 * Time: 12:12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralBalance {

    String accountNo;
    String balance;
    String currency;
    String debitCredit;

    public GeneralBalance() {
    }

    public GeneralBalance(String accountNo, String balance, String currency, String debitCredit) {
        this.accountNo = accountNo;
        this.balance = balance;
        this.currency = currency;
        this.debitCredit = debitCredit;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDebitCredit() {
        return debitCredit;
    }

    public void setDebitCredit(String debitCredit) {
        this.debitCredit = debitCredit;
    }
}
