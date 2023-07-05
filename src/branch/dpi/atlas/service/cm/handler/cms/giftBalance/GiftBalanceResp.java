package branch.dpi.atlas.service.cm.handler.cms.giftBalance;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"actioncode", "desc", "rrn", "accountNo","accountgroup", "branchDocNo", "branchCode", "transDate", "transAmount", "respdatetime"})

@XmlRootElement(name = "BALANCE_GIFTCARD_RESPONSE")
public class GiftBalanceResp {

    protected String actioncode;

    protected String desc;

    protected String rrn;

    protected String respdatetime;
    protected String accountNo;
    protected String accountgroup;
    protected String branchDocNo;
    protected String branchCode;
    protected String transDate;
    protected String transAmount;

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

    public String getRespdatetime() {
        return respdatetime;
    }

    @XmlElement(name = "RESPDATETIME")
    public void setRespdatetime(String respdatetime) {
        this.respdatetime = respdatetime;
    }

    public String getAccountNo() {
        return accountNo;
    }

    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBranchDocNo() {
        return branchDocNo;
    }

    @XmlElement(name = "BRANCH_DOCNO")
    public void setBranchDocNo(String branchDocNo) {
        this.branchDocNo = branchDocNo;
    }

    public String getBranchCode() {
        return branchCode;
    }

    @XmlElement(name = "BRANCH_CODE")
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getTransDate() {
        return transDate;
    }

    @XmlElement(name = "TRANS_DATE")
    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransAmount() {
        return transAmount;
    }

    @XmlElement(name = "TRANS_AMOUNT")
    public void setTransAmount(String transAmount) {
        this.transAmount = transAmount;
    }

    public String getAccountgroup() {
        return accountgroup;
    }
    @XmlElement(name = "ACCOUNT_GROUP")
    public void setAccountgroup(String accountgroup) {
        this.accountgroup = accountgroup;
    }
}
