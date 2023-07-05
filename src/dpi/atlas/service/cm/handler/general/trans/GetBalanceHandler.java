package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.globus.ofs.convertor.NumberConvertor;
import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Map;

/**
 * BalanceHandler class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:28 $
 */
public class GetBalanceHandler extends HostHandlerBase {

    private static Log log = LogFactory.getLog(GetBalanceHandler.class);

/*
    private String user;
    private String pass;
*/

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
    }


    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside BalanceHandler:_post()");
        CMMessage msg = (CMMessage) o;    
        String acc_no = (String) msg.getAttribute(Fields.ACCOUNT_NO);
        acc_no = "40020101";

        HostResultSet hrs = null;
        HostResultSet hrs_ret = null;
        try {                            //("0000000", fromAccount, "CM", "000001", "", "");
            hrs = hostInterface.getBalance("0000000", acc_no, service, "", "", "");
            if (hrs.next()) {
                String bal = hrs.getString("AVAILABLEBALANCE");
                bal = NumberConvertor.convertFromISO(bal);
                hrs_ret = getHostResultSet(bal);
            }
        } catch (HostException e) {
            log.error(e);
            throw new CMFault(CMFault.FAULT_INVALID_CUSTOMER, e.getMessage());
        }
        return hrs_ret;
    }

    private HostResultSet getHostResultSet(String balance) throws HostException {
        int rowindex = 0;

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "BAL");
        rowindex++;
        md.addColumn(rowindex, "Currency");
        rowindex++;


        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();

        row.add(balance);
        row.add("IRR");

        hostResultSet.addRow(row);

        return hostResultSet;

    }


}

