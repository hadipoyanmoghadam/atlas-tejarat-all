package branch.dpi.atlas.service.cm.handler.pg.smsRegister;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SMS_REGISTER")
public class SMSRegisterReq {
    protected String rrn;
    protected String cardNo;
    protected String accountNo;
    protected String parentNotify;
    protected String childNotify;
    protected String chargeNotify;
    protected String transNotify;

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getCardNo() {
        return cardNo;
    }

    @XmlElement(name = "CARDNO")
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getParentNotify() {
        return parentNotify;
    }
    @XmlElement(name = "PARENT_NOTIFY")
    public void setParentNotify(String parentNotify) {
        this.parentNotify = parentNotify;
    }

    public String getChildNotify() {
        return childNotify;
    }
    @XmlElement(name = "CHILD_NOTIFY")
    public void setChildNotify(String childNotify) {
        this.childNotify = childNotify;
    }

    public String getChargeNotify() {
        return chargeNotify;
    }
    @XmlElement(name = "CHARGE_NOTIFY")
    public void setChargeNotify(String chargeNotify) {
        this.chargeNotify = chargeNotify;
    }

    public String getTransNotify() {
        return transNotify;
    }
    @XmlElement(name = "TRANS_NOTIFY")
    public void setTransNotify(String transNotify) {
        this.transNotify = transNotify;
    }
}
