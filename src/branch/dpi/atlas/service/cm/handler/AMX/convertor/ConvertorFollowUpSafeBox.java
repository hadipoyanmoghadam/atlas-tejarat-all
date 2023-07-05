package branch.dpi.atlas.service.cm.handler.AMX.convertor;

import branch.dpi.atlas.service.cm.imf.amx.AmxToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.util.Map;


/**
 * User:R.Nasiri
 * Date: July 10, 2019
 * Time: 1:59 PM
 */
public class ConvertorFollowUpSafeBox extends AmxBaseConvertor implements AmxToIMFFormater {

    public CMCommand format(AmxMessage amxMessage) {
        CMCommand command = super.format(amxMessage);
        if (amxMessage.getTerminalId().equalsIgnoreCase(Constants.TERMINAL_ID_SAFE_BOX))
            command.setCommandName(TJCommand.CMD_FOLLOW_UP_SAFE_BOX);
        else
            command.setCommandName(TJCommand.CMD_FOLLOW_UP_MARHOONAT_INSURANCE);
        command.addParam(Fields.SRC_ACCOUNT, amxMessage.getSrcAccount());
        command.addParam(Fields.DEST_ACCOUNT, amxMessage.getDestAccount());
        command.addParam(Fields.TERMINAL_ID, amxMessage.getTerminalId());
        command.addParam(Fields.ORIG_MESSAGE_DATA, amxMessage.getOrigMessageData());
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(amxMessage.createResponseHeader());
            responseStr.append(amxMessage.getOrigMessageData()).
                    append(msg.getAttribute(Fields.FOLLOWUP_ACTION_CODE));
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}



