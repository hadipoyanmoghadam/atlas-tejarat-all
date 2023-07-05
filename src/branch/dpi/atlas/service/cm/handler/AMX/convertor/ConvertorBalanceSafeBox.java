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
import org.jpos.iso.ISOUtil;

import java.util.Map;


/**
 * User:R.Nasiri
 * Date: Oct 10, 2021
 * Time: 11:46 AM
 */
public class ConvertorBalanceSafeBox extends AmxBaseConvertor implements AmxToIMFFormater {

    public CMCommand format(AmxMessage amxMessage) {
        CMCommand command = super.format(amxMessage);
        command.setCommandName(TJCommand.CMD_MARHOONAT_INSURANCE_BALANCE);
        command.addParam(Fields.SRC_ACCOUNT, amxMessage.getSrcAccount());
        command.addParam(Fields.REQUEST_IS_ACCOUNT_BASED,"1");
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(amxMessage.createResponseHeader())
                    .append(amxMessage.getAvailableBalance())
                    .append(amxMessage.getActualBalance());
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}



