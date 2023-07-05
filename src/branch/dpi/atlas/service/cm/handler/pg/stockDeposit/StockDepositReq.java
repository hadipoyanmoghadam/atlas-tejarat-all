package branch.dpi.atlas.service.cm.handler.pg.stockDeposit;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by R.Nasiri on 02/04/2020.
 */
@XmlRootElement(name = "STOCK_DEPOSIT")
public class StockDepositReq {

    protected String rrn;
    protected String srcAccountNo;
    protected String destAccountNo;
    protected String amount;
    protected String transDate;
    protected String transTime;

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getSrcAccountNo() {
        return srcAccountNo;
    }
    @XmlElement(name = "SRC_ACCOUNT_NO")
    public void setSrcAccountNo(String srcAccountNo) {
        this.srcAccountNo = srcAccountNo;
    }

    public String getDestAccountNo() {
        return destAccountNo;
    }
    @XmlElement(name = "DEST_ACCOUNT_NO")
    public void setDestAccountNo(String destAccountNo) {
        this.destAccountNo = destAccountNo;
    }

    public String getAmount() {
        return amount;
    }
    @XmlElement(name = "AMOUNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransDate() {
        return transDate;
    }
    @XmlElement(name = "TRANS_DATE")
    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransTime() {
        return transTime;
    }
    @XmlElement(name = "TRANS_TIME")
    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }
}
