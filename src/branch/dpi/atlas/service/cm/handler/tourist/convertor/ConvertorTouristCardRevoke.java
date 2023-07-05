package branch.dpi.atlas.service.cm.handler.tourist.convertor;

import branch.dpi.atlas.service.cm.imf.tourist.TouristToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 04:58 PM
 */
public class ConvertorTouristCardRevoke extends TouristBaseConvertor implements TouristToIMFFormater {
    @Override
    public CMCommand format(TouristMessage touristMessage) {
        CMCommand command = super.format(touristMessage);
        command.addParam(Fields.DATE, touristMessage.getTransDate());
        command.addParam(Fields.TIME, touristMessage.getTransTime());
        command.addParam(Fields.BRANCH_DOC_NO, touristMessage.getDocumentNo());
        command.addParam(Fields.DEST_ACCOUNT, touristMessage.getAccountNo());
        command.addParam(Fields.PAN,touristMessage.getCardNo());
        command.setCommandName(TJCommand.CMD_TOURIST_CARD_REVOKE);
        command.addParam(Fields.MN_ID, touristMessage.getId1());
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try{
        StringBuilder responseStr = new StringBuilder();
        TouristMessage touristMessage = (TouristMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        responseStr.insert(0, touristMessage.createResponseHeader()) ;
        return responseStr.toString();
        } catch (Exception e){
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
