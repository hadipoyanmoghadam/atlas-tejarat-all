package branch.dpi.atlas.service.cm.handler.cms.giftDeposit;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DEPOSIT_GIFTCARD")
public class GiftDepositReq {
    protected String rrn;
    protected String branchCode;
    protected String requestNo;
    protected String branchDocNo;
    protected String transDate;
    protected String amount;


    public String getAmount() {
        return amount;
    }

    @XmlElement(name = "TRANS_AMOUNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getBranchCode() {
        return branchCode;
    }

    @XmlElement(name = "BRANCH_CODE")
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getRequestNo() {
        return requestNo;
    }

    @XmlElement(name = "REQUEST_NO")
    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getBranchDocNo() {
        return branchDocNo;
    }

    @XmlElement(name = "BRANCH_DOCNO")
    public void setBranchDocNo(String branchDocNo) {
        this.branchDocNo = branchDocNo;
    }

    public String getTransDate() {
        return transDate;
    }

    @XmlElement(name = "TRANS_DATE")
    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }
}



