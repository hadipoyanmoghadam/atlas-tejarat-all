package branch.dpi.atlas.service.cm.handler.cms.customerResp;

import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.DateUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: jan 12, 2015
 * Time: 10:04:03 PM
 */
public class customerResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            CUSTOMERType customerMsg = (CUSTOMERType) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance("branch.dpi.atlas.service.cm.handler.cms.customerResp", CUSTOMERRESPONSEType.class.getClassLoader());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            CUSTOMERRESPONSEType customerresponseType = new CUSTOMERRESPONSEType();
            customerresponseType.setActioncode(actionCode);
            customerresponseType.setDesc(actionMsg);
            customerresponseType.setCardno(customerMsg.getCardno());
            customerresponseType.setRecordflag(customerMsg.getRecordflag());
            customerresponseType.setRrn(customerMsg.getRrn());
            customerresponseType.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());
            marshaller.marshal(customerresponseType, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());

        } catch (Exception e) {
            log.error("ERROR :::Inside customerResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

