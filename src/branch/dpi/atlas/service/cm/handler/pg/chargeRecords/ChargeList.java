package branch.dpi.atlas.service.cm.handler.pg.chargeRecords;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by sh.Behnaz on 11/21/17.
 */


@XmlType(propOrder ={"charge"})
@XmlRootElement(name = "CHARGES")
public class ChargeList {

    protected List<Charge> charge;

    public List<Charge> getCharge() {
        return charge;
    }
    @XmlElement(name = "CHARGE")
       public void setCharge(List<Charge> charge) {
        this.charge = charge;
    }
}


