package branch.dpi.atlas.service.cm.handler.credits.convertor;

import branch.dpi.atlas.service.cm.imf.credits.CreditsToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Sep 4, 2017
 * Time: 03:38 PM
 */
public class ConvertorCreditsFollowup extends CreditsBaseConvertor implements CreditsToIMFFormater {
    @Override
    public CMCommand format(CreditsMessage creditsMessage) {
        CMCommand command = super.format(creditsMessage);

        command.setCommandName(TJCommand.CMD_CREDITS_FOLLOWUP);
        command.addParam(Fields.ORIG_MESSAGE_DATA, creditsMessage.getOrigMessageData());
        command.addParam(Fields.TERMINAL_ID, creditsMessage.getTerminalId());
        command.addParam(Fields.SRC_ACCOUNT, creditsMessage.getAccountNo());
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            CreditsMessage creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(creditsMessage.getOrigMessageData()).
                    append(msg.getAttribute(Fields.FOLLOWUP_ACTION_CODE));
            responseStr.insert(0, creditsMessage.createResponseHeader());
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}