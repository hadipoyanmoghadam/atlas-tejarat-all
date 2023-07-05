package branch.dpi.atlas.service.cm.handler.pg.chargeReport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by sh.Behnaz on 11/25/17.
 */
@XmlRootElement(name = "CHARGE_REPORT")
public class ChargeReportReq {

    protected String rrn;
    protected String accountNo;
    protected String fromDate;
    protected String toDate;

    public String getRrn() {
        return rrn;
    }

    @XmlElement(name = "RRN")
    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    public String getAccountNo() {
        return accountNo;
    }

    @XmlElement(name = "ACCOUNT_NO")
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getFromDate() {
        return fromDate;
    }

    @XmlElement(name = "FROMDATE")
    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    @XmlElement(name = "TODATE")
    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
