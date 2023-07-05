package branch.dpi.atlas.service.cm.handler.branch.convertor;

import dpi.atlas.service.cm.CMFault;
import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;

import java.util.Map;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: May 27, 2013
 * Time: 4:43:58 PM
 */
public class ConvertorFollowup extends BranchBaseConvertor implements BranchToIMFFormater {
    @Override
    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);

        command.setCommandName(TJCommand.CMD_BRANCH_FOLLOWUP);
        command.addParam(Fields.ORIG_MESSAGE_DATA, branchMsg.getOrigMessageData());
        command.addParam(Fields.TERMINAL_ID, branchMsg.getTerminalId());
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(branchMsg.getOrigMessageData()).
                    append(msg.getAttribute(Fields.FOLLOWUP_ACTION_CODE));
            responseStr.insert(0, branchMsg.createResponseHeader());
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
