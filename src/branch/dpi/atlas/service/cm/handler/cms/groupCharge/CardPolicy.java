package branch.dpi.atlas.service.cm.handler.cms.groupCharge;


import branch.dpi.atlas.service.cm.handler.cms.createBatch.CreateGroupCardReq;
import branch.dpi.atlas.service.cm.handler.pg.policyHistory.PolicyHistoryReq;
import dpi.atlas.util.Constants;

public class CardPolicy {
    protected String sessionId;
    protected String cardno;
    protected CardNoList cardnoList;
    protected String amount;
    protected String accountNo;
    protected String type;
    protected String isDone;
    protected String startDate;
    protected String endDate;
    protected String nextDate;
    protected int count;
    protected int interval;
    protected String intervalType;

    public CardPolicy() {
    }

    public CardPolicy(String sessionId, String cardno, String amount, String accountNo, String type, String startDate, String endDate, String nextDate, int count, int interval, String isDone, String intervalType) {
        this.sessionId = sessionId;
        this.cardno = cardno;
        this.amount = amount;
        this.accountNo = accountNo;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.nextDate = nextDate;
        this.count = count;
        this.interval = interval;
        this.intervalType = intervalType;
        this.isDone = isDone;
    }

    public CardPolicy getPolicyMsg(Object obj, String msgType) {
        if (msgType.equalsIgnoreCase(Constants.POLICY_CREATE_ELEMENT)) {
            CreatePolicyReq policyReq = (CreatePolicyReq) obj;
            this.cardno = policyReq.getCardno();
            this.amount = policyReq.getAmount();
            this.accountNo = policyReq.getAccountNo();
            this.type = policyReq.getType();
            this.startDate = policyReq.getStartDate();
            this.count = policyReq.getCount();
            this.interval = policyReq.getInterval();
            this.intervalType = policyReq.getIntervalType();

        } else if (msgType.equalsIgnoreCase(Constants.POLICY_UPDATE_ELEMENT)) {
            UpdatePolicyReq policyReq = (UpdatePolicyReq) obj;
            this.cardno = policyReq.getCardno();
            this.amount = policyReq.getAmount();
            this.accountNo = policyReq.getAccountNo();
            this.type = policyReq.getType();
            this.startDate = policyReq.getStartDate();
            this.count = policyReq.getCount();
            this.interval = policyReq.getInterval();
            this.intervalType = policyReq.getIntervalType();

        } else if (msgType.equalsIgnoreCase(Constants.POLICY_UPDATE_ALL_ELEMENT)) {
            UpdateAllPolicyReq policyReq = (UpdateAllPolicyReq) obj;
            this.cardno = "";
            this.amount = policyReq.getAmount();
            this.accountNo = policyReq.getAccountNo();
            this.type = policyReq.getType();
            this.startDate = policyReq.getStartDate();
            this.count = policyReq.getCount();
            this.interval = policyReq.getInterval();
            this.intervalType = policyReq.getIntervalType();
        } else if (msgType.equalsIgnoreCase(Constants.POLICY_UPDATE_COLLECTION_ELEMENT)) {
            UpdateCollectionPolicyReq policyReq = (UpdateCollectionPolicyReq) obj;
            this.cardnoList = policyReq.getCardno();
            this.amount = policyReq.getAmount();
            this.accountNo = policyReq.getAccountNo();
            this.type = policyReq.getType();
            this.startDate = policyReq.getStartDate();
            this.count = policyReq.getCount();
            this.interval = policyReq.getInterval();
            this.intervalType = policyReq.getIntervalType();
        } else if (msgType.equalsIgnoreCase(Constants.POLICY_PRESENTATION_ELEMENT)) {
            PresentationPolicyReq policyReq = (PresentationPolicyReq) obj;
            this.accountNo = policyReq.getAccountNo();
            this.cardno = policyReq.getCardno();
        } else if (msgType.equalsIgnoreCase(Constants.POLICY_ENDED_PRESENTATION_ELEMENT)) {
            PresentationEndedPolicyReq policyReq = (PresentationEndedPolicyReq) obj;
            this.accountNo = policyReq.getAccountNo();
            this.cardno = policyReq.getCardno();

        } else if (msgType.equalsIgnoreCase(Constants.POLICY_CREATE_BATCH_ELEMENT)) {
            CreateGroupCardReq policyReq = (CreateGroupCardReq) obj;
            this.cardno = policyReq.getCardno();
            this.amount = policyReq.getAmount();
            this.accountNo = policyReq.getAccountNo();
            this.type = policyReq.getType();
            this.startDate = policyReq.getStartDate();
            this.count = policyReq.getCount();
            this.interval = policyReq.getInterval();
            this.intervalType = policyReq.getIntervalType();

        } else if (msgType.equalsIgnoreCase(Constants.POLICY_HISTORY_ELEMENT)) {
            PolicyHistoryReq policyReq = (PolicyHistoryReq) obj;
            this.accountNo = policyReq.getAccountNo();
            this.cardno = policyReq.getCardno();
        }
        return this;
    }

    public CardNoList getCardnoList() {
        return cardnoList;
    }

    public void setCardnoList(CardNoList cardnoList) {
        this.cardnoList = cardnoList;
    }

    public String getIsDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getNextDate() {
        return nextDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(String intervalType) {
        this.intervalType = intervalType;
    }
}



