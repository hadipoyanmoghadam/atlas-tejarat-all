package branch.dpi.atlas.model.tj.entity.cms;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sh.Behnaz on 10/27/16.
 */
@XmlRootElement(name = "REVOKE")
public class RevokeReq {

    protected String rrn;
    protected String cardno;
    protected String row;

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

    @XmlElement(name = "CARDNO")
    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getRow() {
        return row;
    }

    @XmlElement(name = "ROW")
    public void setRow(String row) {
        this.row = row;
    }
}
