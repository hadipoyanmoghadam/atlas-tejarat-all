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
 * Time: 03:24 PM
 */
public class ConvertorCreditsStatement extends CreditsBaseConvertor implements CreditsToIMFFormater {

    public CMCommand format(CreditsMessage creditsMessage) {
        CMCommand command = super.format(creditsMessage);
        command.setCommandName(TJCommand.CMD_CREDITS_STATEMENT);
        command.addParam(Fields.PAN, getCardNo(creditsMessage.getCardNo()));
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        command.addParam(Fields.SRC_ACCOUNT, creditsMessage.getAccountNo());
        command.addParam(Fields.FROM_DATE, creditsMessage.getFromDate());
        command.addParam(Fields.TO_DATE, creditsMessage.getToDate());
        command.addParam(Fields.FROM_TIME, creditsMessage.getFromTime());
        command.addParam(Fields.TO_TIME, creditsMessage.getToTime());
        command.addParam(Fields.TRANS_COUNT, creditsMessage.getTransactionCount());
        command.addParam(Fields.DEBIT_CREDIT, creditsMessage.getCreditDebit());
        command.addParam(Fields.MIN_AMOUNT, creditsMessage.getTransactionMinimumAmount());
        command.addParam(Fields.MAX_AMOUNT, creditsMessage.getTransactionMaximumAmount());
        command.addParam(Fields.BRANCH_DOC_NO, creditsMessage.getTransactionDocumentNumber());
        command.addParam(Fields.DOCUMENT_DESCRIPTION, creditsMessage.getTransactionDescription());
        command.addParam(Fields.OPERATION_CODE, creditsMessage.getOperationCode());
        command.addParam(Fields.MN_RRN, creditsMessage.getFromSequence());
        command.addParam(Fields.REQUEST_TYPE, Constants.FULL_STATEMENT);    //STATMENT_TYPE   0:TRANSACTION   1:COUNT  2:FULL STATMENT

        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {                                       // add to live  971215
            StringBuilder responseStr = new StringBuilder();
            CreditsMessage creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
             if (msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equals("0"))
                responseStr.append(creditsMessage.getCardNo());
            else
                responseStr.append(ISOUtil.padleft("", creditsMessage.CARD_NO, '0'));
            responseStr.append(ISOUtil.padleft(creditsMessage.getTransactionCount(), 5, '0')).append(creditsMessage.getResponse());
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
