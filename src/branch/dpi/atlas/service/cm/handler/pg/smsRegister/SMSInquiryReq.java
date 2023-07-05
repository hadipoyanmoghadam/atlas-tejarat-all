package branch.dpi.atlas.service.cm.handler.pg.smsRegister;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sh.Behnaz on 10/28/18.
 */
@XmlRootElement(name = "SMS_INQUIRY")
public class SMSInquiryReq {

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
