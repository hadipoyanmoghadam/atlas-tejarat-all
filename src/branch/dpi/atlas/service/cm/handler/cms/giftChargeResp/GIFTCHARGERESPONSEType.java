package branch.dpi.atlas.service.cm.handler.cms.giftChargeResp;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CHARGE_GIFTCARD_RESPONSE")
public class GIFTCHARGERESPONSEType {

    @XmlPath( "ACTIONCODE/text()")
    protected String actioncode;
    @XmlPath( "DESC/text()")
    protected String desc;
    @XmlPath( "RRN/text()")
    protected String rrn;
    @XmlPath( "REQUEST_NO/text()")
    protected String requestNo;
    @XmlPath( "CARDNO/text()")
    protected String cardNo;
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

    public String getRequestNo() {
        return requestNo;
    }
    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }
    public String getCardNo() {
        return cardNo;
    }
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
    public String getRespdatetime() {
        return respdatetime;
    }

    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }
}
