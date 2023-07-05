package branch.dpi.atlas.service.cm.handler.branch.convertor;

import branch.dpi.atlas.model.tj.entity.PayaRequest;
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
public class ConvertorGetPayaRequest extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            PayaRequest request= (PayaRequest) holder.get(Fields.PAYA_REQUEST);
            responseStr.append(branchMsg.createResponseHeader())
                    .append(ISOUtil.padleft(request.getSerial().trim(), branchMsg.SERIAL, ' '))
                    .append(ISOUtil.padleft(request.getDueDate(), branchMsg.DATE, ' '))
                    .append(ISOUtil.padleft(request.getBankSend(), branchMsg.BANK_CODE, ' '))
                    .append(ISOUtil.padleft(request.getBankRecv(), branchMsg.BANK_CODE, ' '))
                    .append(ISOUtil.padleft(String.valueOf(request.getAmount()), branchMsg.AMOUNT, ' '))
                    .append(ISOUtil.padleft(request.getCreateDate(), branchMsg.DATE, ' '))
                    .append(ISOUtil.padleft(request.getSourceIban(), branchMsg.IBAN, ' '))
                    .append(ISOUtil.padleft(request.getPaymentCode(), branchMsg.PAYMENT_CODE, ' '))
                    .append(ISOUtil.padleft(request.getDestIban(), branchMsg.IBAN, ' '))
                    .append(ISOUtil.padleft(request.getNameSend(), branchMsg.PAYA_NAME, ' '))
                    .append(ISOUtil.padleft(request.getSenderMeliCode(), branchMsg.MELI_CODE, '0'))
                    .append(ISOUtil.padleft(request.getSenderpostalCode(), branchMsg.POSTAL_CODE, ' '))
                    .append(ISOUtil.padleft(request.getSenderAddress(), branchMsg.ADDRESS1, ' '))
                    .append(ISOUtil.padleft(request.getSenderTell(), branchMsg.SENDER_TEL, ' '))
                    .append(ISOUtil.padleft(request.getReceiverName(), branchMsg.PAYA_NAME, ' '))
                    .append(ISOUtil.padleft(request.getDescription(), branchMsg.PAYA_DESCRIPTION, ' '))
                    .append(ISOUtil.padleft(request.getSenderShahab(), branchMsg.SHAHAB, ' '))
                    .append(ISOUtil.padleft(request.getReciverShahab(), branchMsg.SHAHAB, ' '))
                    .append(ISOUtil.padleft(request.getReciverMeliCode(), branchMsg.MELI_CODE, '0'))
                    .append(ISOUtil.padleft(request.getReciverPostalCode(), branchMsg.POSTAL_CODE, ' '))
                    .append(ISOUtil.padleft(request.getReason(), branchMsg.REASON, ' '));

            request=null;

            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
