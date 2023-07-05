package branch.dpi.atlas.service.cm.handler.pg.accountStatement;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * Created by R.Nasiri on 11/24/19.
 */


@XmlType(propOrder ={"tx"})
@XmlRootElement(name = "TRANSACTIONS")
public class AccountStatementTxList {

    protected List<AccountStatementTx> tx;

    public List<AccountStatementTx> getTx() {
        return tx;
    }
    @XmlElement(name = "TRANSACTION")
    public void setTx(List<AccountStatementTx> tx) {
        this.tx = tx;
    }
}



