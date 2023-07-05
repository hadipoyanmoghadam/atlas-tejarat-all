package dpi.atlas.service.cfs.handler;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Jun 16, 2020
 * Time: 12:54 PM
 */
public class SMSPrepareResponseGroupCard extends CFSHandlerBase implements Configurable {

    private ArrayList actionCodeList;
    long smsMinimumWithdrawAmount;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {
            String actionCode = msg.getAttributeAsString(CFSConstants.ACTION_CODE);
            if (!actionCodeList.contains(actionCode))
                return;

            String smsRegister = msg.getAttributeAsString(Fields.SMS_REGISTER);
            boolean parentHasSMS = smsRegister.charAt(0) == '1';
            boolean childHasSMS = smsRegister.charAt(1) == '1';
            boolean txHasSMS = smsRegister.charAt(2) == '1';
            boolean chargeHasSMS = smsRegister.charAt(3) == '1';
            if (!childHasSMS || !chargeHasSMS)
                return;


            String sendStr = "";
            String sequenceNo = "1";
            String date = "";        // yyyymmdd
            String time = "";        // hhmm
            String amount = "";
            long newCardBalance;
            String debitCredit = "";
            String SGBActionCode = "000";
            String channelType = "";
            String channelID = "";
            String bankCode = "";
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            String commandName = command.getCommandName();
            amount = ISOUtil.zeroUnPad(msg.getAttribute(Fields.AMOUNT).toString());
            bankCode = Constants.BANKE_TEJARAT_BIN_NEW;
            String childPan = msg.getAttributeAsString(Fields.PAN);
            String childMobileNo = CFSFacadeNew.getCustomerCellphone(childPan);
            if (childMobileNo != null && !childMobileNo.trim().equals(""))
                childMobileNo = childMobileNo.substring(1);
            else
                childMobileNo = "0000000000";
            newCardBalance = (Long) holder.get(Fields.CARD_BALANCE);
            sequenceNo = String.valueOf(System.currentTimeMillis());
            date = DateUtil.getSystemDate();
            time = DateUtil.getSystemTime();
            String serviceType = msg.getAttributeAsString(Fields.SERVICE_TYPE);
            if (serviceType.equals(Fields.SERVICE_PG))
                channelType = Constants.PG_CHANNEL_CODE;
            else if (serviceType.equals(Fields.SERVICE_CMS))
                channelType = Constants.BRANCH_CHANNEL_CODE;
            else
                channelType = Constants.OTHER_CHANNEL_CODE;

            channelID = ISOUtil.padleft("", 12, '0');

            if (commandName != null && commandName.trim().equalsIgnoreCase(TJCommand.CMD_CMS_CROUPCARD_DCHARGE) && Long.parseLong(amount) > smsMinimumWithdrawAmount) {

                debitCredit = Constants.GROUP_CARD_DE_CHARGE;
            }
            if (commandName != null && commandName.trim().equalsIgnoreCase(TJCommand.CMD_CMS_CROUPCARD_IMMEDIATE_CHARGE) && Long.parseLong(amount) > smsMinimumWithdrawAmount) {

                debitCredit = Constants.GROUP_CARD_CHARGE;
            }
            sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                    date + time.substring(0, 4) + childPan.substring(6, 16) + "C" +
                    ISOUtil.zeropad(amount, 15) +
                    ISOUtil.zeropad(String.valueOf(newCardBalance), 15) +
                    debitCredit + SGBActionCode + channelType +
                    ISOUtil.zeropad(channelID, 15) + bankCode + ISOUtil.zeropad("", 7) + childMobileNo;
            holder.put(CFSConstants.SMS_SRC_RESULT, sendStr);
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
    }
}

