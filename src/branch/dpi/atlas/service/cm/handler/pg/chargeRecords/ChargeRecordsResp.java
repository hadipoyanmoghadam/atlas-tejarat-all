package branch.dpi.atlas.service.cm.handler.pg.chargeRecords;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 11/25/17.
 */


@XmlType(propOrder ={"actioncode","desc","rrn","cardno","accountNo","charges","respdatetime"})
@XmlRootElement(name = "CHARGE_PRESENTATION_RESPONSE")
public class ChargeRecordsResp {

    protected String rrn;
    protected String cardno;
    protected String desc;
    protected String actioncode;
    protected String respdatetime;
    protected String accountNo;
    protected ChargeList charges;


    public String getAccountNo() {
        return accountNo;
    }
    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public ChargeList getCharges() {
        return charges;
    }
    @XmlElement(name = "CHARGES")
    public void setCharges(ChargeList charges) {
        this.charges = charges;
    }

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getCardno() {
        return cardno;
    }

    @XmlElement(name = "CARDNO")
    public void setCardno(String cardno) {
        this.cardno = cardno;
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



