package branch.dpi.atlas.service.cm.handler.pg.wfp;




import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 7/1/19.
 */


@XmlType(propOrder ={"actioncode","desc","rrn","accountNo","trans","respdatetime"})
@XmlRootElement(name = "WFP_STATEMENT_RESPONSE")
public class StatementResp {

    protected String rrn;
    protected String desc;
    protected String actioncode;
    protected String respdatetime;
    protected String accountNo;
    protected WFPTransactionList trans;

    public WFPTransactionList getTrans() {
        return trans;
    }
    @XmlElement(name = "TRANSACTIONS")
    public void setTrans(WFPTransactionList trans) {
        this.trans = trans;
    }

    public String getAccountNo() {
        return accountNo;
    }
    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
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


}



