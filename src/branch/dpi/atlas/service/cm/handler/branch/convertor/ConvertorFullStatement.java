package branch.dpi.atlas.service.cm.handler.branch.convertor;

import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
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
 * Date: Jan 13, 2014
 * Time: 03:00:45 PM
 */
public class ConvertorFullStatement extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        command.setCommandName(TJCommand.CMD_BRANCH_STATEMENT);
        command.addParam(Fields.PAN, getCardNo(branchMsg.getCardNo()));
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());
        command.addParam(Fields.FROM_DATE, branchMsg.getFromDate());
        command.addParam(Fields.TO_DATE, branchMsg.getToDate());
        command.addParam(Fields.FROM_TIME, branchMsg.getFromTime());
        command.addParam(Fields.TO_TIME, branchMsg.getToTime());
        command.addParam(Fields.TRANS_COUNT, branchMsg.getTransactionCount());
        command.addParam(Fields.DEBIT_CREDIT, branchMsg.getCreditDebit());
        command.addParam(Fields.MIN_AMOUNT, branchMsg.getTransactionMinimumAmount());
        command.addParam(Fields.MAX_AMOUNT, branchMsg.getTransactionMaximumAmount());
        command.addParam(Fields.BRANCH_DOC_NO, branchMsg.getTransactionDocumentNumber());
        command.addParam(Fields.DOCUMENT_DESCRIPTION, branchMsg.getTransactionDescription());
        command.addParam(Fields.OPERATION_CODE, branchMsg.getOperationCode());
        command.addParam(Fields.MN_RRN, branchMsg.getFromSequence());
        command.addParam(Fields.REQUEST_TYPE, Constants.FULL_STATEMENT);    //STATMENT_TYPE   0:TRANSACTION   1:COUNT  2:FULL STATMENT

        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
             if (msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equals("0"))
                responseStr.append(branchMsg.getCardNo());
            else
                responseStr.append(ISOUtil.padleft("", branchMsg.CARD_NO, '0'));
            responseStr.append(ISOUtil.padleft(branchMsg.getTransactionCount(), 5, '0')).append(branchMsg.getResponse());
            responseStr.insert(0, branchMsg.createResponseHeader());
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
