package branch.dpi.atlas.service.cm.handler.pg.policyHistory;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"actioncode", "desc", "rrn", "cardNo", "accountNo", "policies", "respdatetime"})
@XmlRootElement(name = "POLICYHISTORY_RESPONSE")
public class PolicyHistoryResp {

    protected String actioncode;
    protected String desc;
    protected String rrn;
    protected String accountNo;
    protected String cardNo;
    protected String respdatetime;
    protected PolicyList policies;

    public PolicyList getPolicies() {
        return policies;
    }
    @XmlElement(name = "POLICES")
    public void setPolicies(PolicyList policies) {
        this.policies = policies;
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

    public String getRespdatetime() {
        return respdatetime;
    }

    @XmlElement(name = "RESPDATETIME")
    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }

}
