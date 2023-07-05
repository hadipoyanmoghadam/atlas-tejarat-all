package branch.dpi.atlas.service.cm.handler.AMX.convertor;

import branch.dpi.atlas.service.cm.imf.amx.AmxToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Nov 09, 2013
 * Time: 11:33:07 AM
 */
public class ConvertorChangeAccountStatusAMX extends AmxBaseConvertor implements AmxToIMFFormater {
    public CMCommand format(AmxMessage amxMessage) {

        CMCommand command = super.format(amxMessage);
        command.addParam(Fields.ACCOUNT_STATUS, amxMessage.getRequestType());
        command.setCommandName(TJCommand.CMD_AMX_CHANGE_ACCOUNT_STATUS);
        command.addParam(Fields.SRC_ACCOUNT, amxMessage.getAccountNo());
        command.addParam(Fields.NATIONAL_CODE, amxMessage.getNationalCode());
        command.addParam(Fields.EXTERNAL_ID_NUMBER, amxMessage.getExt_IdNumber());
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