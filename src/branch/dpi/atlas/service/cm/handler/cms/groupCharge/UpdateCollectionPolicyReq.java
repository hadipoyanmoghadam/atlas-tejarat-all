package branch.dpi.atlas.service.cm.handler.cms.groupCharge;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "POLICY_UPDATECOLLECTION")
public class UpdateCollectionPolicyReq {
    protected String rrn;
    protected CardNoList cardno;
    protected String amount;
    protected String accountNo;
    protected String type;
    protected String startDate;
    protected int count;
    protected int interval;
    protected String  intervalType;

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public CardNoList getCardno() {
        return cardno;
    }

    @XmlElement(name = "CARDS")
    public void setCardno(CardNoList cardno) {
        this.cardno = cardno;
    }

    public String getAmount() {
        return amount;
    }

    @XmlElement(name = "AMOUNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAccountNo() {
        return accountNo;
    }

    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getType() {
        return type;
    }

    @XmlElement(name = "CHARGE_TYPE")
    public void setType(String type) {
        this.type = type;
    }

    public String getStartDate() {
        return startDate;
    }

    @XmlElement(name = "START_DATE")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public int getCount() {
        return count;
    }

    @XmlElement(name = "CHARGE_COUNT")
    public void setCount(int count) {
        this.count = count;
    }

    public int getInterval() {
        return interval;
    }

    @XmlElement(name = "CHARGE_INTERVAL")

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getIntervalType() {
        return intervalType;
    }
    @XmlElement(name = "INTERVAL_TYPE")
    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }

}



