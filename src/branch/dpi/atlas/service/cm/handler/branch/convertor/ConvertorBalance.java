package branch.dpi.atlas.service.cm.handler.branch.convertor;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: Apr 16, 2013
 * Time: 5:21:45 PM
 */
public class ConvertorBalance extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        command.setCommandName(TJCommand.CMD_BRANCH_BALANCE);
        command.addParam(Fields.PAN,getCardNo(branchMsg.getCardNo()));
        command.addParam(Fields.CARD_SEQUENCE_NUMBER, "1");
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());

        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(branchMsg.getAvailableBalance()).
                    append(branchMsg.getActualBalance()).
                    append(branchMsg.getBlockedAmount()).
                    append(branchMsg.getAccountStatus());
            if (msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equals("0"))
                responseStr.append(branchMsg.getCardNo());
            else
                responseStr.append(ISOUtil.padleft("", branchMsg.CARD_NO, '0'));
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
