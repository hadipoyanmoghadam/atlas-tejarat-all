package dpi.atlas.service.cm.handler.general.trans;

import dpi.atlas.service.cm.host.handlers.HostHandlerBase;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostResultSetMetaData;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.nab.HostInterfaceNAB;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import dpi.atlas.iso.MyISODate;
import dpi.atlas.client.messages.BaseMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

import java.util.Map;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.SQLException;

/**
 * FundTransferHandler class
 * with Amjadi
 *
 * @author <a href="mailto:amjadi.sahar@tgmail.com">Sahar Amjadi</a>
 * @version $Revision: 1.13 $ $Date: 2008/02/05 14:04:28 $
 */
public class FundTransfer extends HostHandlerBase {
    private static Log log = LogFactory.getLog(FundTransfer.class);

    public HostResultSet _post(Object o, Map map, HostInterface hostInterface) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside FundTransferHandler:_post()");
        CMMessage msg = (CMMessage) o;
        String service = msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
        if (service.equals(Constants.ISO_SERVICE)) {
            return sparrowFundTransfer(msg, map, hostInterface);
        } else {
            return eBankingFundTransfer(msg, map, hostInterface);
        }
    }

    public HostResultSet sparrowFundTransfer(CMMessage msg, Map map, HostInterface hostInterface) throws CMFault {
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
        String billId = "";
        String paymentId = "";

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");
        ref_no = Constants.SPW_FUND_TRANSFER + inputParams.get(Constants.SESSION_ID);
        inputParams.put(Fields.SETTLEMENT_DATE, command.getParam(Fields.SETTLEMENT_DATE));
        if ( commandName.equals(TJCommand.CMD_TRANSFER_FUND_REVERSAL) ||
             commandName.equals(TJCommand.CMD_CASH_WITHDRAWAL_REVERSAL) ||
             commandName.equals(TJCommand.CMD_BILL_PAYMENT_REVERSAL) ||
             commandName.equals(TJCommand.CMD_NETWORK_BILL_PAYMENT_REVERSE))
                    commandName = Constants.SPW_FUND_TRANSFER_REVERS;
        else {
            commandName = Constants.SPW_FUND_TRANSFER;

            inputParams.put(Constants.MERCHANT_RRN, command.getParam(Fields.MN_RRN));
            inputParams.put(Constants.MERCHANT_TERMINAL_ID, command.getParam(Fields.MERCHANT_TERMINAL_ID));
            inputParams.put(Constants.MERCHANT_TERMINAL_TYPE, command.getParam(Fields.TERMINAL_TYPE));
            inputParams.put(Constants.CLIENT_ID, command.getParam(Fields.TERMINAL_ID));
            inputParams.put(Constants.SPW_PAYMENT_ID, command.getParam(Fields.MN_ID));
            inputParams.put(Fields.DEST_BIN, command.getParam(Fields.DEST_BIN));
            inputParams.put(Fields.SRC_BIN, command.getParam(Fields.SRC_BIN));
            inputParams.put(Fields.PAN, command.getParam(Fields.PAN));
            inputParams.put(Fields.TOTAL_DEST_ACCOUNT, command.getParam(Fields.DEST_ACCOUNT));
            if (command.getParam(Fields.TOTAL_DEST_ACCOUNT) != null && !command.getParam(Fields.TOTAL_DEST_ACCOUNT).equalsIgnoreCase(""))
                inputParams.put(Fields.TOTAL_DEST_ACCOUNT, command.getParam(Fields.TOTAL_DEST_ACCOUNT));

        }
        String txCode = command.getParam(Fields.TX_CODE);
        if (txCode == null)
            txCode = msg.getAttributeAsString(Fields.COMMAND);

        inputParams.put(Fields.TX_CODE, txCode);
        if (!commandName.equals(Constants.SPW_FUND_TRANSFER_REVERS)) {
            src_acc_no = command.getParam(Fields.SRC_ACCOUNT);
            dest_acc_no = command.getParam(Fields.DEST_ACCOUNT);
            amount = command.getParam(Fields.AMOUNT);
            
            desc = command.getParam(Fields.DOCUMENT_DESCRIPTION);
            host_id = msg.getAttribute(Constants.SRC_HOST_ID) + (String) msg.getAttribute(Constants.DEST_HOST_ID);
            branch_code = (String) command.getParam(Fields.ATM_BRANCH_ID);
            if (branch_code == null || "".equals(branch_code.trim()))
                branch_code = "";
        }
        String date = command.getParam(Fields.DATE);
        String time = command.getParam(Fields.TIME);
        String batchPK = command.getParam(CFSConstants.CURRENT_BATCH);
        String opCode = msg.getAttributeAsString(Fields.OPERATION_CODE);

        //-- bill payment --
        if (msg.hasAttribute(Fields.BLPY_BILL_ID)) {
            billId = msg.getAttributeAsString(Fields.BLPY_BILL_ID);
            paymentId = msg.getAttributeAsString(Fields.BLPY_PAYMENT_ID);
        }
        inputParams.put(Constants.COMMAND_NAME, commandName);
        inputParams.put(Constants.REF_NO, ref_no);
        inputParams.put(Constants.SRC_ACCOUNT, src_acc_no);
        if (!"".equals(dest_acc_no) && dest_acc_no.length() > 13)
            dest_acc_no = dest_acc_no.trim().substring(0, 13);
        inputParams.put(Constants.DST_ACCOUNT, dest_acc_no);
        inputParams.put(Constants.AMOUNT, amount);
        if (date.trim().length() == 6)
            date = DateUtil.getSystemDate().substring(0, 2) + date;
        inputParams.put(Constants.DATE, date);
        inputParams.put(Constants.TIME, time);
        inputParams.put(Constants.BRANCH_CODE, branch_code);
        inputParams.put(Constants.SRC_HOST_ID, host_id);
        inputParams.put(Constants.BATCH_PK, batchPK);
        inputParams.put(Constants.BILL_ID, billId);
        inputParams.put(Constants.PAYMENT_ID, paymentId);
        String msgSeq = (String) command.getCommandParams().get(Fields.TRANSACTION_SEQUENCE_NUMBER);
        if (commandName.equals(Constants.SPW_FUND_TRANSFER_REVERS))
            msgSeq = "98" + msgSeq;
        inputParams.put(Constants.MSG_SEQ, msgSeq);
        inputParams.put(Constants.OP_CODE, opCode);
        inputParams.put(Constants.DOC_DESC, desc);
        String spwChannelCode = command.getParam(Fields.SPW_CHANNEL_CODE);
        if (spwChannelCode != null && !"".equals(spwChannelCode.trim()))
            inputParams.put(Constants.SERVICE, spwChannelCode);
        else
            inputParams.put(Constants.SERVICE, Constants.DEFAULT_CHANNEL_CODE);

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = ((HostInterfaceNAB) hostInterface).spwFundTransfer(inputParams);
            if (hrs.next()) {
                hrs_ret = getHostResultSet(hrs, host_id, branch_code);
                if (hrs_ret.getDataHeaderField(Fields.ACTION_CODE).equals(ActionCode.APPROVED) && command.getCommandName().equals("BLPYR"))
                    try {
                        ChannelFacadeNew.updateTbbillPaymentTxToReversed(billId, paymentId);
                    } catch (SQLException e) {
                        log.error(e.getMessage());
                        throw new CMFault(Integer.toString(HostException.UNKNOWN_ERROR));
                    }
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

    public HostResultSet eBankingFundTransfer(CMMessage msg, Map map, HostInterface hostInterface) throws CMFault {
        HashMap inputParams = (HashMap) msg.getAttribute(Constants.INPUT_PARAMS);
        String service = (String) inputParams.get(Constants.SERVICE);
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String fPayCode = command.getParam(Fields.FIRST_PAY_CODE) != null ? command.getParam(Fields.FIRST_PAY_CODE) : "";
        String sPayCode = command.getParam(Fields.SECOND_PAY_CODE) != null ? command.getParam(Fields.SECOND_PAY_CODE) : "";
        String cbPayId = command.getParam(Fields.CB_PAY_ID) != null ? command.getParam(Fields.CB_PAY_ID) : "";
        String commandName = command.getCommandName();
        String ref_no = "";
        String src_acc_no = "";
        String dest_acc_no = "";
        String amount = "";
        String desc = "";
        String host_id = "";
        String branch_code = "";
        String billId = "";
        String paymentId = "";

        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");

        Map header = command.getCommandHeader();
        ref_no = (String) header.get(Fields.REFRENCE_NUMBER);
        if (!commandName.equals(TJCommand.SHARE_STCK_FT_CMD)) {
            if (commandName.equals(TJCommand.CMD_TRANSFER_FUND_REVERSAL))
                commandName = TJCommand.CMD_TRANSFER_FUND_REVERSAL;
            else
                commandName = TJCommand.CMD_TRANSFER_FUND;
        }
        src_acc_no = command.getParam(Fields.SRC_ACCOUNT);
        dest_acc_no = command.getParam(Fields.DEST_ACCOUNT);
        amount = command.getParam(Fields.AMOUNT);
        desc = command.getParam(Fields.DOCUMENT_DESCRIPTION);
        host_id = msg.getAttribute(Constants.SRC_HOST_ID) + (String) msg.getAttribute(Constants.DEST_HOST_ID);
        branch_code = (String) msg.getAttribute(Fields.CHANNEL_BRANCH_NO);
        if (branch_code == null || "".equals(branch_code.trim()))
            branch_code = "";
        String date = command.getParam(Fields.DATE);
        String time = command.getParam(Fields.TIME);
        String batchPK = command.getParam(CFSConstants.CURRENT_BATCH);
        String opCode = msg.getAttributeAsString(Fields.OPERATION_CODE);
        String msgSeq;
        if (msg.hasAttribute(CMMessage.COMMAND_OBJ))
            msgSeq = ((BaseMessage) msg.getAttribute(CMMessage.COMMAND_OBJ)).getMsgSequence();
        else
            msgSeq = (String) msg.getAttribute(Fields.MESSAGE_SEQUENCE);

        //-- bill payment --
        if (msg.hasAttribute(Fields.BLPY_BILL_ID)) {
            billId = msg.getAttributeAsString(Fields.BLPY_BILL_ID);
            paymentId = msg.getAttributeAsString(Fields.BLPY_PAYMENT_ID);
            if (host_id.equals("13"))
                desc = command.getParam("documentDescription");
        }

        inputParams.put(Constants.COMMAND_NAME, commandName);
        inputParams.put(Constants.REF_NO, ref_no);
        inputParams.put(Constants.SRC_ACCOUNT, src_acc_no);
        if (!"".equals(dest_acc_no) && dest_acc_no.length() > 13)
            dest_acc_no = dest_acc_no.trim().substring(0, 13);
        inputParams.put(Constants.DST_ACCOUNT, dest_acc_no);
        inputParams.put(Constants.AMOUNT, amount);
        if (date.trim().length() == 6)
            date = DateUtil.getSystemDate().substring(0, 2) + date;
        inputParams.put(Constants.DATE, date);
        inputParams.put(Constants.TIME, time);
        inputParams.put(Constants.BRANCH_CODE, branch_code);
        inputParams.put(Constants.SRC_HOST_ID, host_id);
        inputParams.put(Constants.BATCH_PK, batchPK);
        inputParams.put(Constants.BILL_ID, billId);
        inputParams.put(Constants.PAYMENT_ID, paymentId);
        if (service.equals("FWS"))
            inputParams.put(Constants.DOC_SERIAL, msgSeq.substring(msgSeq.length() - 6, msgSeq.length()));
        inputParams.put(Constants.MSG_SEQ, msgSeq);
        inputParams.put(Constants.OP_CODE, opCode);
        inputParams.put(Constants.DOC_DESC, desc);
        inputParams.put(Fields.FIRST_PAY_CODE, fPayCode);
        inputParams.put(Fields.SECOND_PAY_CODE, sPayCode);
        inputParams.put(Fields.CB_PAY_ID, cbPayId);

        if (commandName.equals(TJCommand.SHARE_STCK_FT_CMD)) {
            inputParams.put(Constants.DOC_DESC, command.getParam(Fields.TRANS_DESC));
            inputParams.put(Constants.TIME, command.getHeaderParam(Fields.TIME));
            inputParams.put(Constants.OP_CODE, command.getParam(Fields.OPERATION_CODE));
            inputParams.put(Constants.COMMAND_NAME, TJCommand.CMD_TRANSFER_FUND);
        }

        HostResultSet hrs;
        HostResultSet hrs_ret = null;
        try {
            hrs = hostInterface.fundTransfer(inputParams);
            if (hrs.next()) {
                hrs_ret = getHostResultSet(hrs, host_id, branch_code);
                if (hrs_ret.getDataHeaderField(Fields.ACTION_CODE).equals(ActionCode.APPROVED) && command.getCommandName().equals("BLPYR"))
                    try {
                        ChannelFacadeNew.updateTbbillPaymentTxToReversed(billId, paymentId);
                    } catch (SQLException e) {
                        log.error(e.getMessage());
                        throw new CMFault(Integer.toString(HostException.UNKNOWN_ERROR));
                    }
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

    private HostResultSet getHostResultSet(HostResultSet original_res, String host_id, String branchCode) throws HostException {
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
        md.addColumn(rowindex, "accountBalance");
        rowindex++;
        md.addColumn(rowindex, "availableBalance");

        hostResultSet.setMetaData(md);
        ArrayList row = new ArrayList();
        original_res.getMetaData();

        row.add(host_id);
        row.add(current_date_persian);
        row.add(branchCode);
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
