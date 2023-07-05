package branch.dpi.atlas.service.cm.handler.branch.convertor;

import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
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
 * Date: Feb 13, 2019
 * Time: 2:41 PM
 */
public class ConvertorSiminDeleteDocument extends BranchBaseConvertor implements BranchToIMFFormater {
    public CMCommand format(BranchMessage branchMsg) {

        CMCommand command = super.format(branchMsg);
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());
        command.addParam(Fields.AMOUNT, branchMsg.getAmount());
        command.addParam(Fields.BRANCH_DOC_NO, branchMsg.getDocumentNo());
        command.addParam(Fields.REQUEST_TYPE, branchMsg.getRequestType());  //1=deposit, 2=withdraw
        command.addParam(Fields.DATE, branchMsg.getTransDate());
        command.addParam(Fields.TIME, branchMsg.getTransTime());
        command.addParam(Fields.OPERATION_CODE, branchMsg.getOperationCode());
        command.addParam(Fields.TERMINAL_CODE, branchMsg.getTerminalCode());
        command.addParam(Fields.ISSUER_BRANCH_CODE, branchMsg.getIssuerBranchCode());
        command.addParam(Fields.LOG_ID, branchMsg.getFromSequence()); //log_id
        command.addParam(Fields.USER_ID, branchMsg.getUserId()); //Simin_User

        command.setCommandName(TJCommand.CMD_SIMIN_DELETE_DOCUMENT);

        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
        StringBuilder responseStr = new StringBuilder();
        BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        responseStr.append(branchMsg.createResponseHeader());
        return responseStr.toString();
        }catch (Exception e){
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}