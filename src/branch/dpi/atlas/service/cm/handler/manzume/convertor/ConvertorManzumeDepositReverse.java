package branch.dpi.atlas.service.cm.handler.manzume.convertor;


import branch.dpi.atlas.service.cm.imf.manzume.ManzumeToIMFFormater;
import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.util.Map;

/**
 * User: F.Heydari
 * Date: NOV 11, 2022
 * Time: 08:53 PM
 */
public class ConvertorManzumeDepositReverse extends ManzumeBaseConvertor implements ManzumeToIMFFormater {
    @Override
    public CMCommand format(ManzumeMessage manzumeMessage) {
        CMCommand command = super.format(manzumeMessage);

        command.addParam(Fields.DATE, manzumeMessage.getTransDate());
        command.addParam(Fields.TIME, manzumeMessage.getTransTime());
        command.addParam(Fields.BRANCH_DOC_NO, manzumeMessage.getDocumentNo());
        command.addParam(Fields.OPERATION_CODE, manzumeMessage.getOperationCode());
        command.addParam(Fields.SRC_ACCOUNT, manzumeMessage.getAccountNo());
        command.addParam(Fields.TERMINAL_ID, manzumeMessage.getTerminalId());
        command.addParam(Fields.ORIG_MESSAGE_DATA, manzumeMessage.getOrigMessageData());
        command.setCommandName(TJCommand.CMD_MANZUME_DEPOSIT_REVERSAL);
        command.addParam(Constants.CREDIT_DEBIT, Constants.DEBIT_VALUE);
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_SGB);
        command.addParam(Constants.DEST_HOST_ID,Constants.HOST_ID_CFS);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            ManzumeMessage manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(manzumeMessage.createResponseHeader());

            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
