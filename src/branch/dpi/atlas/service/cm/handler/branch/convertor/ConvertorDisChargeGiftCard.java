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
 * User: R.Nasiri
 * Date: Jun 21, 2015
 * Time: 3:25 PM
 */
public class ConvertorDisChargeGiftCard extends BranchBaseConvertor implements BranchToIMFFormater {
    @Override
    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        command.addParam(Fields.DATE, branchMsg.getTransDate());
        command.addParam(Fields.TIME, branchMsg.getTransTime());
        command.addParam(Fields.DEST_ACCOUNT, branchMsg.getAccountNo());
        command.addParam(Fields.PAN, getCardNo(branchMsg.getCardNo()));
        command.addParam(Fields.TERMINAL_ID, branchMsg.getTerminalId());
        command.addParam(Fields.NATIONAL_CODE, branchMsg.getNationalCode());
        command.addParam(Fields.DOCUMENT_DESCRIPTION, branchMsg.getNationalCode());
        command.setCommandName(TJCommand.CMD_BRANCH_DISCHARGE_GIFTCARD);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try{
        StringBuilder responseStr = new StringBuilder();
        BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        responseStr.append(branchMsg.createResponseHeader())
        .append(ISOUtil.padleft(branchMsg.getAvailableBalance(), branchMsg.AMOUNT, '0'))
        .append(ISOUtil.padleft(branchMsg.getDocumentNo(), branchMsg.DOCUMENT_NO, '6'));
        return responseStr.toString();
        } catch (Exception e){
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
