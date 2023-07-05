package branch.dpi.atlas.service.cm.handler.cms.childCard;

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
 * Date: Nov 3, 2018
 * Time: 10:04:03 PM
 */
public class childCardResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            ChildCardReq childCardMsg = (ChildCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(ChildCardResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ChildCardResp childCardResp = new ChildCardResp();
            childCardResp.setActioncode(actionCode);
            childCardResp.setDesc(actionMsg);
            childCardResp.setCardno(childCardMsg.getCardno());
            childCardResp.setRecordflag(childCardMsg.getRecordflag());
            childCardResp.setRrn(childCardMsg.getRrn());
            childCardResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());
            marshaller.marshal(childCardResp, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());

        } catch (Exception e) {
            log.error("ERROR :::Inside childCardResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

