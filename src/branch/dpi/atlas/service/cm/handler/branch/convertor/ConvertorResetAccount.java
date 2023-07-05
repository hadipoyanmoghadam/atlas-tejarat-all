package branch.dpi.atlas.service.cm.handler.branch.convertor;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Dec 29, 2013
 * Time: 10:33:07 AM
 */
public class ConvertorResetAccount extends BranchBaseConvertor implements BranchToIMFFormater {
    public CMCommand format(BranchMessage branchMsg) {

        CMCommand command = super.format(branchMsg);
        command.setCommandName(TJCommand.CMD_BRANCH_RESET_ACCOUNT);
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault{
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