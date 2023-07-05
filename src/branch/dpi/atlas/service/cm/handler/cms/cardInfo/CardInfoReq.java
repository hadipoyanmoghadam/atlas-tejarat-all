package branch.dpi.atlas.service.cm.handler.cms.cardInfo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="CARDINFO")
public class CardInfoReq {

    protected String rrn;
    protected String cardno;

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name="RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getCardno() {
        return cardno;
    }

    @XmlElement(name="CARD_NO")
    public void setCardno(String cardno) {
        this.cardno = cardno;
    }
}
