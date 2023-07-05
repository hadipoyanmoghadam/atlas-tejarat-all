package branch.dpi.atlas.service.cm.handler.pg.accountStatement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by R.Nasiri on 11/24/19.
 */


@XmlType(propOrder ={"actioncode","desc","rrn","account_no","fromDate","toDate","transCount","tx","respdatetime"})
@XmlRootElement(name = "STATEMENT_RESPONSE")
public class AccountStatementResp {

    protected String rrn;
    protected String account_no;
    protected String desc;
    protected String actioncode;
    protected String respdatetime;
    protected String fromDate;
    protected String toDate;
    protected String transCount;
    protected AccountStatementTxList tx;


    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getAccount_no() {
        return account_no;
    }

    @XmlElement(name = "ACCOUNT_NO")
    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getActioncode() {
        return actioncode;
    }

    @XmlElement(name = "ACTIONCODE")
    public void setActioncode(String actioncode) {
        this.actioncode = actioncode;
    }

    public String getDesc() {
        return desc;
    }

    @XmlElement(name = "DESC")
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRespdatetime() {
        return respdatetime;
    }

    @XmlElement(name = "RESPDATETIME")
    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }

    public String getFromDate() {
        return fromDate;
    }
    @XmlElement(name = "FROMDATE")
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }
    @XmlElement(name = "TODATE")
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getTransCount() {
        return transCount;
    }
    @XmlElement(name = "TRANSCOUNT")
    public void setTransCount(String transCount) {
        this.transCount = transCount;
    }

    public AccountStatementTxList getTx() {
        return tx;
    }
    @XmlElement(name = "TRANSACTIONS")
    public void setTx(AccountStatementTxList tx) {
        this.tx = tx;
    }
}



