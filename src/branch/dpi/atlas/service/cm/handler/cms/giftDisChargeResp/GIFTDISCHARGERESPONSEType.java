package branch.dpi.atlas.service.cm.handler.cms.giftDisChargeResp;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "DISCHARGE_GIFTCARD_RESPONSE")
public class GIFTDISCHARGERESPONSEType {

    @XmlPath( "ACTIONCODE/text()")
    protected String actioncode;
    @XmlPath( "DESC/text()")
    protected String desc;
    @XmlPath( "RRN/text()")
    protected String rrn;
    @XmlPath( "RESPDATETIME/text()")
    protected String respdatetime;
    @XmlPath( "REQUEST_NO/text()")
    protected String requestNo;
    @XmlPath("CARDNO/text()")
    protected String cardno;

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

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getRespdatetime() {
        return respdatetime;
    }

    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }
}
