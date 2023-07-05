package branch.dpi.atlas.service.cm.handler.SMS.convertor;

import branch.dpi.atlas.service.cm.imf.sms.SMSToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * User: F.Heydari
 * Date: Nov ,12, 2019
 * Time: 2:11 PM
 */


public class ConvertorFollowUpSMS extends SMSBaseConvertor implements SMSToIMFFormater {
    @Override

    public CMCommand format(SMSMessage smsMessage) {

        CMCommand command = super.format(smsMessage);
        command.addParam(Fields.ORIG_MESSAGE_DATA, smsMessage.getOrigMessageData());
        command.addParam(Fields.SRC_ACCOUNT, smsMessage.getAccountNo());
        command.addParam(Fields.TERMINAL_ID, smsMessage.getTerminalId());
        command.setCommandName(TJCommand.CMD_FOLLOW_UP_SMS);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            SMSMessage smsMessage = (SMSMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(smsMessage.createResponseHeader())
                    .append(msg.getAttribute(Fields.FOLLOWUP_ACTION_CODE));


            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));

        }
    }

}








