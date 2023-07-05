package branch.dpi.atlas.service.cm.handler.cms.giftBalance;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "BALANCE_GIFTCARD")
public class GiftBalanceReq {
    protected String rrn;
    protected String branchCode;

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
}
