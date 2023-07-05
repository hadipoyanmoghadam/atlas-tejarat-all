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
 * Date: April 19, 2020
 * Time: 06:32 PM
 */
public class FundTransferStockDeposit extends HostHandlerBase {
    private static Log log = LogFactory.getLog(FundTransferStockDeposit.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside FundTransferStockDeposit:_post()");
        CMMessage msg = (CMMessage) o;

        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String commandName = Constants.FUND_TRANSFER_CMD;;
        String ref_no = "";
        String fee_ref_no = "";
        String src_acc_no = "";
        String dest_acc_no = "";
        String fee_acc_no = "";
        String amount = "";
        String feeAmount = "";
        String desc = "";
        String extra_info = "";
        String fee_desc = "";
        String fee_extra_info = "";
        String host_id = "";
        String fee_host_id = "";
        String branch_code = "";

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        src_acc_no = command.getParam(Fields.SRC_ACCOUNT);
        dest_acc_no = command.getParam(Fields.DEST_ACCOUNT);
        fee_acc_no=command.getParam(Fields.FEE_ACCOUNT);
        amount = command.getParam(Fields.AMOUNT);
        feeAmount=command.getParam(Fields.FEE_AMOUNT);
        desc = command.getParam(Fields.DOCUMENT_DESCRIPTION);
        extra_info=command.getParam(Fields.EXTRA_INFO);
        fee_desc=command.getParam(Fields.FEE_DOCUMENT_DESCRIPTION);
        fee_extra_info=command.getParam(Fields.FEE_EXTRA_INFO);
        host_id = msg.getAttribute(Constants.SRC_HOST_ID) + (String) msg.getAttribute(Constants.DEST_HOST_ID);
        fee_host_id=Constants.HOST_ID_FARAGIR+Constants.HOST_ID_SGB;
        branch_code = (String) command.getParam(Fields.BRANCH_CODE);
        if (branch_code == null || "".equals(branch_code.trim()))
            branch_code = "";

        String date = command.getParam(Fields.DATE);
        String time = command.getParam(Fields.TIME);
        String batchPK = command.getParam(CFSConstants.CURRENT_BATCH);
        String messageSeq = command.getParam(Fields.MN_RRN);

        String docNo = command.getParam(Fields.BRANCH_DOC_NO);
        if (docNo == null || docNo.trim().equalsIgnoreCase(""))
            docNo = "";

        String opCode = command.getParam(Fields.SGB_TX_CODE);
        String feeOpCode = command.getParam(Fields.OPERATION_CODE_FEE_AMOUNT);

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
        inputParams.put(Constants.BRANCH_CODE, branch_code);
        inputParams.put(Constants.SRC_ACCOUNT, src_acc_no);
        inputParams.put(Constants.DST_ACCOUNT, dest_acc_no);
        inputParams.put(Constants.FEE_ACCOUNT, fee_acc_no);
        inputParams.put(Constants.AMOUNT, amount);
        inputParams.put(Constants.FEE_AMOUNT, feeAmount);
        ref_no = Constants.SPW_FUND_TRANSFER + inputParams.get(Constants.SESSION_ID);
        inputParams.put(Constants.REF_NO, ref_no);
        fee_ref_no = Constants.FEE_FUND_TRANSFER + inputParams.get(Constants.SESSION_ID);
        inputParams.put(Constants.FEE_REF_NO, fee_ref_no);
        inputParams.put(Constants.MSG_SEQ, messageSeq);
        inputParams.put(Constants.SRC_HOST_ID, host_id);
        inputParams.put(Constants.FEE_HOST_ID, fee_host_id);
        inputParams.put(Constants.BATCH_PK, batchPK);
        inputParams.put(Constants.OP_CODE, opCode);
        inputParams.put(Constants.OP_CODE, opCode);
        inputParams.put(Constants.FEE_OP_CODE, feeOpCode);
        inputParams.put(Constants.DOC_SERIAL, docNo);

        inputParams.put(Fields.TOTAL_DEST_ACCOUNT, command.getParam(Fields.DEST_ACCOUNT));
        if (command.getParam(Fields.TOTAL_DEST_ACCOUNT) != null && !command.getParam(Fields.TOTAL_DEST_ACCOUNT).equalsIgnoreCase(""))
            inputParams.put(Fields.TOTAL_DEST_ACCOUNT, command.getParam(Fields.TOTAL_DEST_ACCOUNT));

        inputParams.put(Constants.DOC_DESC, desc.trim());
        inputParams.put(Constants.FEE_DESC, fee_desc.trim());
        inputParams.put(Constants.EXTRA_INFO, extra_info.trim());
        inputParams.put(Constants.FEE_EXTRA_INFO, fee_extra_info.trim());

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = ((HostInterfaceNAB) hostInterface).depositStock(inputParams);
            if (hrs.next()) {
                hrs_ret = getHostResultSet(hrs, host_id);
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

    private HostResultSet getHostResultSet(HostResultSet original_res, String host_id) throws HostException {
        int rowindex = 0;

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();

        rowindex++;
        md.addColumn(rowindex, "hostId");
        rowindex++;
        md.addColumn(rowindex, "serverUpdateDate");


        hostResultSet.setMetaData(md);
        ArrayList row = new ArrayList();
        original_res.getMetaData();

        row.add(host_id);
        row.add(original_res.getString("DateTime"));

        hostResultSet.addRow(row);
        hostResultSet.setDataHeaderField(Fields.ACTION_CODE, original_res.getString("ACTIONCODE").substring(1, 5));


        return hostResultSet;
    }
}
