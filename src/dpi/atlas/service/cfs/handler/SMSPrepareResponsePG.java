package dpi.atlas.service.cfs.handler;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 25, 2020
 * Time: 01:06 PM
 */
public class SMSPrepareResponsePG extends CFSHandlerBase implements Configurable {

    private ArrayList actionCodeList;
    long smsMinimumWithdrawAmount;
    private String serviceType;
    private ArrayList commandList;
    private ArrayList checkPin;
    private ArrayList smsForWageList;

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

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);


        try {

            actionCode = msg.getAttributeAsString(CFSConstants.ACTION_CODE);
            if (!actionCodeList.contains(actionCode) || !checkPin.contains(command.getCommandName()))
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
            msg.setAttribute(Fields.SERVICE_TYPE, serviceType);
            SGBActionCode = msg.getAttribute(Fields.SGB_TX_CODE).toString();
            channelType = Constants.PG_CHANNEL_CODE;
            channelID = "";
            bankCode = Constants.BANKE_TEJARAT_BIN_NEW;
            List<String> smsStringList = new ArrayList<String>();
            // For Withdraw and  Deposit Reverse

            if (srcHasSMS != null && srcHasSMS.equals(Constants.TRUE) && isSrcHostCFS && Long.parseLong(amount) > smsMinimumWithdrawAmount) {

                sequenceNo = String.valueOf(System.currentTimeMillis());
                date = msg.getAttribute(Fields.DATE).toString();
                if (date.length() == 6)
                    date = DateUtil.getSystemDate().substring(0,2) + date;
                time = msg.getAttribute(Fields.TIME).toString();
                accountNo = msg.getAttribute(Fields.SRC_ACCOUNT).toString();
                try {
                    accountNo = ISOUtil.zeropad(accountNo, 13);
                } catch (ISOException e) {
                    log.error("Can not zeropad account number = '" + accountNo + "' in SMSPrepareResponsePG : " + e.getMessage());
                }

                newBalance = (Long) holder.get(Fields.SOURCE_ACCOUNT_BALANCE);

                debitCredit = Constants.WITHDRAW;  //bardash
                if (isReversed.equals(Constants.TRUE))
                    debitCredit = Constants.DEPOSIT;             // Variz

                sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                        date + time.substring(0, 4) + accountNo.substring(2, 13) +
                        ISOUtil.zeropad(amount, 15) +
                        ISOUtil.zeropad(String.valueOf(newBalance), 15) +
                        debitCredit + SGBActionCode + channelType +
                        ISOUtil.zeropad(channelID, 15) + bankCode;
                smsStringList.add(sendStr);
            }
            // For Deposit  and Withdraw Reverse
            if (destHasSMS != null && destHasSMS.equals(Constants.TRUE) && isDestHostCFS) {

                sequenceNo = String.valueOf(System.currentTimeMillis());
                if (date.length() == 6)
                    date = DateUtil.getSystemDate().substring(0,2) + date;
                time = msg.getAttribute(Fields.TIME).toString();
                accountNo = msg.getAttribute(Fields.DEST_ACCOUNT).toString();
                try {
                    accountNo = ISOUtil.zeropad(accountNo, 13);
                } catch (ISOException e) {
                    log.error("Can not zeropad account number = '" + accountNo + "' in SMSPrepareResponsePG : " + e.getMessage());
                }
                newBalance = (Long) holder.get(Fields.DEST_ACCOUNT_BALANCE);
                debitCredit = Constants.DEPOSIT;
                if (isReversed.equals(Constants.TRUE))
                    debitCredit = Constants.WITHDRAW;

                sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                        date + time.substring(0, 4) + accountNo.substring(2, 13) +
                        ISOUtil.zeropad(amount, 15) +
                        ISOUtil.zeropad(String.valueOf(newBalance), 15) +
                        debitCredit + SGBActionCode + channelType +
                        ISOUtil.zeropad(channelID, 15) + bankCode;
                smsStringList.add(sendStr);


                if (smsForWageList.contains(command.getCommandName()) && isReversed.equalsIgnoreCase(CFSConstants.FALSE)) {

                    sequenceNo = String.valueOf(System.currentTimeMillis());
                    date = msg.getAttribute(Fields.DATE).toString();
                    if(date!=null && date.trim().length()==6)
                        date = DateUtil.getSystemDate().substring(0,2) + msg.getAttribute(Fields.DATE).toString();
                    time = msg.getAttribute(Fields.TIME).toString();
                    accountNo = msg.getAttribute(Fields.DEST_ACCOUNT).toString();

                    newBalance = (Long) holder.get(Fields.DEST_ACCOUNT_BALANCE_WAGE);
                    amount = msg.getAttributeAsString(Fields.FEE_AMOUNT);
                    SGBActionCode = msg.getAttributeAsString(Fields.OPERATION_CODE_FEE_AMOUNT);

                    debitCredit = Constants.WITHDRAW;

                    sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                            date + time.substring(0, 4) + accountNo.substring(2, 13) +
                            ISOUtil.zeropad(amount, 15) +
                            ISOUtil.zeropad(String.valueOf(newBalance), 15) +
                            debitCredit + SGBActionCode + channelType +
                            ISOUtil.zeropad(channelID, 15) + bankCode;
                    smsStringList.add(sendStr);
                }
            }
            if (smsStringList.size() > 0)
                holder.put(CFSConstants.SMS_RESULT, smsStringList);


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
        serviceType = cfg.get(Fields.SERVICE_TYPE);
        String commandString = cfg.get(Fields.NOT_CHECKED);    //for some command like BAL , SMS have not sent
        commandList = CMUtil.tokenizString(commandString, ",");
        String checkString = cfg.get(Fields.CHECKED);    //for some command have sent
        checkPin = CMUtil.tokenizString(checkString, ",");
        String smsForWage = cfg.get(Fields.SMS_FOR_WAGE);    //for some command like DEPSTPG, sms have send for wage
        smsForWageList = CMUtil.tokenizString(smsForWage, ",");


    }
}

