package branch.dpi.atlas.model.tj.entity.charge;

/**
 * Created by shiva on 10/29/17.
 */
public class ChargeLog {

    private int partNo;
    private String cardNO;
    private String accountNo;
    private Long amount;
    private String sessionId;
    private Long chargeBatchPk;
    private String chargeFileDate;
    private int chargeType;
    private String creationDate;
    private String creationTime;
    private String effectiveDate;
    private String effectiveTime;
    private String processStatus;
    private int tryCount;
    private String firstError;
    private String secondError;
    private String actionCode;


    public void setPartNo(int partNo) {
        this.partNo = partNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public void setChargeType(int chargeType) {
        this.chargeType = chargeType;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public void setChargeFileDate(String chargeFileDate) {
        this.chargeFileDate = chargeFileDate;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public void setChargeBatchPk(Long chargeBatchPk) {
        this.chargeBatchPk = chargeBatchPk;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setTryCount(int tryCount) {
        this.tryCount = tryCount;
    }

    public void setFirstError(String firstError) {
        this.firstError = firstError;
    }

    public void setSecondError(String secondError) {
        this.secondError = secondError;
    }

    public int getPartNo() {
        return partNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public int getChargeType() {
        return chargeType;
    }

    public Long getAmount() {
        return amount;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public String getChargeFillDate() {
        return chargeFileDate;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public Long getChargeBatchPk() {
        return chargeBatchPk;
    }

    public String getActionCode() {
        return actionCode;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getTryCount() {
        return tryCount;
    }

    public String getFirstError() {
        return firstError;
    }

    public String getSecondError() {
        return secondError;
    }

    public String getCardNO() {
        return cardNO;

    }

    public void setCardNO(String cardNO) {
        this.cardNO = cardNO;
    }

}
