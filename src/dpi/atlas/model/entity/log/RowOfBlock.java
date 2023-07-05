package dpi.atlas.model.entity.log;


/**
 * User: F.Heydari
 * Date: Jun 9, 2020
 * Time: 09:40 AM
 */
public class RowOfBlock {

    private int batchPk;
    private String cardNo;
    private String nationalCode;
    private String serviceType;
    private String proccessStatus;
    private String amount;
    private String hostId;
    private String blockNo;
    private String userId;
    private String actionCode;
    private String effectiveDate;
    private String effectiveTime;
    private String accountType;
    private String branchCode;
    private String accountNo;
    private String blockDesc;
    private String unblockDesc;

    public int getBatchPk() {
        return batchPk;
    }

    public void setBatchPk(int batchPk) {
        this.batchPk = batchPk;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getProccessStatus() {
        return proccessStatus;
    }

    public void setProccessStatus(String proccessStatus) {
        this.proccessStatus = proccessStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getBlockNo() {
        return blockNo;
    }

    public void setBlockNo(String blockNo) {
        this.blockNo = blockNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBlockDesc() {
        return blockDesc;
    }

    public void setBlockDesc(String blockDesc) {
        this.blockDesc = blockDesc;
    }

    public String getUnblockDesc() {
        return unblockDesc;
    }

    public void setUnblockDesc(String unblockDesc) {
        this.unblockDesc = unblockDesc;
    }
}
