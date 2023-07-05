package branch.dpi.atlas.service.cm.handler.pg.cardBalance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 03/29/17.
 */


@XmlType(propOrder ={"actioncode","desc","rrn","cardno","availableBalance","availableBalanceSign","actualBalance","actualBalanceSign","respdatetime"})
@XmlRootElement(name = "BALANCE_GROUPCARD_RESPONSE")
public class CardBalanceResp {

    protected String rrn;
    protected String cardno;
    protected String desc;
    protected String actioncode;
    protected String respdatetime;
    protected String availableBalance;
    protected String availableBalanceSign;
    protected String actualBalance;
    protected String actualBalanceSign;

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

    public String getAvailableBalance() {
        return availableBalance;
    }
    @XmlElement(name = "AVAILABLEBALANCE")
    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getAvailableBalanceSign() {
        return availableBalanceSign;
    }
    @XmlElement(name = "AVAILABLEBALANCE_SIGN")
    public void setAvailableBalanceSign(String availableBalanceSign) {
        this.availableBalanceSign = availableBalanceSign;
    }

    public String getActualBalance() {
        return actualBalance;
    }
    @XmlElement(name = "ACTUALBALANCE")
    public void setActualBalance(String actualBalance) {
        this.actualBalance = actualBalance;
    }

    public String getActualBalanceSign() {
        return actualBalanceSign;
    }
    @XmlElement(name = "ACTUALBALANCE_SIGN")
    public void setActualBalanceSign(String actualBalanceSign) {
        this.actualBalanceSign = actualBalanceSign;
    }
}


