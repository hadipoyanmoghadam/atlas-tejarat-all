package branch.dpi.atlas.service.cm.handler.cms.reCreate;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RE_CREATE")
public class RECREATEType {

    @XmlPath("RRN/text()")
    protected String rrn;
    @XmlPath("CARDNO/text()")
    protected String cardno;
    @XmlPath("NEW_CARDNO/text()")
    protected String newcardno;
    @XmlPath("EDIT_DATE/text()")
    protected String editdate;

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

    public String getNewcardno() {
        return newcardno;
    }

    public void setNewcardno(String newcardno) {
        this.newcardno = newcardno;
    }

    public String getEditdate() {
        return editdate;
    }

    public void setEditdate(String editdate) {
        this.editdate = editdate;
    }
}
