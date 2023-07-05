package dpi.atlas.service.cfs.handler;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
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
 * Created by IntelliJ IDEA.
 * User: Habib  Ghayoumi
 * Date: 1/7/06
 * Time: 9:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class SMSPrepareResponseBRANCH extends CFSHandlerBase implements Configurable {

    private ArrayList actionCodeList;
    long smsMinimumWithdrawAmount;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String actionCode = "";
        String sendStr = "";
        String sequenceNo = "1";
        String date = "";        // yyyymmdd
        String time = "";        // hhmm
        String accountNo = "";
        String amount = "";
        long newBalance ;
        String debitCredit ="";
        String SGBActionCode = "";
        String channelType = "";
        String channelID = "";
        String bankCode = "";
        String srcHasSMS ="0";
        String destHasSMS ="0";
        String srcHostId = "";
        String destHostId = "";


        try {
           if (log.isInfoEnabled()) log.info(description);
           actionCode = msg.getAttributeAsString(CFSConstants.ACTION_CODE);

            // If Action Code is approved (0000) , sms is sent to MQ
           if(!actionCodeList.contains(actionCode))
               return;
            if(msg.hasAttribute(Constants.SRC_SMS_NOTIFICATION))
                srcHasSMS = msg.getAttribute(Constants.SRC_SMS_NOTIFICATION).toString();
            if(msg.hasAttribute(Constants.DEST_SMS_NOTIFICATION))
                destHasSMS = msg.getAttribute(Constants.DEST_SMS_NOTIFICATION).toString();
            if(msg.hasAttribute(Constants.DEST_HOST_ID))
                destHostId = msg.getAttribute(Constants.DEST_HOST_ID).toString();
            if(msg.hasAttribute(Constants.SRC_HOST_ID))
                srcHostId = msg.getAttribute(Constants.SRC_HOST_ID).toString();
            boolean isSrcHostCFS = srcHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);
            boolean isDestHostCFS = destHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);
            // For Branch Withdraw
            amount = ISOUtil.zeroUnPad(msg.getAttribute(Fields.AMOUNT).toString());
            if(srcHasSMS != null && srcHasSMS.equals(Constants.TRUE) && isSrcHostCFS && Long.parseLong(amount) > smsMinimumWithdrawAmount){
                msg.setAttribute(Fields.SERVICE_TYPE, "BRCH");
                sequenceNo = String.valueOf(System.currentTimeMillis());
                date = msg.getAttribute(Fields.DATE).toString();
                if(date!=null && date.trim().length()==6)
                    date = DateUtil.getSystemDate().substring(0,2) + msg.getAttribute(Fields.DATE).toString();
                time =  msg.getAttribute(Fields.TIME).toString();
                accountNo = msg.getAttribute(Fields.SRC_ACCOUNT).toString();
                accountNo = ISOUtil.zeropad(accountNo, 13);

                newBalance =(Long) holder.get(Fields.SOURCE_ACCOUNT_BALANCE);
                debitCredit = Constants.WITHDRAW;
                SGBActionCode = msg.getAttribute(Fields.SGB_TX_CODE).toString();
                channelType = Constants.BRANCH_CHANNEL_CODE;
                channelID = msg.getAttribute(Fields.BRANCH_CODE).toString();
                bankCode = Constants.BANKE_TEJARAT_BIN_NEW;

                sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                                               date + time.substring(0,4) + accountNo.substring(2,13) +
                                               ISOUtil.zeropad(amount , 15) +
                                               ISOUtil.zeropad(String.valueOf(newBalance), 15)+
                                               debitCredit+SGBActionCode+channelType+
                                               ISOUtil.zeropad(channelID , 15)+bankCode;
                holder.put(CFSConstants.SMS_SRC_RESULT, sendStr);
            }
            // For Branch Deposit
            if(destHasSMS != null && destHasSMS.equals(Constants.TRUE) && isDestHostCFS){
                msg.setAttribute(Fields.SERVICE_TYPE, "BRCH");
                sequenceNo = String.valueOf(System.currentTimeMillis());
                date = msg.getAttribute(Fields.DATE).toString();
                if(date!=null && date.trim().length()==6)
                    date = DateUtil.getSystemDate().substring(0,2) + msg.getAttribute(Fields.DATE).toString();
                time =  msg.getAttribute(Fields.TIME).toString();
                String commandName = msg.getAttribute(Fields.MESSAGE_TYPE).toString();
                accountNo = msg.getAttribute(Fields.DEST_ACCOUNT).toString();
                accountNo = ISOUtil.zeropad(accountNo, 13);
                amount = ISOUtil.zeroUnPad(msg.getAttribute(Fields.AMOUNT).toString());
                newBalance =(Long) holder.get(Fields.DEST_ACCOUNT_BALANCE);           
                debitCredit = Constants.DEPOSIT;
                if(commandName.equals("DEPBRNCHR"))
                    debitCredit = Constants.WITHDRAW;
                SGBActionCode = msg.getAttribute(Fields.SGB_TX_CODE).toString();
                channelType = Constants.BRANCH_CHANNEL_CODE;
                channelID = msg.getAttribute(Fields.BRANCH_CODE).toString();
                bankCode = Constants.BANKE_TEJARAT_BIN_NEW;

                sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                                               date + time.substring(0,4) + accountNo.substring(2,13) +
                                               ISOUtil.zeropad(amount , 15) +
                                               ISOUtil.zeropad(String.valueOf(newBalance), 15)+
                                               debitCredit+SGBActionCode+channelType+
                                               ISOUtil.zeropad(channelID , 15)+bankCode;
                holder.put(CFSConstants.SMS_SRC_RESULT, sendStr);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new CFSFault(CFSFault.FLT_SMS_FORMAT, new Exception(ActionCode.FORMAT_ERROR));
        }


    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
       super.setConfiguration(cfg);
       String  actionCodes = cfg.get(Fields.ACTIONCODE_TO_BE_CONSIDERED);
       actionCodeList= CMUtil.tokenizString(actionCodes, ",");    //for action code = 0000 , sms have sent
       smsMinimumWithdrawAmount =Long.parseLong(cfg.get(Fields.SMS_NOTIFICATION_MINIMUM_WITHDRAW_AMOUNT));


    }
}
