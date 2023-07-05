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
 * Date: Sep 4, 2017
 * Time: 03:14 PM
 */
public class ConvertorCreditsBalance extends CreditsBaseConvertor implements CreditsToIMFFormater {

    public CMCommand format(CreditsMessage creditsMessage) {
        CMCommand command = super.format(creditsMessage);
        command.setCommandName(TJCommand.CMD_CREDITS_BALANCE);
        command.addParam(Fields.PAN,getCardNo(creditsMessage.getCardNo()));
        command.addParam(Fields.CARD_SEQUENCE_NUMBER, "1");
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        command.addParam(Fields.SRC_ACCOUNT, creditsMessage.getAccountNo());

        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            CreditsMessage creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(creditsMessage.getAvailableBalance()).
                    append(creditsMessage.getActualBalance()).
                    append(creditsMessage.getBlockedAmount()).
                    append(creditsMessage.getAccountStatus());
            if (msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equals("0"))
                responseStr.append(creditsMessage.getCardNo());
            else
                responseStr.append(ISOUtil.padleft("", creditsMessage.CARD_NO, '0'));
            responseStr.insert(0, creditsMessage.createResponseHeader());
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }

    }

    private String getCardNo(String cardno) {
        if (cardno != null && !cardno.equals("") && cardno.startsWith(Constants.BANKE_TEJARAT_BIN))
            return cardno + "000";
        else
            return cardno;
    }
}
