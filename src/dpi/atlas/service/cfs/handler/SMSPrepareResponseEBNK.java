package dpi.atlas.service.cfs.handler;

import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.facade.CFSFacadeNew;

/**
 * Created by IntelliJ IDEA.
 * User: F.Moghri < Moghri@DPI.IR >
 * Date: Jun 12, 2011
 * Time: 12:18:04 PM
 * This Class Made SMS String for sending to MQ - for message received from Channel EBANKING
 * The SMS string have been made for Source Account or
 * dest Account that field SMSNotification = 1 in table TBCustomerSRV
 * Format of this String is according to this document TSA_EXCHANGE_FORMAT891104.pdf

 */
public class SMSPrepareResponseEBNK  extends CFSHandlerBase implements Configurable {

    private ArrayList actionCodeList;
    private ArrayList commandList;
    long smsMinimumWithdrawAmount;
    private static HashMap<String, String> channelCodeMap = new HashMap <String, String>();
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        try {
            if (log.isInfoEnabled()) log.info(description);
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            long newBalance ;
            String srcHasSMS =Constants.FALSE;
            String destHasSMS =Constants.FALSE;
            String sendStr ="";
            String sequenceNo = "1"; // Contain  currentTimeMillis
            String date = "";
            String time = "";
            String srcAccountNo = "";
            String destAccountNo = "";
            String amount = "";
            String debitCredit="" ;
            String actionCode = ""; //opCode
            String channelType ="";
            String channelID = "0";
            String bankCode = Constants.BANKE_TEJARAT_BIN_NEW;
            String serviceType = "";
            String opCode = "";
            String isReversed = Constants.FALSE;

           actionCode = msg.getAttributeAsString(CFSConstants.ACTION_CODE);

           if(!actionCodeList.contains(actionCode) || commandList.contains(command.getCommandName()))
               return;
           sequenceNo = String.valueOf(System.currentTimeMillis());
           serviceType = command.getHeaderParam(Fields.SERVICE_TYPE);
           if(msg.hasAttribute(Constants.SRC_SMS_NOTIFICATION))
                srcHasSMS= msg.getAttribute(Constants.SRC_SMS_NOTIFICATION).toString();
           if(msg.hasAttribute(Constants.DEST_SMS_NOTIFICATION)) 
                destHasSMS = msg.getAttribute(Constants.DEST_SMS_NOTIFICATION).toString();
           date =  msg.getAttribute(Fields.DATE).toString();
           time =  command.getHeaderParam(Fields.TIME);
           amount = ISOUtil.zeroUnPad(msg.getAttribute(Fields.AMOUNT).toString());
           opCode = msg.getAttributeAsString(Fields.OPERATION_CODE);
           channelType = setChannelCodeMap(serviceType);
           if(msg.hasAttribute(Constants.ISREVERSED))
                isReversed = msg.getAttribute(Constants.ISREVERSED).toString();


           String srcHostId = (String) msg.getAttribute(Constants.SRC_HOST_ID);
           boolean isSrcHostCFS = srcHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);

           if (srcHasSMS!=null && srcHasSMS.equals("1") && isSrcHostCFS && Long.parseLong(amount) > smsMinimumWithdrawAmount ){
                    srcAccountNo=msg.getAttributeAsString(Fields.SRC_ACCOUNT).trim();
           if(srcAccountNo.length() < 13)
                srcAccountNo = "000" + srcAccountNo;

               newBalance =(Long) holder.get(Fields.SOURCE_ACCOUNT_BALANCE);

           debitCredit = Constants.WITHDRAW;                // BARDASHT
           if(isReversed.equals(Constants.TRUE))
                debitCredit = Constants.DEPOSIT ;             // Variz

           sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                                       date + time.substring(0,4) + srcAccountNo.substring(2,13) +
                                       ISOUtil.zeropad(amount, 15) +
                                       ISOUtil.zeropad(Long.toString(newBalance), 15)+
                                       debitCredit+opCode+channelType+
                                       ISOUtil.zeropad(channelID , 15)+bankCode;
              
               holder.put(CFSConstants.SMS_SRC_RESULT, sendStr);
  
           }


           String destHostId = "";
           if(msg.hasAttribute(Constants.DEST_HOST_ID))
             destHostId = (String) msg.getAttribute(Constants.DEST_HOST_ID);
           boolean isDestHostCFS = destHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);


           if (destHasSMS!=null && destHasSMS.equals("1") && isDestHostCFS ){
               destAccountNo = msg.getAttributeAsString(Fields.DEST_ACCOUNT);
               newBalance =(Long) holder.get(Fields.DEST_ACCOUNT_BALANCE);

               debitCredit = Constants.DEPOSIT;   // VARIZ
               if(isReversed.equals(Constants.TRUE))
                    debitCredit = Constants.WITHDRAW;

               sendStr = ISOUtil.zeropad(sequenceNo, 32) +
                                    date + time.substring(0,4) + destAccountNo.substring(2,13) +
                                    ISOUtil.zeropad(amount, 15) +
                                    ISOUtil.zeropad(Long.toString(newBalance), 15)+
                                    debitCredit+opCode+channelType+
                                    ISOUtil.zeropad(channelID , 15)+bankCode;
               //System.out.println("SENDSTRING(DEST) >>"+sendStr);
               //System.out.println("SENDSTRING(DESTLenght) >>"+sendStr.length());
               holder.put(CFSConstants.SMS_DEST_RESULT, sendStr);


        }

        } catch (Exception e) {
            throw new CFSFault(CFSFault.FLT_SMS_FORMAT, new Exception(ActionCode.FORMAT_ERROR));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
       super.setConfiguration(cfg);
       String  actionCodes = cfg.get(Fields.ACTIONCODE_TO_BE_CONSIDERED); //for action code = 0000 , sms have sent
       actionCodeList= CMUtil.tokenizString(actionCodes,",");
       smsMinimumWithdrawAmount =Long.parseLong(cfg.get(Fields.SMS_NOTIFICATION_MINIMUM_WITHDRAW_AMOUNT));

      String  commandString  = cfg.get(Fields.NOT_CHECKED);  //for some command like BAL , SMS have not sent
       commandList= CMUtil.tokenizString(commandString,",");



    }


    public String setChannelCodeMap(String ServiceType) {

        if(!channelCodeMap.containsKey(ServiceType))
        {
            channelCodeMap.put("IVR", "02");
            channelCodeMap.put("SMS", "03");
            channelCodeMap.put("WAP", "04");
            channelCodeMap.put("IB", "05");
            channelCodeMap.put("DEAFAULT_FAX", "06");
            channelCodeMap.put("DEFAULT_EMAIL", "07");
            channelCodeMap.put("RTGS", "08");
            channelCodeMap.put("ISO", "09");
            channelCodeMap.put("CCS", "24");
            channelCodeMap.put("FWS", "95");
            channelCodeMap.put("OTWS", "94");
            channelCodeMap.put("ACH", "96");
            channelCodeMap.put("SST", "97");
            channelCodeMap.put("ISO", "98");
            channelCodeMap.put("CM", "99");
            channelCodeMap.put("BRM", "86");
            channelCodeMap.put("CCC", "87");
            channelCodeMap.put("CB", "88");
            channelCodeMap.put("SAD", "89");
            channelCodeMap.put("POC", "18");
            channelCodeMap.put("CDP", "20");
        }

        return   channelCodeMap.get(ServiceType).toString() ;
    }

}
