package branch.dpi.atlas.service.cm.handler.pg.smsRegister;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"actioncode", "desc", "rrn", "cardNo", "accountNo","parentNotify","childNotify","chargeNotify","transNotify","respdatetime"})

@XmlRootElement(name = "SMS_INQUIRY_RESPONSE")
public class SMSInquiryResp {

    protected String actioncode;
    protected String desc;
    protected String rrn;
    protected String accountNo;
    protected String cardNo;
    protected String respdatetime;
    protected String parentNotify;
    protected String childNotify;
    protected String chargeNotify;
    protected String transNotify;

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

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getAccountNo() {
        return accountNo;
    }

    @XmlElement(name = "ACCOUNT_NO")

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    @XmlElement(name = "CARDNO")
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
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

    public String getRespdatetime() {
        return respdatetime;
    }

    @XmlElement(name = "RESPDATETIME")
    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }

}
