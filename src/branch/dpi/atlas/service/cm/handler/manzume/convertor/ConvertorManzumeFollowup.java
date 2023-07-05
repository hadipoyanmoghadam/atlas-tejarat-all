package branch.dpi.atlas.service.cm.handler.manzume.convertor;

import branch.dpi.atlas.service.cm.imf.manzume.ManzumeToIMFFormater;
import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;

import java.util.Map;

/**
 * User: F.Heydari
 * Date: NOV 3, 2022
 * Time: 03:38 PM
 */
public class ConvertorManzumeFollowup extends ManzumeBaseConvertor implements ManzumeToIMFFormater {
    @Override
    public CMCommand format(ManzumeMessage manzumeMessage) {
        CMCommand command = super.format(manzumeMessage);

        command.setCommandName(TJCommand.CMD_MANZUME_FOLLOWUP);
        command.addParam(Fields.ORIG_MESSAGE_DATA, manzumeMessage.getOrigMessageData());
        command.addParam(Fields.TERMINAL_ID, manzumeMessage.getTerminalId());
        command.addParam(Fields.DEST_ACCOUNT, manzumeMessage.getAccountNo());
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            ManzumeMessage manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(manzumeMessage.createResponseHeader()).
            //responseStr.append(manzumeMessage.getOrigMessageData()).

                    append(msg.getAttribute(Fields.FOLLOWUP_ACTION_CODE));
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
