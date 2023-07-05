package branch.dpi.atlas.service.cm.handler.cms.groupCharge;

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
 * User: R.Nasiri
 * Date: November 03, 2019
 * Time: 09:04:03
 */
public class ImmediateChargeResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            ImmediateChargeReq immediateChargeMsg = (ImmediateChargeReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(ImmediateChargeResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ImmediateChargeResp immediateChargeResp = new ImmediateChargeResp();
            immediateChargeResp.setActioncode(actionCode);
            immediateChargeResp.setDesc(actionMsg);
            immediateChargeResp.setRrn(immediateChargeMsg.getRrn());
            immediateChargeResp.setCardNo(immediateChargeMsg.getCardNo());
            immediateChargeResp.setAccountNo(immediateChargeMsg.getAccountNo());
            immediateChargeResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            marshaller.marshal(immediateChargeResp, responseTypeXml);


            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside DChargeResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

