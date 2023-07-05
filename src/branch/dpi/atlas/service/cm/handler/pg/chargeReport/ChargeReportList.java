package branch.dpi.atlas.service.cm.handler.pg.chargeReport;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by sh.Behnaz on 8/27/18.
 */


@XmlType(propOrder ={"charge"})
@XmlRootElement(name = "CHARGES")
public class ChargeReportList {

    protected List<ChargeReport> charge;

    public List<ChargeReport> getCharge() {
        return charge;
    }
    @XmlElement(name = "CHARGE")
       public void setCharge(List<ChargeReport> charge) {
        this.charge = charge;
    }
}


