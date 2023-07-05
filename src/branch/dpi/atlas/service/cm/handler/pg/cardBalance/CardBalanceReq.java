package branch.dpi.atlas.service.cm.handler.pg.cardBalance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sh.Behnaz on 3/29/17.
 */
@XmlRootElement(name = "BALANCE_GROUPCARD")
public class CardBalanceReq {

    protected String rrn;
    protected String cardno;
    protected String accountNo;

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

    public String getAccountNo() {
        return accountNo;
    }
    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
}
