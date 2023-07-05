package branch.dpi.atlas.service.cm.handler.cms.reCreateResp;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RE_CREATE_RESPONSE")

public class RECREATERESPONSEType {

    @XmlPath("ACTIONCODE/text()")
    protected String actioncode;
    @XmlPath("DESC/text()")
    protected String desc;
    @XmlPath("RRN/text()")
    protected String rrn;
    @XmlPath("NEW_CARDNO/text()")
    protected String newcardno;
    @XmlPath("RESPDATETIME/text()")
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

    public String getNewcardno() {
        return newcardno;
    }

    public void setNewcardno(String newcardno) {
        this.newcardno = newcardno;
    }

    public String getRespdatetime() {
        return respdatetime;
    }

    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }
}
