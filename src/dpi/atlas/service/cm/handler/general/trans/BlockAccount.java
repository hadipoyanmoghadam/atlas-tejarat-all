package dpi.atlas.service.cm.handler.general.trans;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.iso.MyISODate;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
import dpi.atlas.service.cm.ib.Params;
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
 * User: SH.Behnaz
 * Date: June 30, 2015
 * Time: 10:04:03 AM
 */

public class BlockAccount extends HostHandlerBase {
    private static Log log = LogFactory.getLog(BlockAccount.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside BlockAccount:_post()");
        CMMessage msg = (CMMessage) o;
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String accountStatus = branchMsg.getAccountStatus();
        if (accountStatus.equals(Constants.CM_BLOCK))
            accountStatus = Constants.FARAGIR_BLOCK;
        else if (accountStatus.equals(Constants.CM_UNBLOCK))
            accountStatus = Constants.FARAGIR_UNBLOCK;

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");

        if (accountStatus.equals(Constants.BLOCK_ACCOUNT_STATUS) || accountStatus.equals(Constants.UNBLOCK_ACCOUNT_STATUS)) {
            inputParams.put(Constants.SERVICE,msg.getAttributeAsString(CMMessage.SERVICE_TYPE));
            inputParams.put(Constants.ACC_NO, branchMsg.getAccountNo());
            inputParams.put(Constants.BRANCH_CODE, branchMsg.getBranchCode());
            inputParams.put(Constants.ENEX_BLOCK_AMOUNT, branchMsg.getAmount() != null ? branchMsg.getAmount() : "");
            inputParams.put(Constants.BLOCK_NO, branchMsg.getBlockRow().substring(3));
            inputParams.put(Constants.DATE, branchMsg.getTransDate());
            inputParams.put(Constants.TIME, branchMsg.getTransTime());
            inputParams.put(Constants.BLOCK_DESC, branchMsg.getDocumentDescription());
            inputParams.put(Fields.USER_ID, branchMsg.getUserId());
            inputParams.put(Constants.BLOCK_STATUS, accountStatus);
            inputParams.put(Constants.MERCHANT_TERMINAL_ID,branchMsg.getTerminalId()!=null ? branchMsg.getTerminalId() : "");
     //  inputParams.put(Constants.MERCHANT_TERMINAL_ID, "");

            inputParams.put(Constants.DST_ACCOUNT, "");
            inputParams.put(Constants.WAGE_AMOUNT, "0");
            inputParams.put(Constants.REF_NO, "");
            inputParams.put(Constants.SRC_HOST_ID, "");
            inputParams.put(Constants.BATCH_PK, "");
            inputParams.put(Constants.MSG_SEQ,"");
            inputParams.put(Constants.OP_CODE, "");
            inputParams.put(Fields.BRANCH_DOC_NO, "");
            inputParams.put(Constants.MERCHANT_RRN,"");
            inputParams.put(Fields.TX_CODE, "");
            inputParams.put(Fields.TOTAL_DEST_ACCOUNT, "");
            inputParams.put(Constants.MERCHANT_TERMINAL_TYPE, "");
            inputParams.put(Constants.DOC_DESC, "");
            inputParams.put(Constants.EXTRA_INFO, "");
            inputParams.put(Constants.BROKER, Constants.BROKER_ID_BLOCK_FOR_FARAGIR);
            inputParams.put(Constants.PROVIDER, Constants.PROVIDER_ID_BLOCK);
            inputParams.put(Constants.MESSAGE_TYPE, "N");  //N=without Document Y=with Document
        }
        else{
            String ref_no = "";
            String dest_acc_no = "";
            String wageamount = "";
            String wageDescription = "";
            String host_id = "";

            dest_acc_no = msg.getAttributeAsString(Fields.DEST_ACCOUNT);
            wageamount = msg.getAttributeAsString(Fields.WAGE_AMOUNT);

            wageDescription = msg.getAttributeAsString(Fields.WAGE_DESCRIPTION);
            host_id= Constants.HOST_ID_FARAGIR+ Constants.HOST_ID_SGB;
        String documentStatus=msg.getAttributeAsString(Fields.WAGE_WITH_DOCUMENT);

            String batchPK = command.getParam(CFSConstants.CURRENT_BATCH);
            String messageSeq=command.getParam(Fields.MN_RRN);
            String docNo = messageSeq.substring(6,12);
            String opCode = msg.getAttributeAsString(Fields.SGB_TX_CODE);


            inputParams.put(Constants.SERVICE, msg.getAttributeAsString(CMMessage.SERVICE_TYPE));
            inputParams.put(Constants.ACC_NO, branchMsg.getAccountNo());
            inputParams.put(Constants.BRANCH_CODE, branchMsg.getBranchCode());
            inputParams.put(Constants.ENEX_BLOCK_AMOUNT, branchMsg.getAmount() != null ? branchMsg.getAmount() : "");
            inputParams.put(Constants.BLOCK_NO, branchMsg.getBlockRow().substring(3));
            inputParams.put(Constants.DATE, branchMsg.getTransDate());
            inputParams.put(Constants.TIME, branchMsg.getTransTime());
            inputParams.put(Constants.BLOCK_DESC, branchMsg.getDocumentDescription());
            inputParams.put(Fields.USER_ID, branchMsg.getUserId());
            inputParams.put(Constants.BLOCK_STATUS, accountStatus);
            inputParams.put(Constants.MERCHANT_TERMINAL_ID,branchMsg.getTerminalId()!=null ? branchMsg.getTerminalId() : "");
//        inputParams.put(Constants.MERCHANT_TERMINAL_ID, "");
            inputParams.put(Constants.BROKER, Constants.BROKER_ID_SIMIN_FOR_FARAGIR);
            inputParams.put(Constants.PROVIDER, Constants.PROVIDER_ID_SIMIN);
            if (!"".equals(dest_acc_no) && dest_acc_no.length() > 13)
                dest_acc_no = dest_acc_no.trim().substring(0, 13);
            inputParams.put(Constants.DST_ACCOUNT, dest_acc_no);
            inputParams.put(Constants.WAGE_AMOUNT, wageamount);
            ref_no = Constants.SPW_FUND_TRANSFER + inputParams.get(Constants.SESSION_ID);
            inputParams.put(Constants.REF_NO, ref_no);
            inputParams.put(Constants.SRC_HOST_ID, host_id);
            inputParams.put(Constants.BATCH_PK, batchPK);
            inputParams.put(Constants.MSG_SEQ, messageSeq);
            inputParams.put(Constants.OP_CODE, opCode);
            inputParams.put(Fields.BRANCH_DOC_NO, docNo);
            inputParams.put(Constants.MERCHANT_RRN, command.getParam(Fields.MN_RRN));
            inputParams.put(Fields.TX_CODE, command.getCommandName());
            inputParams.put(Fields.TOTAL_DEST_ACCOUNT, dest_acc_no);
            inputParams.put(Constants.MERCHANT_TERMINAL_TYPE, Constants.SIMIN_CHANNEL_CODE);

        inputParams.put(Constants.DOC_DESC, wageDescription.trim());
        inputParams.put(Constants.EXTRA_INFO, wageDescription.trim());
        if (documentStatus.equalsIgnoreCase(Constants.TRUE))
            inputParams.put(Constants.MESSAGE_TYPE, "Y");  //N=without Document Y=with Document
        else
            inputParams.put(Constants.MESSAGE_TYPE, "N");  //N=without Document Y=with Document
        }

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {

            if (accountStatus.equalsIgnoreCase("0") || accountStatus.equalsIgnoreCase("1"))
                //block or unblock account
                hrs = ((HostInterfaceNAB) hostInterface).Account_Block(inputParams);
            else if (accountStatus.equalsIgnoreCase("6")|| accountStatus.equalsIgnoreCase("8"))
                //block amount
                hrs = ((HostInterfaceNAB) hostInterface).Account_AmountBlock(inputParams);
            else if (accountStatus.equalsIgnoreCase("5")|| accountStatus.equalsIgnoreCase("7"))
                //unblock amount
                hrs = ((HostInterfaceNAB) hostInterface).Account_AmountUNBlock(inputParams);
            else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
            }
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

        String current_date_persian = getCurrentDate(Calendar.getInstance());
        current_date_persian = MyISODate.getPersianDateYYYYMMDD2(current_date_persian);

        HostResultSet hostResultSet = new HostResultSet();
        HostResultSetMetaData md = new HostResultSetMetaData();
        rowindex++;
        md.addColumn(rowindex, "accNo");
        rowindex++;
        md.addColumn(rowindex, "serverUpdateDate");
        hostResultSet.setMetaData(md);

        ArrayList row = new ArrayList();
        row.add(original_res.getString("AccountNo"));
        row.add(current_date_persian);
        hostResultSet.addRow(row);

        hostResultSet.setDataHeaderField("action_code", original_res.getString("ActionCode").substring(1, 5));

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
