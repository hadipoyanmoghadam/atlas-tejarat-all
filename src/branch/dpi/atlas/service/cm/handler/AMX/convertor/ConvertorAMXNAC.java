package branch.dpi.atlas.service.cm.handler.AMX.convertor;

import branch.dpi.atlas.service.cm.imf.amx.AmxToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;

import java.util.Map;


/**
 * User:R.Nasiri
 * Date: April 4, 2015
 * Time: 4:37 PM
 */
public class ConvertorAMXNAC extends AmxBaseConvertor implements AmxToIMFFormater {

    public CMCommand format(AmxMessage amxMessage) {
        CMCommand command = super.format(amxMessage);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(amxMessage.createResponseHeader());
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}



