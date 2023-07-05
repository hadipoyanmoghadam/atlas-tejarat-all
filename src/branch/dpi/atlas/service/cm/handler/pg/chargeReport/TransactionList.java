package branch.dpi.atlas.service.cm.handler.pg.chargeReport;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by sh.Behnaz on 8/27/18.
 */


@XmlType(propOrder ={"trans"})
@XmlRootElement(name = "TRANSACTIONS")
public class TransactionList {

    protected List<Transaction> trans;

    public List<Transaction> getTrans() {
        return trans;
    }
    @XmlElement(name = "TRANSACTION")
    public void setTrans(List<Transaction> trans) {
        this.trans = trans;
    }

}


