package branch.dpi.atlas.service.cm.handler.SMS.convertor;

import branch.dpi.atlas.service.cm.imf.sms.SMSToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * User: F.Heydari
 * Date: Nov ,12, 2019
 * Time: 2:15 PM
 */


public class ConvertorWage extends SMSBaseConvertor implements SMSToIMFFormater {
    @Override
    public CMCommand format(SMSMessage smsMessage) {
        CMCommand command = super.format(smsMessage);

        command.addParam(Fields.REQUEST_ID, smsMessage.getMessageSequence());
        command.addParam(Fields.SRC_ACCOUNT, smsMessage.getAccountNo());
        command.addParam(Fields.DATE, smsMessage.getTransactionDate());
        command.addParam(Fields.TIME, smsMessage.getTransactionTime());
        command.addParam(Fields.PAN, Constants.BANKE_TEJARAT_BIN_NEW);
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        command.setCommandName(TJCommand.CMD_WAGE_SMS);
        command.addParam(Fields.TERMINAL_ID, smsMessage.getTerminalId());

        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            SMSMessage smsMessage = (SMSMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(smsMessage.createResponseHeader());


            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }

}














