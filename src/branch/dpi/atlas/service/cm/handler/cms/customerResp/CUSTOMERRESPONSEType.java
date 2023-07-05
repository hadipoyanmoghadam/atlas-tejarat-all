package branch.dpi.atlas.service.cm.handler.cms.customerResp;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CUSTOMER_RESPONSE")
public class CUSTOMERRESPONSEType {

    @XmlPath( "ACTIONCODE/text()")
    protected String actioncode;
    @XmlPath( "DESC/text()")
    protected String desc;
    @XmlPath( "RECORD_FLAG/text()")
    protected String recordflag;
    @XmlPath( "RRN/text()")
    protected String rrn;
    @XmlPath( "CARDNO/text()")
    protected String cardno;
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

    public String getRecordflag() {
        return recordflag;
    }

    public void setRecordflag(String recordflag) {
        this.recordflag = recordflag;
    }

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
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
