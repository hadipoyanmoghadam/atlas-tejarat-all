package branch.dpi.atlas.service.cm.handler.pg.chargeRecords;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 11/21/17.
 */


@XmlType(propOrder ={"chargeActionCode","type","amount","chargeDate","efectiveDate","requestType"})
@XmlRootElement(name = "CHARGE")
public class Charge {

    protected String type;
    protected String amount;
    protected String chargeDate;
    protected String chargeActionCode;
    protected String efectiveDate;
    protected String requestType;

    public String getChargeActionCode() {
        return chargeActionCode;
    }

    @XmlElement(name = "CHARGE_ACTIONCODE")
    public void setChargeActionCode(String chargeActionCode) {
        this.chargeActionCode = chargeActionCode;
    }

    public String getAmount() {
        return amount;
    }
    @XmlElement(name = "AMOUNT")
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }
    @XmlElement(name = "CHARGE_TYPE")
    public void setType(String type) {
        this.type = type;
    }

    public String getChargeDate() {
        return chargeDate;
    }
    @XmlElement(name = "CHARGE_DATE")
    public void setChargeDate(String chargeDate) {
        this.chargeDate = chargeDate;
    }

    public String getEfectiveDate() {
        return efectiveDate;
    }
    @XmlElement(name = "EFECTIVE_DATE")
    public void setEfectiveDate(String efectiveDate) {
        this.efectiveDate = efectiveDate;
    }

    public String getRequestType() {
        return requestType;
    }
    @XmlElement(name = "REQUEST_TYPE")
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}


