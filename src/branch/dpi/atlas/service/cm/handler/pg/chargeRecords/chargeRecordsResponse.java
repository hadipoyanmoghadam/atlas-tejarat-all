package branch.dpi.atlas.service.cm.handler.pg.chargeRecords;

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
 * Created by sh.Behnaz on 11/25/17.
 */
public class chargeRecordsResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            ChargeRecordsReq chargeMsg = (ChargeRecordsReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(ChargeRecordsResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ChargeRecordsResp chargeResp = new ChargeRecordsResp();
            chargeResp.setActioncode(actionCode);
            chargeResp.setDesc(actionMsg);
            chargeResp.setRrn(chargeMsg.getRrn());
            chargeResp.setCardno(chargeMsg.getCardno());
            chargeResp.setAccountNo(chargeMsg.getAccountNo());
            chargeResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                chargeResp.setCharges((ChargeList) msg.getAttribute(Fields.CHARGE_LIST));
            }
            marshaller.marshal(chargeResp, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside chargeRecordsResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

