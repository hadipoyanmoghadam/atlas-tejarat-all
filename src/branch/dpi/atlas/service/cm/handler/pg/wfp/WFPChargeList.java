package branch.dpi.atlas.service.cm.handler.pg.wfp;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by sh.Behnaz on 7/1/19.
 */


@XmlType(propOrder ={"charge"})
@XmlRootElement(name = "CHARGES")
public class WFPChargeList {

    protected List<WFPCharge> charge;

    public List<WFPCharge> getCharge() {
        return charge;
    }
    @XmlElement(name = "CHARGE")
       public void setCharge(List<WFPCharge> charge) {
        this.charge = charge;
    }
}


