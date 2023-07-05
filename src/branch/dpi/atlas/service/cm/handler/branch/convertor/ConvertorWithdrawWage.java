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

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Dec 28, 2020
 * Time: 2:46 PM
 */
public class ConvertorWithdrawWage extends BranchBaseConvertor implements BranchToIMFFormater {
    @Override
    public CMCommand format(BranchMessage branchMsg) {

        CMCommand command = super.format(branchMsg);
        command.addParam(Fields.AMOUNT, branchMsg.getAmount());
        command.addParam(Fields.DATE, branchMsg.getTransDate());
        command.addParam(Fields.TIME, branchMsg.getTransTime());
        command.addParam(Fields.BRANCH_DOC_NO, branchMsg.getDocumentNo());
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());
        command.addParam(Fields.PAN, Constants.BANKE_TEJARAT_BIN_NEW);
        command.addParam(Fields.TERMINAL_ID, branchMsg.getTerminalId());
        command.addParam(Fields.OPERATION_TYPE, branchMsg.getOpType());  //0= ACH 1=RTGS
        command.addParam(Fields.GROUP_TYPE, branchMsg.getGroupType());   //0= sigle 1=Group
        command.addParam(Fields.GROUP_NO, branchMsg.getGroupNo());
        command.addParam(Fields.FILLER, branchMsg.getFiller());

        if (branchMsg.getOpType().equalsIgnoreCase(Fields.ACH_WAGE))
            command.setCommandName(TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE);
        else
            command.setCommandName(TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE);


        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault{
        try {
        StringBuilder responseStr = new StringBuilder();
        BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        responseStr.insert(0, branchMsg.createResponseHeader()) ;
        return responseStr.toString();
        }catch (Exception e){
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
