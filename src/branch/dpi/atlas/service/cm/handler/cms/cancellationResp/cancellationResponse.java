package branch.dpi.atlas.service.cm.handler.cms.cancellationResp;

import branch.dpi.atlas.service.cm.handler.cms.cancellation.CANCELLATIONType;
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
 * Date: May 24, 2015
 * Time: 10:04:03
 */
public class cancellationResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            CANCELLATIONType cancellationMsg = (CANCELLATIONType) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance("branch.dpi.atlas.service.cm.handler.cms.cancellationResp", CANCELLATIONRESPONSEType.class.getClassLoader());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            CANCELLATIONRESPONSEType cancellationresponseType = new CANCELLATIONRESPONSEType();
            cancellationresponseType.setActioncode(actionCode);
            cancellationresponseType.setDesc(actionMsg);
            cancellationresponseType.setRrn(cancellationMsg.getRrn());
            cancellationresponseType.setRequestNo(cancellationMsg.getRequestNo());
            cancellationresponseType.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            marshaller.marshal(cancellationresponseType, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside cancellationResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

