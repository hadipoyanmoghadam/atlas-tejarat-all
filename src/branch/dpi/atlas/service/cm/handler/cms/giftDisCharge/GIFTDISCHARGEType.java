package branch.dpi.atlas.service.cm.handler.cms.giftDisCharge;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "DISCHARGE_GIFTCARD")
public class GIFTDISCHARGEType {
    @XmlPath( "RRN/text()")
    protected String rrn;
    @XmlPath( "BRANCH_CODE/text()")
    protected String branchCode;
    @XmlPath( "REQUEST_NO/text()")
    protected String requestNo;
    @XmlPath( "CREATION_DATE/text()")
    protected String creationDate;
    @XmlPath( "CREATION_TIME/text()")
    protected String creationTime;
    @XmlPath("CARDNO/text()")
    protected String cardno;
    @XmlPath("AMOUNT/text()")
    protected String amount;

    public String getRrn() {
        return rrn;
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
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
}
