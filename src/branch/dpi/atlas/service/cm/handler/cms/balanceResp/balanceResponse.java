package branch.dpi.atlas.service.cm.handler.cms.balanceResp;

import branch.dpi.atlas.service.cm.handler.cms.balance.BALANCEType;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: May 23, 2015
 * Time: 14:04:03
 */
public class balanceResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            BALANCEType balanceMsg = (BALANCEType) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance("branch.dpi.atlas.service.cm.handler.cms.balanceResp", BALANCERESPONSEType.class.getClassLoader());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            BALANCERESPONSEType balanceresponseType = new BALANCERESPONSEType();
            balanceresponseType.setActioncode(actionCode);
            balanceresponseType.setDesc(actionMsg);
            balanceresponseType.setRrn(balanceMsg.getRrn());
            balanceresponseType.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                balanceresponseType.setAccountNo(msg.getAttributeAsString(Fields.CENTER_CREDIT_ACCOUNT));
                balanceresponseType.setBalance(msg.getAttributeAsString(Fields.ACTUAL_BALANCE));
                balanceresponseType.setAccountgroup(Constants.GIFT_CARD_007);
            }
            marshaller.marshal(balanceresponseType, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside balanceResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

