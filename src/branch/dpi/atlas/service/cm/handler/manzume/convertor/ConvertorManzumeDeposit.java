package branch.dpi.atlas.service.cm.handler.manzume.convertor;

import branch.dpi.atlas.service.cm.imf.manzume.ManzumeToIMFFormater;
import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * User: F.Heydari
 * Date: NOV 1, 2022
 * Time: 14:25:21 AM
 */
public class ConvertorManzumeDeposit extends ManzumeBaseConvertor implements ManzumeToIMFFormater {
    @Override
    public CMCommand format(ManzumeMessage manzumeMessage) {
        CMCommand command = super.format(manzumeMessage);

        command.addParam(Fields.DATE, manzumeMessage.getTransDate());
        command.addParam(Fields.TIME, manzumeMessage.getTransTime());
        command.addParam(Fields.AMOUNT, manzumeMessage.getAmount());
        command.addParam(Fields.BRANCH_DOC_NO, manzumeMessage.getDocumentNo());
        command.addParam(Fields.DOCUMENT_DESCRIPTION, manzumeMessage.getDocumentDescription());
        command.addParam(Fields.OPERATION_CODE, manzumeMessage.getOperationCode());
//        command.addParam(Fields.DEST_ACCOUNT, manzumeMessage.getAccountNo());
        command.addParam(Fields.PAN, Constants.BANKE_TEJARAT_BIN_NEW);
        command.addParam(Fields.TERMINAL_ID, "MN");
        command.setCommandName(TJCommand.CMD_MANZUME_DEPOSIT);
        command.addParam(Fields.MN_ID, manzumeMessage.getId1());
       command.addParam(Fields.PAYID2, manzumeMessage.getId2());
        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_SGB);
        command.addParam(Constants.DEST_HOST_ID,Constants.HOST_ID_CFS);



        return command;

    }

    public String createResponse(CMMessage msg, Map map) throws CMFault{
        try{
            StringBuilder responseStr = new StringBuilder();
            ManzumeMessage manzumeMessage = (ManzumeMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(manzumeMessage.createResponseHeader())
            .append(ISOUtil.padleft(manzumeMessage.getActualBalance().trim(), manzumeMessage.ACTUAL_BALANCE, '0'));
            return responseStr.toString();
        } catch (Exception e){
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
