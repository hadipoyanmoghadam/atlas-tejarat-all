package branch.dpi.atlas.service.cm.handler.pg.policyHistory;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 8/27/18.
 */


@XmlType(propOrder ={"type","amount","startDate","count","interval","intervalType","changeDate","changeTime","channelType","chargeStatus"})
@XmlRootElement(name = "POLICY")
public class Policy {

    protected String type;
    protected String amount;
    protected String startDate;
    protected int count;
    protected int interval;
    protected String  intervalType;
    protected String changeDate;
    protected String changeTime;
    protected String channelType;
    protected String chargeStatus;
//    protected String isDone;

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
    @XmlElement(name = "COUNT")
    public void setCount(int count) {
        this.count = count;
    }

    public int getInterval() {
        return interval;
    }
    @XmlElement(name = "INTERVAL")
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

    public String getChangeDate() {
        return changeDate;
    }
    @XmlElement(name = "CHANGE_DATE")
    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeTime() {
        return changeTime;
    }
    @XmlElement(name = "CHANGE_TIME")
    public void setChangeTime(String changeTime) {
        this.changeTime = changeTime;
    }

    public String getChannelType() {
        return channelType;
    }
    @XmlElement(name = "CHANNEL_TYPE")
    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getAmount() {
        return amount;
    }
    @XmlElement(name = "AMOUNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }
    @XmlElement(name = "CHARGE_TYPE")
    public void setType(String type) {
        this.type = type;
    }

    public String getChargeStatus() {
        return chargeStatus;
    }

    @XmlElement(name = "CHARGE_STATUS")
    public void setChargeStatus(String chargeStatus) {
        this.chargeStatus = chargeStatus;
    }

    //    public String getIsDone() {
//        return isDone;
//    }
//
//    public void setIsDone(String isDone) {
//        this.isDone = isDone;
//    }
}


