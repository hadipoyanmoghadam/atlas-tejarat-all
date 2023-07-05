package dpi.atlas.service.cm.common;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Oct 21, 2006
 * Time: 12:12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeneralTxn {

    String docNo;
    //    String txCode;
    String amount;
    String accountNo;
    String debitCredit;
    String date;
    String time;
    String txDesc;

    public GeneralTxn() {
    }

    public GeneralTxn(String docNo, String amount, String accountNo, String debitCredit, String date, String time, String txDesc) {
        this.docNo = docNo;
        this.amount = amount;
        this.accountNo = accountNo;
        this.debitCredit = debitCredit;
        this.date = date;
        this.time = time;
        this.txDesc = txDesc;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getDebitCredit() {
        return debitCredit;
    }

    public void setDebitCredit(String debitCredit) {
        this.debitCredit = debitCredit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTxDesc() {
        return txDesc;
    }

    public void setTxDesc(String txDesc) {
        this.txDesc = txDesc;
    }

}
