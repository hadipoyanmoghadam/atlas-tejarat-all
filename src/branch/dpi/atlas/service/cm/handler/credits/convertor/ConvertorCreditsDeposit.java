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
 * Time: 03:38 PM
 */
public class ConvertorCreditsDeposit extends CreditsBaseConvertor implements CreditsToIMFFormater {
    @Override
    public CMCommand format(CreditsMessage creditsMessage) {
        CMCommand command = super.format(creditsMessage);
        command.addParam(Fields.DATE, creditsMessage.getTransDate());
        command.addParam(Fields.TIME, creditsMessage.getTransTime());
        command.addParam(Fields.AMOUNT, creditsMessage.getAmount());
        command.addParam(Fields.BRANCH_DOC_NO, creditsMessage.getDocumentNo());
        command.addParam(Fields.DOCUMENT_DESCRIPTION, creditsMessage.getDocumentDescription());
        command.addParam(Fields.OPERATION_CODE, creditsMessage.getOperationCode());
        command.addParam(Fields.DEST_ACCOUNT, creditsMessage.getAccountNo());
        command.addParam(Fields.PAN, Constants.BANKE_TEJARAT_BIN_NEW);
        command.addParam(Fields.TERMINAL_ID, creditsMessage.getTerminalId());
        command.setCommandName(TJCommand.CMD_CREDITS_DEPOSIT);
        command.addParam(Fields.MN_ID, creditsMessage.getId1());
        command.addParam(Constants.CREDIT_DEBIT, Constants.DEBIT_VALUE);

        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            CreditsMessage creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(creditsMessage.getActualBalance());
            responseStr.insert(0, creditsMessage.createResponseHeader());

            if (creditsMessage.getTx_pk() != null)
                responseStr.append(ISOUtil.padleft(creditsMessage.getTx_pk().trim(), 14, ' '));
            else
                responseStr.append(ISOUtil.padleft("", 14, ' '));

            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
