package branch.dpi.atlas.service.cm.handler.cms.groupCharge;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"actioncode", "desc", "rrn", "accountNo", "respdatetime"})
@XmlRootElement(name = "POLICY_UPDATECOLLECTION_RESPONSE")
public class UpdateCollectionPolicyResp {

    protected String actioncode;
    protected String desc;
    protected String rrn;
    protected String accountNo;
    protected String respdatetime;

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

    public String getRespdatetime() {
        return respdatetime;
    }

    @XmlElement(name = "RESPDATETIME")
    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }
}
