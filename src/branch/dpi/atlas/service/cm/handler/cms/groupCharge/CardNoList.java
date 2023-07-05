package branch.dpi.atlas.service.cm.handler.cms.groupCharge;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


@XmlType(propOrder ={"cardNo"})
@XmlRootElement(name = "CARDS")
public class CardNoList {

    protected List<String> cardNo;

    public List<String> getCardNo() {
        return cardNo;
    }

    @XmlElement(name = "CARDNO")
    public void setCardNo(List<String> cardNo) {
        this.cardNo = cardNo;
    }

}




