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
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Jul 28, 2021
 * Time: 10:02 PM
 */
public class SMSPrepareResponseSIMIN extends CFSHandlerBase implements Configurable {

    private ArrayList actionCodeList;
    long smsMinimumWithdrawAmount;
    private String serviceType;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String actionCode = "";
        String sendStr = "";
        String sequenceNo = "1";
        String date = "";        // yyyymmdd
        String time = "";        // hhmm
        String accountNo = "";
        String amount = "";
        long newBalance;
        String SGBActionCode = "";
        String channelType = "";
        String channelID = "";
        String bankCode = "";
        String srcHasSMS = Constants.FALSE;
        String havingDocument = Constants.FALSE;


        try {
            actionCode = msg.getAttributeAsString(CFSConstants.ACTION_CODE);
            if (!actionCodeList.contains(actionCode))
                return;

            if (msg.hasAttribute(Fields.WAGE_AMOUNT))
                amount = ISOUtil.zeroUnPad(msg.getAttributeAsString(Fields.WAGE_AMOUNT));

            if (msg.hasAttribute(Constants.SRC_SMS_NOTIFICATION))
                srcHasSMS = msg.getAttribute(Constants.SRC_SMS_NOTIFICATION).toString();

            if (msg.hasAttribute(Fields.HAVING_DOCUMENT))
                havingDocument = msg.getAttribute(Fields.HAVING_DOCUMENT).toString();

            msg.setAttribute(Fields.SERVICE_TYPE, serviceType);
            if (msg.hasAttribute(Fields.SGB_TX_CODE))
                SGBActionCode = msg.getAttribute(Fields.SGB_TX_CODE).toString();
            channelType = Constants.SIMIN_CHANNEL_CODE;
            bankCode = Constants.BANKE_TEJARAT_BIN_NEW;

            if (srcHasSMS != null && srcHasSMS.equals(Constants.TRUE) && havingDocument != null && havingDocument.equals(Constants.TRUE)) {

                sequenceNo = String.valueOf(System.currentTimeMillis());
                date = DateUtil.getSystemDate();
                time = DateUtil.getSystemTime();
                accountNo = msg.getAttribute(Fields.SRC_ACCOUNT).toString();

                try {
                    accountNo = ISOUtil.zeropad(accountNo, 13);
                } catch (ISOException e) {
                    log.error("Can not zeropad account number = '" + accountNo + "' in SMSPrepareResponseSIMIN : " + e.getMessage());
                }

                newBalance = (Long) holder.get(Fields.SOURCE_ACCOUNT_BALANCE);

                sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                        date + time.substring(0, 4) + accountNo.substring(2, 13) +
                        ISOUtil.zeropad(amount, 15) +
                        ISOUtil.zeropad(String.valueOf(newBalance), 15) +
                        Constants.WITHDRAW + SGBActionCode + channelType +
                        ISOUtil.zeropad(channelID, 15) + bankCode;
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
        serviceType = cfg.get(Fields.SERVICE_TYPE);
    }
}

