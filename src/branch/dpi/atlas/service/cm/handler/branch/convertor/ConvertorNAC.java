package branch.dpi.atlas.service.cm.handler.branch.convertor;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.imf.Fields;
import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.CMMessage;

import java.util.Map;


/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: May 20, 2013
 * Time: 1:19:52 PM
 */
public class ConvertorNAC extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(branchMsg.createResponseHeader());
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}



