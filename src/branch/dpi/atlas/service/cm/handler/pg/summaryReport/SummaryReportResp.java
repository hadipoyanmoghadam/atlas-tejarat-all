package branch.dpi.atlas.service.cm.handler.pg.summaryReport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by R.Nasiri on 11/24/19.
 */


@XmlType(propOrder ={"actioncode","desc","rrn","cardno","chargeAmount","dChargeAmount","transactionAmountC","transactionAmountD","respdatetime"})
@XmlRootElement(name = "CHR_TRANS_SUMMARY_RESPONSE")
public class SummaryReportResp {

    protected String actioncode;
    protected String desc;
    protected String rrn;
    protected String cardno;
    protected String chargeAmount;
    protected String dChargeAmount;
    protected String transactionAmountC;
    protected String transactionAmountD;
    protected String respdatetime;


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

    public String getCardno() {
        return cardno;
    }

    @XmlElement(name = "CARDNO")
    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getChargeAmount() {
        return chargeAmount;
    }

    @XmlElement(name = "CHARGE_AMOUNT")
    public void setChargeAmount(String chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getdChargeAmount() {
        return dChargeAmount;
    }

    @XmlElement(name = "DCHARGE_AMOUNT")
    public void setdChargeAmount(String dChargeAmount) {
        this.dChargeAmount = dChargeAmount;
    }

    public String getTransactionAmountC() {
        return transactionAmountC;
    }

    @XmlElement(name = "CR_TRANS_AMOUNT")
    public void setTransactionAmountC(String transactionAmountC) {
        this.transactionAmountC = transactionAmountC;
    }

    public String getTransactionAmountD() {
        return transactionAmountD;
    }

    @XmlElement(name = "DB_TRANS_AMOUNT")
    public void setTransactionAmountD(String transactionAmountD) {
        this.transactionAmountD = transactionAmountD;
    }
}



