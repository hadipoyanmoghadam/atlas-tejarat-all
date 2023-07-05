package dpi.atlas.service.cm.handler.general.trans.branch;

import dpi.atlas.service.cfs.common.CFSConstants;
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
 * Time: 03:01 PM
 */
public class FundTransferStockDepositReverse extends HostHandlerBase {
    private static Log log = LogFactory.getLog(FundTransferStockDepositReverse.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside FundTransferStockDepositReverse:_post()");
        CMMessage msg = (CMMessage) o;

        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        String ref_no = "";
        String desc = "";
        String extra_info = "";

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);

        String commandName = Constants.FUND_TRANSFER_REVERS_CMD;

        desc = command.getParam(Fields.DOCUMENT_DESCRIPTION);
        extra_info = command.getParam(Fields.EXTRA_INFO);
        String date = command.getParam(Fields.DATE);
        String time = command.getParam(Fields.TIME);
        String batchPK = command.getParam(CFSConstants.CURRENT_BATCH);
        String messageSeq = command.getParam(Fields.MN_RRN);
        String docNo = command.getParam(Fields.BRANCH_DOC_NO);
        if (docNo == null && docNo.trim().equalsIgnoreCase(""))
            docNo = "";
        String opCode = command.getParam(Fields.SGB_TX_CODE);


        inputParams.put(Constants.COMMAND_NAME, commandName);

        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");

        inputParams.put(Constants.SERVICE, msg.getAttributeAsString(CMMessage.SERVICE_TYPE));

        if (date.trim().length() == 6)
            date = DateUtil.getSystemDate().substring(0, 2) + date;
        inputParams.put(Constants.DATE, date);
        inputParams.put(Constants.TIME, time);

        ref_no = Constants.SPW_FUND_TRANSFER + inputParams.get(Constants.SESSION_ID);
        inputParams.put(Constants.REF_NO, ref_no);
        inputParams.put(Constants.BATCH_PK, batchPK);
        inputParams.put(Constants.MSG_SEQ, messageSeq);
        inputParams.put(Constants.OP_CODE, opCode);
        inputParams.put(Fields.TX_CODE, command.getCommandName());
        inputParams.put(Fields.BRANCH_DOC_NO, docNo);

        inputParams.put(Constants.DOC_DESC, desc.trim());
        inputParams.put(Constants.EXTRA_INFO, extra_info.trim());

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = ((HostInterfaceNAB) hostInterface).brFundTransfer(inputParams);
            if (hrs.next()) {
                hrs_ret = getHostResultSet(hrs);
            }
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
        md.addColumn(rowindex, "serverUpdateDate");

        hostResultSet.setMetaData(md);
        ArrayList row = new ArrayList();
        original_res.getMetaData();

        row.add(original_res.getString("DateTime"));

        hostResultSet.addRow(row);
        hostResultSet.setDataHeaderField(Fields.ACTION_CODE, original_res.getString("ACTIONCODE").substring(1, 5));


        return hostResultSet;
    }
}
