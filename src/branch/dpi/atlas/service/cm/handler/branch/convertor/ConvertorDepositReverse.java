package branch.dpi.atlas.service.cm.handler.branch.convertor;

import dpi.atlas.service.cm.CMFault;
import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.util.Constants;

import java.util.Map;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: May 27, 2013
 * Time: 12:01:14 PM
 */
public class ConvertorDepositReverse extends BranchBaseConvertor implements BranchToIMFFormater {
    @Override
    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        
        command.addParam(Fields.DATE, branchMsg.getTransDate());
        command.addParam(Fields.TIME, branchMsg.getTransTime());
        command.addParam(Fields.BRANCH_DOC_NO, branchMsg.getDocumentNo());
        command.addParam(Fields.OPERATION_CODE, branchMsg.getOperationCode());
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());
        command.addParam(Fields.TERMINAL_ID, branchMsg.getTerminalId());
        command.addParam(Fields.ORIG_MESSAGE_DATA, branchMsg.getOrigMessageData());
        command.setCommandName(TJCommand.CMD_BRANCH_DEPOSIT_REVERSAL);
        return command;





        //command.addParam(Fields.DEST_ACCOUNT, amxMessage.getDestAccount());
//        command.addParam(Fields.AMOUNT, amxMessage.getAmount());

        //command.addParam(Fields.DOCUMENT_DESCRIPTION, amxMessage.getDocumentDescription());
        //command.addParam(Fields.REQUEST_TYPE, amxMessage.getRequestType());

       // command.addParam(Fields.PAN, Constants.BANKE_TEJARAT_BIN_NEW);
       // command.addParam(Fields.PIN, amxMessage.getPin());




    }

    public String createResponse(CMMessage msg, Map map) throws CMFault{
        try{
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
