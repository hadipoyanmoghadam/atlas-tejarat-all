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

import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: July 28, 2020
 * Time: 04:30 PM
 */
public class ConvertorGetPayaRequestInDueDate extends BranchBaseConvertor implements BranchToIMFFormater {

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
            List<PayaRequest> requestList = (List<PayaRequest>) holder.get(Fields.PAYA_REQUEST);
            responseStr.append(branchMsg.createResponseHeader())
                    .append(ISOUtil.padleft(String.valueOf(requestList.size()), branchMsg.PAYA_COUNT, '0'));

            for (int i = 0; i < requestList.size(); i++) {

                responseStr.append(ISOUtil.padleft(requestList.get(i).getSerial().trim(), branchMsg.SERIAL, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getDueDate(), branchMsg.DATE, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getBankSend(), branchMsg.BANK_CODE, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getBankRecv(), branchMsg.BANK_CODE, ' '))
                        .append(ISOUtil.padleft(String.valueOf(requestList.get(i).getAmount()), branchMsg.AMOUNT, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getCreateDate(), branchMsg.DATE, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getSourceIban(), branchMsg.IBAN, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getPaymentCode(), branchMsg.PAYMENT_CODE, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getDestIban(), branchMsg.IBAN, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getNameSend(), branchMsg.PAYA_NAME, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getSenderMeliCode(), branchMsg.MELI_CODE, '0'))
                        .append(ISOUtil.padleft(requestList.get(i).getSenderpostalCode(), branchMsg.POSTAL_CODE, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getSenderAddress(), branchMsg.ADDRESS1, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getSenderTell(), branchMsg.SENDER_TEL, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getReceiverName(), branchMsg.PAYA_NAME, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getDescription(), branchMsg.PAYA_DESCRIPTION, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getSenderShahab(), branchMsg.SHAHAB, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getReciverShahab(), branchMsg.SHAHAB, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getReciverMeliCode(), branchMsg.MELI_CODE, '0'))
                        .append(ISOUtil.padleft(requestList.get(i).getReciverPostalCode(), branchMsg.POSTAL_CODE, ' '))
                        .append(ISOUtil.padleft(requestList.get(i).getReason(), branchMsg.REASON, ' '));
            }

            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
