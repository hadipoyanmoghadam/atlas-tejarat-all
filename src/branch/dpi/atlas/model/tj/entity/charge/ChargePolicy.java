package branch.dpi.atlas.model.tj.entity.charge;

/**
 * Created by shiva on 10/29/17.
 */
public class ChargePolicy {
    private Long LogId;
    private String sessionId;
    private String cardNO;
    private String account_no;
    private String creation_date;
    private String creation_time;
    private String type;
    private String startDate;
    private String endDate;
    private String nextDate;
    private int count;
    private Long amount;
    private int interval;
    private String isDone;
    private String  intervalType;

    public Long getLogId() {
        return LogId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getCardNO() {
        return cardNO;
    }

    public String getAccount_no() {
        return account_no;
    }

    public String getCreationDate() {
        return creation_date;
    }

    public String getCreationTime() {
        return creation_time;
    }

    public String getType() {
        return type;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setLogId(Long logId) {
        LogId = logId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setCardNO(String cardNO) {
        this.cardNO = cardNO;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public void setCreationDate(String creation_date) {
        this.creation_date = creation_date;
    }

    public void setCreationTime(String creation_time) {
        this.creation_time = creation_time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public String getNextDate() {
        return nextDate;

    }

    public int getCount() {
        return count;
    }

    public Long getAmount() {
        return amount;
    }

    public int getInterval() {
        return interval;
    }

    public String getIsDone() {
        return isDone;
    }
    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }
}
