package branch.dpi.atlas.service.cm.handler.cms.balanceResp;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "BALANCE_RESPONSE")
public class BALANCERESPONSEType {

    @XmlPath( "ACTIONCODE/text()")
    protected String actioncode;
    @XmlPath( "DESC/text()")
    protected String desc;
    @XmlPath( "RRN/text()")
    protected String rrn;
    @XmlPath( "BALANCE/text()")
    protected String balance;
    @XmlPath( "ACCOUNT_NO/text()")
    protected String accountNo;
    @XmlPath("ACCOUNT_GROUP/text()")
    protected String accountgroup;
    @XmlPath( "RESPDATETIME/text()")
    protected String respdatetime;


    public String getActioncode() {
        return actioncode;
    }

    public void setActioncode(String actioncode) {
        this.actioncode = actioncode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getRespdatetime() {
        return respdatetime;
    }

    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }

    public String getAccountgroup() {
        return accountgroup;
    }

    public void setAccountgroup(String accountgroup) {
        this.accountgroup = accountgroup;
    }
}
