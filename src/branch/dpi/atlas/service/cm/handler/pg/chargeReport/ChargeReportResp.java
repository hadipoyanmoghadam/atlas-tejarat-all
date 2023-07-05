package branch.dpi.atlas.service.cm.handler.pg.chargeReport;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 8/27/18.
 */


@XmlType(propOrder ={"actioncode","desc","rrn","accountNo","trans","charges","totalAmount","respdatetime"})
@XmlRootElement(name = "CHARGE_REPORT_RESPONSE")
public class ChargeReportResp {

    protected String rrn;
    protected String desc;
    protected String actioncode;
    protected String respdatetime;
    protected String accountNo;
    protected String totalAmount;
    protected ChargeReportList charges;
    protected TransactionList trans;

    public String getTotalAmount() {
        return totalAmount;
    }
    @XmlElement(name = "TOTAL_AMNT")
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
    @XmlElement(name = "CHARGES")
    public void setCharges(ChargeReportList charges) {
        this.charges = charges;
    }

    public ChargeReportList getCharges() {
        return charges;
    }

    public TransactionList getTrans() {
        return trans;
    }
    @XmlElement(name = "TRANSACTIONS")
    public void setTrans(TransactionList trans) {
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



