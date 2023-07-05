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
 * User: SH.Behnaz
 * Date: October 3, 2017
 * Time: 9:24:03 AM
 */

public class FundTransferReverse extends HostHandlerBase {
    private static Log log = LogFactory.getLog(FundTransferReverse.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside FundTransfer:_post()");
        CMMessage msg = (CMMessage) o;
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        CreditsMessage creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");


        String origMessageData = creditsMessage.getOrigMessageData();
        inputParams.put(Constants.REF_NO, origMessageData.substring(0, 12));
        inputParams.put(Constants.DATE, origMessageData.substring(12, 20));
        inputParams.put(Constants.TIME, origMessageData.substring(20, 26));
        inputParams.put(Constants.MSG_SEQ, creditsMessage.getMessageSequence());
        inputParams.put(Constants.SERVICE, msg.getAttributeAsString(CMMessage.SERVICE_TYPE));
        inputParams.put(Constants.ACC_NO, creditsMessage.getAccountNo());
//      ???  inputParams.put(Constants.REF_NO, branchMsg.getMessageSequence());
        inputParams.put(Constants.AMOUNT, creditsMessage.getAmount());
//        inputParams.put(Constants.DATE, date);
//        inputParams.put(Constants.TIME, branchMsg.getTransTime());
        inputParams.put(Constants.BRANCH_CODE, creditsMessage.getBranchCode());
        inputParams.put(Fields.BRANCH_DOC_NO, creditsMessage.getDocumentNo());
        inputParams.put(Constants.OP_CODE, creditsMessage.getOperationCode());
        inputParams.put(Constants.DOC_DESC, creditsMessage.getDocumentDescription());
        inputParams.put(Constants.EFECTIVE_DATE, DateUtil.getSystemDate());
        inputParams.put(Constants.CREDIT_DEBIT, command.getParam(Constants.CREDIT_DEBIT));
//        inputParams.put(Fields.REQUEST_TYPE, branchMsg.getRequestType());


        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = ((HostInterfaceNAB) hostInterface).getBranchDepositReverse(inputParams);
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
        rowindex++;
        md.addColumn(rowindex, "serverUpdateDate");
        rowindex++;
        md.addColumn(rowindex, "CRDB");
        rowindex++;
        md.addColumn(rowindex, "accountBalance");
        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();
        original_res.getMetaData();

        row.add(original_res.getString("AccountNo"));
        row.add(original_res.getString("DateTime"));
        row.add(original_res.getString("CRDB"));
        row.add(original_res.getString("balance"));

        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ActionCode").substring(1, 5));

        return hostResultSet;
    }

}