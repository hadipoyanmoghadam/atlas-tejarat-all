package branch.dpi.atlas.service.cm.handler.branch.convertor;

import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * User: F.Heidari
 * Date: July 27 2019
 * Time: 10:27 AM
 */
public class ConvertorRemittanceInquiry extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(branchMsg.createResponseHeader())
                    .append(ISOUtil.padleft(branchMsg.getRemittance_request_no(), branchMsg.REMITTANCE_REQUEST_NO, '0'))
                    .append(ISOUtil.padleft(branchMsg.getRemittanceDate(), branchMsg.REMITTANCE_DATE, '0'))
                    .append(ISOUtil.padleft(branchMsg.getNationalCode(), branchMsg.NATIONAL_CODE, ' '))
                    .append(ISOUtil.padleft(branchMsg.getExt_IdNumber(), branchMsg.EXT_ID_NUMBER, '0'))
                    .append(ISOUtil.padleft(branchMsg.getBirthDate_remittance(), branchMsg.BIRTH_DATE, '0'))
                    .append(ISOUtil.padleft(branchMsg.getTelNumber_remittance(), branchMsg.REMITTANCE_TELE, '0'))
                    .append(ISOUtil.padleft(branchMsg.getExpirDate_remittance(), branchMsg.REMITTANCE_EXPIRE_DATE, '0'))
                    .append(ISOUtil.padleft(branchMsg.getRemittanceAmount(), branchMsg.AMOUNT, '0'))
                    .append(ISOUtil.padleft(branchMsg.getRemittanceFeeAmount(), branchMsg.AMOUNT, '0'))
                    .append(ISOUtil.padleft(branchMsg.getRemittanceStatus(), branchMsg.REMITTANCE_STATUS, ' '))
                    .append(ISOUtil.padleft(branchMsg.getRemittanceMessage_Sequence(), branchMsg.MESSAGE_SEQUENCE, '0'));
            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}


