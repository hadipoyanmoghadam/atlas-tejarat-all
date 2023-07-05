package branch.dpi.atlas.service.cm.handler.branch.convertor;

import dpi.atlas.service.cm.CMFault;
import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.util.Map;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: Jun 10, 2013
 * Time: 3:48:07 PM
 */
public class ConvertorWithdraw extends BranchBaseConvertor implements BranchToIMFFormater {
    @Override
    public CMCommand format(BranchMessage branchMsg) {

        CMCommand command = super.format(branchMsg);
        command.addParam(Fields.AMOUNT, branchMsg.getAmount());
        command.addParam(Fields.DATE, branchMsg.getTransDate());
        command.addParam(Fields.TIME, branchMsg.getTransTime());
        command.addParam(Fields.BRANCH_DOC_NO, branchMsg.getDocumentNo());
        command.addParam(Fields.DOCUMENT_DESCRIPTION, branchMsg.getDocumentDescription());
        command.addParam(Fields.OPERATION_CODE, branchMsg.getOperationCode());
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());
        command.addParam(Fields.PAN, Constants.BANKE_TEJARAT_BIN_NEW);
        command.addParam(Fields.TERMINAL_ID, branchMsg.getTerminalId());
        command.addParam(Fields.REQUEST_TYPE, branchMsg.getRequestType());  //WITHDRAW_TYPE   0:with min balance   1:without min balance
        command.setCommandName(TJCommand.CMD_BRANCH_WITHDRAW);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault{
        try {
        StringBuilder responseStr = new StringBuilder();
        BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        responseStr.append(branchMsg.getActualBalance());
        responseStr.insert(0, branchMsg.createResponseHeader()) ;
        return responseStr.toString();
        }catch (Exception e){
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
