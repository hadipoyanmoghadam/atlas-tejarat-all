package dpi.atlas.service.cfs.handler;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Map;

import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.model.tj.entity.Tx;

/**
 * User: H.Ghayoumi
 * Date: Jul 11, 2013
 * Time: 3:25:27 PM
 */
public class SMSPrepareResponseNASIM extends CFSHandlerBase implements Configurable {

    private ArrayList actionCodeList;
    long smsMinimumWithdrawAmount;
    private String sericeType;
    private ArrayList commandList;
    private ArrayList checkPin;
    private ArrayList operationCode;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String actionCode = "";
        String sendStr = "";
        String sequenceNo = "1";
        String date = "";        // yyyymmdd
        String time = "";        // hhmm
        String accountNo = "";
        String amount = "";
        long newBalance;
        String debitCredit = "";
        String SGBActionCode = "";
        String channelType = "";
        String channelID = "";
        String bankCode = "";
        String srcHasSMS = "0";
        String destHasSMS = "0";
        String srcHostId = "";
        String destHostId = "";
        String descriptionCode = "000";
//        String isReversed = Constants.FALSE;
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);


        try {
            if (log.isInfoEnabled()) log.info(description);
            actionCode = msg.getAttributeAsString(CFSConstants.ACTION_CODE);
            if (!actionCodeList.contains(actionCode) || commandList.contains(command.getCommandName()))
                return;
            String isReversed = (msg.hasAttribute(Constants.ISREVERSED)) ? msg.getAttributeAsString(Constants.ISREVERSED) : Constants.FALSE;
            Tx reversal_tx = null;
            if (isReversed.equals(Constants.TRUE)) {
                reversal_tx = (Tx) holder.get("tx");
                amount = String.valueOf(reversal_tx.getAmount());
            } else
                amount = ISOUtil.zeroUnPad(msg.getAttribute(Fields.AMOUNT).toString());

            if (msg.hasAttribute(Constants.SRC_SMS_NOTIFICATION))
                srcHasSMS = msg.getAttribute(Constants.SRC_SMS_NOTIFICATION).toString();
            if (msg.hasAttribute(Constants.DEST_SMS_NOTIFICATION))
                destHasSMS = msg.getAttribute(Constants.DEST_SMS_NOTIFICATION).toString();
            if (msg.hasAttribute(Constants.DEST_HOST_ID))
                destHostId = msg.getAttribute(Constants.DEST_HOST_ID).toString();
            if (msg.hasAttribute(Constants.SRC_HOST_ID))
                srcHostId = msg.getAttribute(Constants.SRC_HOST_ID).toString();
            boolean isSrcHostCFS = srcHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);
            boolean isDestHostCFS = destHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);
            msg.setAttribute(Fields.SERVICE_TYPE, sericeType);
            SGBActionCode = msg.getAttribute(Fields.SGB_TX_CODE).toString();
            if (command.getCommandName().equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_SAFE_BOX) ||
                    command.getCommandName().equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_REVERSE_SAFE_BOX))
                channelType = Constants.TERMINAL_TYPE_SAFE_BOX;
            else if (command.getCommandName().equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_MARHOONAT_INSURANCE) ||
                    command.getCommandName().equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_REVERSE_MARHOONAT_INSURANCE))
                channelType = Constants.TERMINAL_TYPE_MARHOONAT_INSURANCE;
            else
                channelType = Constants.BRANCH_CHANNEL_CODE;
            channelID = msg.getAttribute(Fields.BRANCH_CODE).toString();
            bankCode = Constants.BANKE_TEJARAT_BIN_NEW;
            // For Branch Withdraw and  Deposit Reverse

            if (srcHasSMS != null && srcHasSMS.equals(Constants.TRUE) && isSrcHostCFS && Long.parseLong(amount) > smsMinimumWithdrawAmount) {

                sequenceNo = String.valueOf(System.currentTimeMillis());
                date = msg.getAttribute(Fields.DATE).toString();
                if(date!=null && date.trim().length()==6)
                    date = DateUtil.getSystemDate().substring(0,2) + msg.getAttribute(Fields.DATE).toString();
                time = msg.getAttribute(Fields.TIME).toString();
                if (!isReversed.equals("1"))
                    accountNo = msg.getAttribute(Fields.SRC_ACCOUNT).toString();
                else
                    accountNo = reversal_tx.getDestAccountNo();

                newBalance = (Long) holder.get(Fields.SOURCE_ACCOUNT_BALANCE);

                debitCredit = Constants.WITHDRAW;

                sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                        date + time.substring(0, 4) + accountNo.substring(2, 13) +
                        ISOUtil.zeropad(amount, 15) +
                        ISOUtil.zeropad(String.valueOf(newBalance), 15) +
                        debitCredit + SGBActionCode + channelType +
                        ISOUtil.zeropad(channelID, 15) + bankCode;
                holder.put(CFSConstants.SMS_SRC_RESULT, sendStr);
            }
            // For Branch Deposit  and Withdraw Reverse
            if (destHasSMS != null && destHasSMS.equals(Constants.TRUE) && isDestHostCFS) {

                sequenceNo = String.valueOf(System.currentTimeMillis());
                date = msg.getAttribute(Fields.DATE).toString();
                if(date!=null && date.trim().length()==6)
                    date = DateUtil.getSystemDate().substring(0,2) + msg.getAttribute(Fields.DATE).toString();
                time = msg.getAttribute(Fields.TIME).toString();
                if (!isReversed.equals("1"))
                    accountNo = msg.getAttribute(Fields.DEST_ACCOUNT).toString();
                else
                    accountNo = reversal_tx.getSrcAccountNo();
                newBalance = (Long) holder.get(Fields.DEST_ACCOUNT_BALANCE);
                debitCredit = Constants.DEPOSIT;

                if(checkPin.contains(command.getCommandName()) && operationCode.contains(SGBActionCode)){
                    String desc = (String) msg.getAttribute(Fields.DOCUMENT_DESCRIPTION);
                    if (desc != null && desc.length()== BranchMessage.DOCUMENT_DESCRIPTION && !desc.trim().equalsIgnoreCase("")  ){
                        desc=desc.trim();
                        descriptionCode = desc.substring(desc.length()-3);
                    }
                }
                channelID=descriptionCode+ISOUtil.zeropad(channelID, 12);

                sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                        date + time.substring(0, 4) + accountNo.substring(2, 13) +
                        ISOUtil.zeropad(amount, 15) +
                        ISOUtil.zeropad(String.valueOf(newBalance), 15) +
                        debitCredit + SGBActionCode + channelType +
                        channelID + bankCode;
                holder.put(CFSConstants.SMS_SRC_RESULT, sendStr);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CFSFault(CFSFault.FLT_SMS_FORMAT, new Exception(ActionCode.FORMAT_ERROR));
        }


    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        String actionCodes = cfg.get(Fields.ACTIONCODE_TO_BE_CONSIDERED);
        actionCodeList = CMUtil.tokenizString(actionCodes, ",");    //for action code = 0000 , sms have sent
        smsMinimumWithdrawAmount = Long.parseLong(cfg.get(Fields.SMS_NOTIFICATION_MINIMUM_WITHDRAW_AMOUNT));
        sericeType = cfg.get(Fields.SERVICE_TYPE);
        String  commandString  = cfg.get(Fields.NOT_CHECKED);    //for some command like BAL , SMS have not sent
        commandList= CMUtil.tokenizString(commandString,",");
        String  checkString  = cfg.get(Fields.CHECKED);    //for some command like Deposit , description code have sent
        checkPin= CMUtil.tokenizString(checkString,",");
        String  operationCodeString  = cfg.get(Fields.OPERATION_CODE);    //for some operation Code , description code have sent
        operationCode= CMUtil.tokenizString(operationCodeString,",");


    }
}

