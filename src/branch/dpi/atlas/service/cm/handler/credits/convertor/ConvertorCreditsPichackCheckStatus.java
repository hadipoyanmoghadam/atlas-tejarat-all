package branch.dpi.atlas.service.cm.handler.credits.convertor;

import branch.dpi.atlas.service.cm.imf.credits.CreditsToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
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
 * Date: App 29, 2020
 * Time: 12:48 PM
 */
public class ConvertorCreditsPichackCheckStatus extends CreditsBaseConvertor implements CreditsToIMFFormater {

    public CMCommand format(CreditsMessage creditsMessage) {
        CMCommand command = super.format(creditsMessage);
        command.setCommandName(TJCommand.CMD_CREDITS_PICHACK_CHECK_STATUS);
        command.addParam(Fields.SRC_ACCOUNT, creditsMessage.getAccountNo());
        command.addParam(Fields.CHECK_NUMBER, creditsMessage.getCheckNumber());

        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            CreditsMessage creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(creditsMessage.createResponseHeader())
            .append(ISOUtil.padleft(creditsMessage.getCheckStatus(), creditsMessage.CHECK_STATUS, '0'))
            .append(ISOUtil.padleft(creditsMessage.getCheckDescription(), creditsMessage.CHECK_DESCRIPTION, ' '));

            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }

    }
}
