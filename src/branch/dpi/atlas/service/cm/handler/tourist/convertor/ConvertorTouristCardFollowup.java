package branch.dpi.atlas.service.cm.handler.tourist.convertor;

import branch.dpi.atlas.service.cm.imf.tourist.TouristToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 05:01 PM
 */
public class ConvertorTouristCardFollowup extends TouristBaseConvertor implements TouristToIMFFormater {
    @Override
    public CMCommand format(TouristMessage touristMessage) {
        CMCommand command = super.format(touristMessage);

        command.setCommandName(TJCommand.CMD_TOURIST_FOLLOWUP);
        command.addParam(Fields.ORIG_MESSAGE_DATA, touristMessage.getOrigMessageData());
        command.addParam(Fields.SRC_ACCOUNT, touristMessage.getAccountNo());
        command.addParam(Fields.PAN,touristMessage.getCardNo());
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            TouristMessage touristMessage = (TouristMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(touristMessage.getOrigMessageData()).
                    append(ISOUtil.padleft(msg.getAttributeAsString(Fields.FOLLOWUP_ACTION_CODE), 4, ' '));

            if (msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equals("1"))
                responseStr.append(ISOUtil.padleft(touristMessage.getAccountNo(),touristMessage.ACCOUNT_NO, '0'));
            else
                responseStr.append(ISOUtil.padleft("", touristMessage.ACCOUNT_NO, '0'));

            responseStr.insert(0, touristMessage.createResponseHeader());
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
