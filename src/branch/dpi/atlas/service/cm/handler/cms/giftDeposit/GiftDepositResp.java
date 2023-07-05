package branch.dpi.atlas.service.cm.handler.cms.giftDeposit;


import javax.xml.bind.annotation.*;

@XmlType(propOrder = {"actioncode", "desc", "rrn", "respdatetime"})

@XmlRootElement(name = "DEPOSIT_GIFTCARD_RESPONSE")
public class GiftDepositResp {

    protected String actioncode;

    protected String desc;

    protected String rrn;

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

    public String getRespdatetime() {
        return respdatetime;
    }

    @XmlElement(name = "RESPDATETIME")
    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }

}
