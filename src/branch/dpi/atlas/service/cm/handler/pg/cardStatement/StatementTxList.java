package branch.dpi.atlas.service.cm.handler.pg.cardStatement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by sh.Behnaz on 03/29/17.
 */


@XmlType(propOrder ={"tx"})
@XmlRootElement(name = "TRANSACTIONS")
public class StatementTxList {

    protected List<StatementTx> tx;

    public List<StatementTx> getTx() {
        return tx;
    }
    @XmlElement(name = "TRANSACTION")
    public void setTx(List<StatementTx> tx) {
        this.tx = tx;
    }
}



