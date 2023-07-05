package branch.dpi.atlas.service.cm.handler.cms.touristCard;

import branch.dpi.atlas.service.cm.handler.cms.touristCard.TouristCardReq;
import branch.dpi.atlas.service.cm.handler.cms.touristCard.TouristCardResp;
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
 * Date: April 19, 2017
 * Time: 1:04:03 PM
 */
public class touristCardResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            TouristCardReq touristCardMsg = (TouristCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(TouristCardResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            TouristCardResp touristCardResp = new TouristCardResp();
            touristCardResp.setActioncode(actionCode);
            touristCardResp.setDesc(actionMsg);
            touristCardResp.setCardno(touristCardMsg.getCardno());
            touristCardResp.setRecordflag(touristCardMsg.getRecordflag());
            touristCardResp.setRrn(touristCardMsg.getRrn());
            touristCardResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());
            marshaller.marshal(touristCardResp, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());

        } catch (Exception e) {
            log.error("ERROR :::Inside touristCardResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

