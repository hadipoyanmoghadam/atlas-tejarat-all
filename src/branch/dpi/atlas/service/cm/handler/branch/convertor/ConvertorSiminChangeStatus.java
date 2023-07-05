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
 * Date: Nov 09, 2013
 * Time: 11:33:07 AM
 */
public class ConvertorSiminChangeStatus extends BranchBaseConvertor implements BranchToIMFFormater {
    public CMCommand format(BranchMessage branchMsg) {

        CMCommand command = super.format(branchMsg);
        command.addParam(Fields.TERMINAL_ID, "");
        command.addParam(Fields.USER_ID, branchMsg.getUserId());
        command.addParam(Fields.AMOUNT, branchMsg.getAmount());
        command.addParam(Fields.BLOCK_ROW, branchMsg.getBlockRow());
        command.addParam(Fields.ACCOUNT_STATUS, branchMsg.getAccountStatus());
        command.setCommandName(TJCommand.CMD_SIMIN_CHANGE_ACCOUNT_STATUS);
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());
        command.addParam(Fields.DOCUMENT_DESCRIPTION, branchMsg.getDocumentDescription());
        command.addParam(Fields.BLCK_DATE, branchMsg.getTransDate());
        command.addParam(Fields.BLCK_TIME, branchMsg.getTransTime());
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
        StringBuilder responseStr = new StringBuilder();
        BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        responseStr.append(branchMsg.createResponseHeader());
        String block_no=msg.getAttributeAsString(Fields.BLOCK_ROW);
            if(block_no==null || "".equals(block_no))
                responseStr.append(ISOUtil.padleft("", branchMsg.BLOCK_ROW, '0'));
            else
                responseStr.append(ISOUtil.padleft(String.valueOf(block_no), branchMsg.BLOCK_ROW, '0'));
        return responseStr.toString();
        }catch (Exception e){
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}