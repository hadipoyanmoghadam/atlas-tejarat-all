package branch.dpi.atlas.service.cm.handler.pg.chargeReport;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by sh.Behnaz on 8/27/18.
 */


@XmlType(propOrder ={"cardNo","amount","chargeDate","efectiveDate"})
@XmlRootElement(name = "CHARGE")
public class ChargeReport {

    protected String cardNo;
    protected String amount;
    protected String chargeDate;
    protected String efectiveDate;

    public String getCardNo() {
        return cardNo;
    }
    @XmlElement(name = "CARDNO")
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

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

    public String getEfectiveDate() {
        return efectiveDate;
    }
    @XmlElement(name = "EFECTIVE_DT")
    public void setEfectiveDate(String efectiveDate) {
        this.efectiveDate = efectiveDate;
    }

}


