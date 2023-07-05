package dpi.atlas.service.cm.handler.general.trans.branch;

import dpi.atlas.iso.MyISODate;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: July 27, 2019
 * Time: 03:01 PM
 */
public class FundTransferSafeBox extends HostHandlerBase {
    private static Log log = LogFactory.getLog(FundTransferSafeBox.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside FundTransferSafeBox:_post()");
        CMMessage msg = (CMMessage) o;

        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String commandName = command.getCommandName();
        String ref_no = "";
        String src_acc_no = "";
        String dest_acc_no = "";
        String amount = "";
        String desc = "";
        String host_id = "";
        String branch_code = "";

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID == null || "".equals(sessionID.trim()))
            sessionID = "000000000000";

        if (commandName.equals(TJCommand.CMD_FUND_TRANSFER_REVERSE_SAFE_BOX) ||
                commandName.equals(TJCommand.CMD_FUND_TRANSFER_REVERSE_MARHOONAT_INSURANCE))
            commandName = Constants.FUND_TRANSFER_REVERS_CMD;
        else {
            commandName = Constants.FUND_TRANSFER_CMD;
        }

        if (!commandName.equals(Constants.SPW_FUND_TRANSFER_REVERS)) {
            src_acc_no = command.getParam(Fields.SRC_ACCOUNT);
            dest_acc_no = command.getParam(Fields.DEST_ACCOUNT);
            amount = command.getParam(Fields.AMOUNT);

            desc = command.getParam(Fields.DOCUMENT_DESCRIPTION);
            host_id = msg.getAttribute(Constants.SRC_HOST_ID) + (String) msg.getAttribute(Constants.DEST_HOST_ID);
            branch_code = (String) command.getParam(Fields.BRANCH_CODE);
            if (branch_code == null || "".equals(branch_code.trim()))
                branch_code = "";
        }
        String date = command.getParam(Fields.DATE);
        String time = command.getParam(Fields.TIME);
        String batchPK = command.getParam(CFSConstants.CURRENT_BATCH);
        String messageSeq=command.getParam(Fields.MN_RRN);
        String docNo = command.getParam(Fields.BRANCH_DOC_NO);
        if (docNo == null && docNo.trim().equalsIgnoreCase(""))
            docNo = "";
        String opCode = command.getParam(Fields.SGB_TX_CODE);

        inputParams.put(Constants.COMMAND_NAME, commandName);
        inputParams.put(Constants.DATE, date);
        inputParams.put(Constants.TIME, time);
        inputParams.put(Constants.BRANCH_CODE, branch_code);
        inputParams.put(Constants.SRC_ACCOUNT, src_acc_no);
        if (!"".equals(dest_acc_no) && dest_acc_no.length() > 13)
            dest_acc_no = dest_acc_no.trim().substring(0, 13);
        inputParams.put(Constants.DST_ACCOUNT, dest_acc_no);
        inputParams.put(Constants.AMOUNT, amount);
        ref_no = Constants.SPW_FUND_TRANSFER + inputParams.get(Constants.SESSION_ID);
        inputParams.put(Constants.REF_NO, ref_no);
        inputParams.put(Constants.SRC_HOST_ID, host_id);
        inputParams.put(Constants.BATCH_PK, batchPK);
        inputParams.put(Constants.MSG_SEQ, messageSeq);
        inputParams.put(Constants.OP_CODE, opCode);
        inputParams.put(Fields.BRANCH_DOC_NO, docNo);
        inputParams.put(Constants.MERCHANT_RRN, command.getParam(Fields.MN_RRN));
        inputParams.put(Constants.MERCHANT_TERMINAL_ID, command.getParam(Fields.TERMINAL_ID));
        inputParams.put(Fields.TX_CODE, command.getCommandName());

        inputParams.put(Fields.TOTAL_DEST_ACCOUNT, command.getParam(Fields.DEST_ACCOUNT));
        if (command.getParam(Fields.TOTAL_DEST_ACCOUNT) != null && !command.getParam(Fields.TOTAL_DEST_ACCOUNT).equalsIgnoreCase(""))
            inputParams.put(Fields.TOTAL_DEST_ACCOUNT, command.getParam(Fields.TOTAL_DEST_ACCOUNT));

        if (command.getCommandName().equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_SAFE_BOX) ||
                command.getCommandName().equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_REVERSE_SAFE_BOX))
            inputParams.put(Constants.MERCHANT_TERMINAL_TYPE, Constants.TERMINAL_TYPE_SAFE_BOX);
        else if (command.getCommandName().equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_MARHOONAT_INSURANCE) ||
                command.getCommandName().equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_REVERSE_MARHOONAT_INSURANCE))
            inputParams.put(Constants.MERCHANT_TERMINAL_TYPE, Constants.TERMINAL_TYPE_MARHOONAT_INSURANCE);
        else
            inputParams.put(Constants.MERCHANT_TERMINAL_TYPE, Constants.BRANCH_CHANNEL_CODE);

        inputParams.put(Constants.DOC_DESC, desc.trim());
        inputParams.put(Constants.EXTRA_INFO, desc.trim());

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = ((HostInterfaceNAB) hostInterface).brFundTransfer(inputParams);
            if (hrs.next()) {
                hrs_ret = getHostResultSet(hrs, host_id, branch_code, docNo);
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

    private HostResultSet getHostResultSet(HostResultSet original_res, String host_id, String branchCode, String docNo) throws HostException {
        int rowindex = 0;
        String current_date_persian = getCurrentDate(Calendar.getInstance());
        current_date_persian = MyISODate.getPersianDateYYYYMMDD2(current_date_persian);

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();

        rowindex++;
        md.addColumn(rowindex, "hostId");
        rowindex++;
        md.addColumn(rowindex, "serverUpdateDate");
        rowindex++;
        md.addColumn(rowindex, "branchCode");
        rowindex++;
        md.addColumn(rowindex, "DocNo");
        rowindex++;
        md.addColumn(rowindex, "accountBalance");
        rowindex++;
        md.addColumn(rowindex, "availableBalance");

        hostResultSet.setMetaData(md);
        ArrayList row = new ArrayList();
        original_res.getMetaData();

        row.add(host_id);
        row.add(current_date_persian);
        row.add(branchCode);
        row.add(docNo);
        row.add(original_res.getString("balance"));
        row.add(original_res.getString("availableBalance"));

        hostResultSet.addRow(row);
        hostResultSet.setDataHeaderField(Fields.ACTION_CODE, original_res.getString("ACTIONCODE").substring(1, 5));


        return hostResultSet;
    }

    public String getCurrentDate(Calendar calendar) {
        String year = calendar.get(Calendar.YEAR) + "";
        String month = calendar.get(Calendar.MONTH) + 1 + "";
        String day = calendar.get(Calendar.DAY_OF_MONTH) + "";
        try {
            month = ISOUtil.zeropad(month, 2);
            day = ISOUtil.zeropad(day, 2);
        } catch (ISOException e) {
            e.toString();
        }
        return year + month + day;
    }

}
