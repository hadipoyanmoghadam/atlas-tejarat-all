package branch.dpi.atlas.service.cm.handler.pg.wfp;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 7/1/19.
 */


@XmlType(propOrder ={"actioncode","desc","rrn","accountNo","charges","totalAmount","respdatetime"})
@XmlRootElement(name = "WFP_CHARGE_RESPONSE")
public class ChargeResp {

    protected String rrn;
    protected String desc;
    protected String actioncode;
    protected String respdatetime;
    protected String accountNo;
    protected String totalAmount;
    protected WFPChargeList charges;


    public String getTotalAmount() {
        return totalAmount;
    }
    @XmlElement(name = "REVOKED_TOTAL_AMNT")
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
    @XmlElement(name = "CHARGES")
    public void setCharges(WFPChargeList charges) {
        this.charges = charges;
    }

    public WFPChargeList getCharges() {
        return charges;
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



