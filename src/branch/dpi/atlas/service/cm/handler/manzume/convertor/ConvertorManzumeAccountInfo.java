package branch.dpi.atlas.service.cm.handler.manzume.convertor;

import branch.dpi.atlas.service.cm.imf.manzume.ManzumeToIMFFormater;
import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.iso.ISOUtil;

import java.util.Map;

public class ConvertorManzumeAccountInfo extends ManzumeBaseConvertor implements ManzumeToIMFFormater {

    public CMCommand format(ManzumeMessage manzumeMessage) {
        CMCommand command = super.format(manzumeMessage);
        return command;
    }

    public String createResponse(CMMessage msg, Map holder) throws CMFault {
        try {
            StringBuilder responseStr = new StringBuilder();
            ManzumeMessage manzumeMessage;
            manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(manzumeMessage.createResponseHeader())
                    .append(ISOUtil.padleft(manzumeMessage.getCustomerId().trim(), manzumeMessage.CUSTOMER_ID, ' '))
                    .append(ISOUtil.padleft(manzumeMessage.getCustomerName().trim(), manzumeMessage.CUSTOMER_NAME, ' '))
                    .append(ISOUtil.padleft(manzumeMessage.getIdentificationCode().trim(), manzumeMessage.IDENTIFICATION_CODE, '0'))
                    .append(ISOUtil.padleft(manzumeMessage.getMobileNo().trim(), manzumeMessage.MOBILE_NO, '0'))
                    .append(ISOUtil.padleft(manzumeMessage.getPostalCode().trim(), manzumeMessage.POSTAL_CODE, ' '))
                    .append(ISOUtil.padleft(manzumeMessage.getAddress().trim(), manzumeMessage.ADDRESS, ' '))
                    .append(manzumeMessage.getAccountType());


            return responseStr.toString();
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
