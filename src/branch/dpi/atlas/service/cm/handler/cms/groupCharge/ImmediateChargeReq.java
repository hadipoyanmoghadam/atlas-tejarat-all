package branch.dpi.atlas.service.cm.handler.cms.groupCharge;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IMMEDIATE_CHARGE")
public class ImmediateChargeReq {
    protected String rrn;
    protected String cardNo;
    protected String accountNo;
    protected String amount;

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getCardNo() {
        return cardNo;
    }

    @XmlElement(name = "CARDNO")
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAmount() {
        return amount;
    }
    @XmlElement(name = "AMOUNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }
}

