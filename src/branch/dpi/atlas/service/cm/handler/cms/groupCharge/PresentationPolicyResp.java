package branch.dpi.atlas.service.cm.handler.cms.groupCharge;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"actioncode", "desc", "rrn", "cardNo", "accountNo", "type", "amount", "startDate", "count", "interval","intervalType", "nextDate", "respdatetime"})

@XmlRootElement(name = "POLICY_PRESENTATION_RESPONSE")
public class PresentationPolicyResp {

    protected String actioncode;
    protected String desc;
    protected String rrn;
    protected String accountNo;
    protected String cardNo;
    protected String respdatetime;
    protected String type;
    protected String startDate;
    protected String nextDate;
    protected int count;
    protected int interval;
    protected String intervalType;
    protected String amount;


    public String getNextDate() {
        return nextDate;
    }

    @XmlElement(name = "CHARGE_NEXTDATE")
    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }

    public String getActioncode() {
        return actioncode;
    }

    @XmlElement(name = "ACTIONCODE")
    public void setActioncode(String actioncode) {
        this.actioncode = actioncode;
    }

    public String getDesc() {
        return desc;
    }

    @XmlElement(name = "DESC")
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }


    public String getAccountNo() {
        return accountNo;
    }

    @XmlElement(name = "ACCOUNT_NO")

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    @XmlElement(name = "CARDNO")
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getRespdatetime() {
        return respdatetime;
    }

    @XmlElement(name = "RESPDATETIME")
    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
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

    @XmlElement(name = "CHARGE_INTERVAL")
    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public String getAmount() {
        return amount;
    }

    @XmlElement(name = "AMOUNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getIntervalType() {
        return intervalType;
    }

    @XmlElement(name = "INTERVAL_TYPE")
    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }


}
