package dpi.atlas.service.cfs.handler;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.model.tj.entity.TxType;

import java.sql.SQLException;
import java.util.Map;
import java.util.ArrayList;

import dpi.atlas.util.DateUtil;
import org.jpos.iso.ISOUtil;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.core.Configurable;

/**
 * Created by IntelliJ IDEA.
 * User: F.Moghri < Moghri@DPI.IR >
 * Date: Apr 26, 2011
 * Time: 10:05:23 AM
 * This Class Mekes SMS String for sending to MQ - for message received from Channel Sparrow
 * The SMS string have been made for Source Account or
 * dest Account that field SMSNotification = 1 in table TBCustomerSRV
 * Format of this String is according to this document: TSA_EXCHANGE_FORMAT891104.pdf
 */
public class SMSPrepareResponseSPW extends CFSHandlerBase implements Configurable {

    private ArrayList actionCodeList;
    private ArrayList commandList;
    long smsMinimumWithdrawAmount;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        try {
            if (log.isInfoEnabled()) log.info(description);
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            long newBalance ;
            String srcHasSMS ="0";
            String destHasSMS =Constants.FALSE;
            String sendStr ="";
            String sequenceNo = "1";  // Contain  currentTimeMillis
            String date = "";
            String time = "";
            String srcAccountNo = "";
            String destAccountNo = "";
            String amount = "";
            String debitCredit="" ;
            String actionCode = "";
            String channelType = "";
            String channelID = "";
            String bankCode = "";
            String SGBActionCode = "";
            String isReversed = Constants.FALSE;

           actionCode = msg.getAttributeAsString(CFSConstants.ACTION_CODE);

            // If Action Code is approved = 0000 , sms send made & send to MQ
           if(!actionCodeList.contains(actionCode) || commandList.contains(command.getCommandName()) ||
                   msg.getAttributeAsString(Fields.MESSAGE_TYPE).contains("FTGRP"))
               return;
           if(msg.hasAttribute(Constants.SRC_SMS_NOTIFICATION))
                srcHasSMS= msg.getAttribute(Constants.SRC_SMS_NOTIFICATION).toString();
           String srcHostId = (String) msg.getAttribute(Constants.SRC_HOST_ID);
           boolean isSrcHostCFS = srcHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);
           if(msg.hasAttribute(Constants.ISREVERSED))
                    isReversed = msg.getAttribute(Constants.ISREVERSED).toString();

           sequenceNo = String.valueOf(System.currentTimeMillis());
           String txCode = (String) msg.getAttribute(Fields.MESSAGE_TYPE);
           String spwChannelCode = GetSPWChannelCode(txCode);
           msg.setAttribute(Fields.SPW_CHANNEL_CODE, spwChannelCode.trim());
           if(msg.hasAttribute(Constants.DEST_SMS_NOTIFICATION))
                destHasSMS = msg.getAttribute(Constants.DEST_SMS_NOTIFICATION).toString();
            date = msg.getAttribute(Fields.DATE).toString();
            if(date!=null && date.trim().length()==6)
                date = DateUtil.getSystemDate().substring(0,2) + msg.getAttribute(Fields.DATE).toString();
           time =  msg.getAttribute(Fields.TIME).toString();
           amount = ISOUtil.zeroUnPad(msg.getAttribute(Fields.AMOUNT).toString());
           SGBActionCode =(String )  holder.get(Fields.SGB_TX_CODE);
           channelType = msg.getAttribute(Fields.SPW_CHANNEL_CODE).toString();
           channelID = command.getParam(Fields.TERMINAL_ID);
           bankCode = command.getParam(Fields.SRC_BIN);

