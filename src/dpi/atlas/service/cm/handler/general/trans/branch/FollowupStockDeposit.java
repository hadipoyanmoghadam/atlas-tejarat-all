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
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: April 20, 2020
 * Time: 10:21 AM
 */

public class FollowupStockDeposit extends HostHandlerBase {
    private static Log log = LogFactory.getLog(FollowupStockDeposit.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside FollowupStockDeposit:_post()");
        CMMessage msg = (CMMessage) o;
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");

        inputParams.put(Constants.SERVICE, msg.getAttributeAsString(CMMessage.SERVICE_TYPE));


        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String SrcHostIdStr = command.getParam(Constants.SRC_HOST_ID);
        String DestHostIdStr = command.getParam(Constants.DEST_HOST_ID);

        if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR)) {
            // Faragir is source account
            inputParams.put(Constants.ACC_NO, msg.getAttributeAsString(Fields.SRC_ACCOUNT).trim());

        } else if (DestHostIdStr.equals(Constants.HOST_ID_FARAGIR)) {
            // Faragir is destination account
            inputParams.put(Constants.ACC_NO, msg.getAttributeAsString(Fields.DEST_ACCOUNT).trim());
        }

        String origMessageData =msg.getAttributeAsString(Fields.ORIG_MESSAGE_DATA);
        inputParams.put(Constants.REF_NO,origMessageData.substring(0, 12));
        inputParams.put(Constants.DATE, origMessageData.substring(12, 20));
        inputParams.put(Constants.TIME, origMessageData.substring(20, 26));
        inputParams.put(Constants.BRANCH_CODE, msg.getAttributeAsString(Fields.BRANCH_CODE));

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = ((HostInterfaceNAB) hostInterface).getBranchFollowUp(inputParams);
            if (hrs.next())
                hrs_ret = getHostResultSet(hrs);
        } catch (HostException e) {
            String errorCode = Integer.toString(e.getErrorCode());
            if (e.errorCode != HostException.CONNECTION_TIMEOUT)
                throw new CMFault(errorCode);
            else {
                hrs_ret = new HostResultSet();
                hrs_ret.setDataHeaderField(Fields.ACTION_CODE, ActionCode.TIME_OUT);
            }
        }
        return hrs_ret;

    }

    private HostResultSet getHostResultSet(HostResultSet original_res) throws HostException {
        int rowindex = 0;

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "accNo");
        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();
        original_res.getMetaData();
//
        row.add(original_res.getString("AccountNo"));
        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ActionCode").substring(1, 5));

        return hostResultSet;
    }

}