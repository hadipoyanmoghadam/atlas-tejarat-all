package dpi.atlas.service.cm.handler.general.trans.branch;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
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
 * Date: October 19, 2021
 * Time: 12:01 PM
 */

public class GetBalanceSafeBx extends HostHandlerBase {
    private static Log log = LogFactory.getLog(GetBalanceSafeBx.class);
    public static final String CREDIT_VALUE = "0";

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside GetBalanceSafeBox:_post()");
        CMMessage msg = (CMMessage) o;
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");

        inputParams.put(Constants.SERVICE, msg.getAttributeAsString(CMMessage.SERVICE_TYPE));
        inputParams.put(Constants.ACC_NO, amxMessage.getSrcAccount());

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {

            hrs = ((HostInterfaceNAB) hostInterface).getBranchBalance(inputParams);
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
        md.addColumn(rowindex, "AvailableBalance");
        rowindex++;
        md.addColumn(rowindex, "branchCode");
        rowindex++;
        md.addColumn(rowindex, "accountGroup");
        rowindex++;
        md.addColumn(rowindex, "CRDB");
        rowindex++;
        md.addColumn(rowindex, "ledgerBalance");
        rowindex++;
        md.addColumn(rowindex, Constants.AVAILBLE_BAL_SIGN);
        rowindex++;
        md.addColumn(rowindex, "accountStatus");
        rowindex++;
        md.addColumn(rowindex, "BlockAmount");
        hostResultSet.setMetaData(md);


        ArrayList row = new ArrayList();
        row.add(original_res.getString("AccountNo"));
        row.add(original_res.getString("AvailableBalance"));
        row.add(original_res.getString("BranchNo").substring(1));
        row.add(original_res.getString("AccountGroup").trim());
        if (original_res.getString("CRDB").equals(CREDIT_VALUE))
            row.add(Constants.CREDIT);
        else
            row.add(Constants.DEBIT);
        String ledgerBal = original_res.getString("LedgerBalance");
        if (Float.parseFloat(ledgerBal) < 0)
            ledgerBal = ledgerBal.substring(1, ledgerBal.length());
        row.add(ledgerBal);
        row.add(original_res.getString(Constants.AVAILBLE_BAL_SIGN));
        String accountStatus = original_res.getString("AccountStatus");
        if (accountStatus.equals(Constants.CM_BLOCK))
            accountStatus = Constants.FARAGIR_BLOCK;
        else if (accountStatus.equals(Constants.CM_UNBLOCK))
            accountStatus = Constants.FARAGIR_UNBLOCK;
        row.add(accountStatus);
        row.add(original_res.getString("BlockAmount"));

        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ActionCode").substring(1, 5));

        return hostResultSet;
    }

}