           if (srcHasSMS!=null && srcHasSMS.equals("1") && isSrcHostCFS && Long.parseLong(amount) > smsMinimumWithdrawAmount ){
               srcAccountNo=command.getParam(Fields.SRC_ACCOUNT);
               newBalance =(Long) holder.get(Fields.SOURCE_ACCOUNT_BALANCE);
               debitCredit = Constants.WITHDRAW;                // BARDASHT
                 if(isReversed.equals(Constants.TRUE))
                    debitCredit = Constants.DEPOSIT ;             // Variz

                 sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                                           date + time.substring(0,4) + srcAccountNo.substring(2,13) +
                                           ISOUtil.zeropad(amount, 15) +
                                           ISOUtil.zeropad(Long.toString(newBalance), 15)+
                                           debitCredit+SGBActionCode+channelType+
                                           ISOUtil.zeropad(channelID , 15)+bankCode;
                  holder.put(CFSConstants.SMS_SRC_RESULT, sendStr);
           }
           String destHostId = "";
           if(msg.hasAttribute(Constants.DEST_HOST_ID))
                destHostId = (String) msg.getAttribute(Constants.DEST_HOST_ID);
           boolean isDestHostCFS = destHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);

           if (destHasSMS!=null && destHasSMS.equals(Constants.TRUE) && isDestHostCFS ){
               destAccountNo = msg.getAttributeAsString(Fields.DEST_ACCOUNT);
               newBalance =(Long) holder.get(Fields.DEST_ACCOUNT_BALANCE);
               debitCredit = Constants.DEPOSIT;             // VARIZ = 1
               if(isReversed.equals(Constants.TRUE))
                   debitCredit = Constants.WITHDRAW;
               sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                                    date + time.substring(0,4) + destAccountNo.substring(2,13) +
                                    ISOUtil.zeropad(amount, 15) +
                                    ISOUtil.zeropad(Long.toString(newBalance), 15)+
                                    debitCredit+SGBActionCode+channelType+
                                    ISOUtil.zeropad(channelID , 15)+bankCode;
                holder.put(CFSConstants.SMS_DEST_RESULT, sendStr);

        }

        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_SMS_FORMAT, new Exception(ActionCode.FORMAT_ERROR));
        }

    }

    private String GetSPWChannelCode(String txCode)    throws CFSFault
    {
        String spwChannelCode = "";

        if (log.isDebugEnabled()) log.debug("txCode=" + txCode);
        try {                                                                      

            TxType txType = CFSFacadeNew.getTxTypeSgbTxCode(txCode);
            spwChannelCode = txType.getSpwChannelCode().trim();

            if (log.isDebugEnabled()) log.debug("spwChannelCode =" + spwChannelCode);
            if ( "".equals(spwChannelCode.trim())) {
                log.error("SPW Tx Code is null for tx " + spwChannelCode);
                throw new NotFoundException("SPW Trasaction Code is null for Tx " + spwChannelCode);
            }

        } catch (SQLException e) {
            log.error("General data error in retrieving SPWTxType with code " + txCode + " from TBCFSTxType");
            throw new CFSFault(CFSFault.FLT_SMS_FORMAT, new Exception(ActionCode.FORMAT_ERROR));
        } catch (NotFoundException e) {
             throw new CFSFault(CFSFault.FLT_SMS_FORMAT, new Exception(ActionCode.FORMAT_ERROR));
        }
        return spwChannelCode;
    }


    public void setConfiguration(Configuration cfg) throws ConfigurationException {
       super.setConfiguration(cfg);
       String  actionCodes = cfg.get(Fields.ACTIONCODE_TO_BE_CONSIDERED);
       actionCodeList= CMUtil.tokenizString(actionCodes,",");    //for action code = 0000 , sms have sent
       smsMinimumWithdrawAmount =Long.parseLong(cfg.get(Fields.SMS_NOTIFICATION_MINIMUM_WITHDRAW_AMOUNT));
       String  commandString  = cfg.get(Fields.NOT_CHECKED);    //for some command like BAL , SMS have not sent
       commandList= CMUtil.tokenizString(commandString,",");

    }

}
