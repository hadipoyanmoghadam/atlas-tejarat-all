package branch.dpi.atlas.service.cm.handler.pg.wfp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 7/1/19.
 */


@XmlType(propOrder ={"fileName","amount","requestType","chargeDate","chargeTime"})
@XmlRootElement(name = "CHARGE")
public class WFPCharge {

    protected String fileName;
    protected String amount;
    protected String chargeDate;
    protected String chargeTime;
    protected String requestType;

    public String getAmount() {
        return amount;
    }
    @XmlElement(name = "AMNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChargeDate() {
        return chargeDate;
    }
    @XmlElement(name = "CHARGE_DT")
    public void setChargeDate(String chargeDate) {
        this.chargeDate = chargeDate;
    }

    public String getFileName() {
        return fileName;
    }
    @XmlElement(name = "FILENAME")
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getChargeTime() {
        return chargeTime;
    }
    @XmlElement(name = "CHARGE_TM")
    public void setChargeTime(String chargeTime) {
        this.chargeTime = chargeTime;
    }

    public String getRequestType() {
        return requestType;
    }
    @XmlElement(name = "REQUEST_TYPE")
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}


