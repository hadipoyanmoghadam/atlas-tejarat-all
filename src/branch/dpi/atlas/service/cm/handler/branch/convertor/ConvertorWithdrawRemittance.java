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
 * User: F.Heidari
 * Date: July 27 2019
 * Time: 5:08 PM
 */


public class ConvertorWithdrawRemittance extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        command.addParam(Fields.DATE, branchMsg.getTransDate());
        command.addParam(Fields.TIME, branchMsg.getTransTime());
        command.addParam(Fields.BRANCH_DOC_NO, branchMsg.getDocumentNo());
        command.addParam(Fields.DOCUMENT_DESCRIPTION, branchMsg.getDocumentDescription());
        command.addParam(Fields.OPERATION_CODE, branchMsg.getOperationCode());
        command.addParam(Fields.OPERATION_CODE_FEE_AMOUNT, branchMsg.getOperationCodeFeeAmount());
        command.addParam(Fields.REQUEST_NO, branchMsg.getRemittance_request_no());
        command.addParam(Fields.REMITTANCE_DATE, branchMsg.getRemittanceDate());
        command.addParam(Fields.PAN, Constants.BANKE_TEJARAT_BIN_NEW);
        command.addParam(Fields.TERMINAL_ID, branchMsg.getTerminalId());
        command.addParam(Fields.NATIONAL_CODE, branchMsg.getNationalCode());
        command.addParam(Fields.EXTERNAL_ID_NUMBER, branchMsg.getExt_IdNumber());
        command.setCommandName(TJCommand.CMD_BRANCH_WITHDRAW_REMITTANCE);
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

