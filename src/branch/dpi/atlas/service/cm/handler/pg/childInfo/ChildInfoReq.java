package branch.dpi.atlas.service.cm.handler.pg.childInfo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sh.Behnaz on 11/17/18.
 */
@XmlRootElement(name = "CHILDINFO")
public class ChildInfoReq {
    protected String rrn;
    protected String cardno;

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getCardno() {
        return cardno;
    }

    @XmlElement(name = "CARD_NO")
    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

}
