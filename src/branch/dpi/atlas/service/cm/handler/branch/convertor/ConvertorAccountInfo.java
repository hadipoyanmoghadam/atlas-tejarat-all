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
 * Date: July 7, 2015
 * Time: 01:04 PM
 */
public class ConvertorAccountInfo extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        command.setCommandName(TJCommand.CMD_SIMIN_BALANCE);
        command.addParam(Fields.PAN, Constants.BANKE_TEJARAT_BIN_NEW);
        command.addParam(Fields.CARD_SEQUENCE_NUMBER, "1");
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        command.addParam(Fields.SRC_ACCOUNT, branchMsg.getAccountNo());

        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(branchMsg.createResponseHeader())
                    .append(ISOUtil.padleft(branchMsg.getAccountType().trim(), branchMsg.ACCOUNT_TYPE, '0'))
                    .append(ISOUtil.padleft(branchMsg.getAccountGroup().trim(), branchMsg.ACCOUNT_GROUP, '0'))
                    .append(ISOUtil.padleft(branchMsg.getFirstName().trim(), branchMsg.FIRST_NAME, ' '))
                    .append(ISOUtil.padleft(branchMsg.getLastName().trim(), branchMsg.LAST_NAME, ' '))
                    .append(ISOUtil.padleft(branchMsg.getAccountStatus().trim(), branchMsg.ACCOUNT_STATUS, '0'))
                    .append(ISOUtil.padleft(branchMsg.getIssuerBranchCode().trim(), branchMsg.ISSUER_BRANCH_CODE, '0'))
                    .append(branchMsg.getAvailableBalance())
                    .append(branchMsg.getActualBalance())
                    .append(ISOUtil.padleft(msg.getAttributeAsString(Fields.HOST_ID).trim(), branchMsg.HOST_ID, '9'));

            String statusD=msg.getAttributeAsString(Fields.STATUS_D);
            if(statusD.startsWith(Constants.CM_BLOCK)){
                // non_branch_BLOCK
                if(statusD.endsWith(Constants.CM_BLOCK)){
                    //statusD=00 then all channels are block
                    statusD=Constants.BLOCK_DEPOSIT_ALL;
                }else{
                    //statusD=01 then non branch channels are block
                    statusD=Constants.BLOCK_DEPOSIT_NON_BRANCH;
                }
            }else{
                // CM_UNBLOCK
                if(statusD.endsWith(Constants.CM_BLOCK)){
                    //statusD=10 then branch is only block
                    statusD=Constants.BLOCK_DEPOSIT_BRANCH;
                }else{
                    //statusD=11 then all channels are unblock
                    statusD=Constants.UNBLOCK_DEPOSIT;
                }
            }
            responseStr.append(ISOUtil.padleft(statusD, branchMsg.STATUS_D, '0'));

            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
