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
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * User:F.Heydari
 * Date:29 Dec 2019
 * Time:9:27 AM
 */
public class ConvertorSiminStatment extends BranchBaseConvertor implements BranchToIMFFormater {
    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        command.setCommandName(TJCommand.CMD_SIMIN_STATEMENT);
        command.addParam(Fields.PAN, getCardNo(branchMsg.getCardNo()));
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());
        command.addParam(Fields.FROM_DATE, branchMsg.getFromDate());
        command.addParam(Fields.TO_DATE, branchMsg.getToDate());
        command.addParam(Fields.BRANCH_ID, branchMsg.getBranchCode());
        command.addParam(Fields.TRANS_COUNT,"150");
        command.addParam(Fields.MN_RRN, "");
        command.addParam(Fields.REQUEST_TYPE, Constants.FULL_STATEMENT);
        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

            responseStr.append(ISOUtil.padleft(branchMsg.getTransactionCount(),5,'0')).append(branchMsg.getResponse());
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
