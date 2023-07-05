package dpi.atlas.service.cm.handler.general.trans.branch;

import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: App 29, 2022
 * Time: 12:59 PM
 */

public class GetPichackCheckStatus extends HostHandlerBase {
    private static Log log = LogFactory.getLog(GetPichackCheckStatus.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside GetPichackCheckStatus:_post()");
        CMMessage msg = (CMMessage) o;
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        CreditsMessage creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");

        inputParams.put(Constants.SERVICE, msg.getAttributeAsString(CMMessage.SERVICE_TYPE));
        inputParams.put(Constants.ACC_NO, creditsMessage.getAccountNo());
        inputParams.put(Constants.CHEQUE_NUMBER, creditsMessage.getCheckNumber());

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {

            hrs = ((HostInterfaceNAB) hostInterface).getPichackChequeStatus(inputParams);
            if (hrs.next()) {
                hrs_ret = getHostResultSet(hrs);
            }
        } catch (HostException e) {
            String errorCode = Integer.toString(e.getErrorCode());
            throw new CMFault(errorCode);
        }
        return hrs_ret;
    }

    private HostResultSet getHostResultSet(HostResultSet original_res) throws HostException {
        int rowindex = 0;

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "accNo");
        rowindex++;
        md.addColumn(rowindex, "ChequeStatus");
        rowindex++;
        md.addColumn(rowindex, "Desc");
        hostResultSet.setMetaData(md);


        ArrayList row = new ArrayList();
        row.add(original_res.getString("AccountNo"));
        row.add(original_res.getString("ChequeStatus"));
        row.add(original_res.getString("Desc"));

        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ActionCode").substring(1, 5));

        return hostResultSet;
    }
}