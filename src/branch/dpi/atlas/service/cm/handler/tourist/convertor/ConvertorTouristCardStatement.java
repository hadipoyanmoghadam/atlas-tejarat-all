package branch.dpi.atlas.service.cm.handler.tourist.convertor;

import branch.dpi.atlas.service.cm.imf.tourist.TouristToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
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
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 04:46 PM
 */
public class ConvertorTouristCardStatement extends TouristBaseConvertor implements TouristToIMFFormater {

    public CMCommand format(TouristMessage touristMessage) {
        CMCommand command = super.format(touristMessage);
        command.setCommandName(TJCommand.CMD_TOURIST_CARD_STATEMENT);
        command.addParam(Fields.PAN, touristMessage.getCardNo());
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        command.addParam(Fields.SRC_ACCOUNT, touristMessage.getAccountNo());
        command.addParam(Fields.FROM_DATE, touristMessage.getFromDate());
        command.addParam(Fields.TO_DATE, touristMessage.getToDate());
        command.addParam(Fields.FROM_TIME, touristMessage.getFromTime());
        command.addParam(Fields.TO_TIME, touristMessage.getToTime());
        command.addParam(Fields.TRANS_COUNT, touristMessage.getTransactionCount());
        command.addParam(Fields.MN_RRN, touristMessage.getFromSequence());
        command.addParam(Fields.REQUEST_TYPE, Constants.STATEMENT_ALL);    //STATMENT_TYPE   0:TRANSACTION   1:COUNT

        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            TouristMessage touristMessage = (TouristMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(touristMessage.createResponseHeader());
            if (msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equals("1"))
                responseStr.append(ISOUtil.padleft(touristMessage.getAccountNo(),touristMessage.ACCOUNT_NO, '0'));
            else
                responseStr.append(ISOUtil.padleft("", touristMessage.ACCOUNT_NO, '0'));
            responseStr.append(ISOUtil.padleft(touristMessage.getTransactionCount(), 5, '0')).append(touristMessage.getResponse());
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }

    }
}
