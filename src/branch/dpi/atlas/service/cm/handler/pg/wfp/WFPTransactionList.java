package branch.dpi.atlas.service.cm.handler.pg.wfp;


import branch.dpi.atlas.service.cm.handler.pg.chargeReport.Transaction;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by sh.Behnaz on 7/1/19.
 */


@XmlType(propOrder ={"trans"})
@XmlRootElement(name = "TRANSACTIONS")
public class WFPTransactionList {

    protected List<WFPTransaction> trans;

    public List<WFPTransaction> getTrans() {
        return trans;
    }
    @XmlElement(name = "TRANSACTION")
    public void setTrans(List<WFPTransaction> trans) {
        this.trans = trans;
    }

}


