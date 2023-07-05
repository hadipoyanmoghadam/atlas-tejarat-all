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
 * User: SH.Behnaz
 * Date: July 31, 2019
 * Time: 15:04:03
 */
public class EndedPolicyResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            String rrn = msg.getAttributeAsString(Fields.RRN);
            StringWriter responseTypeXml = new StringWriter();

            JAXBContext context = JAXBContext.newInstance(EndedPolicyResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            EndedPolicyResp policyType = new EndedPolicyResp();
            policyType.setActioncode(actionCode);
            policyType.setDesc(actionMsg);
            policyType.setRrn(rrn);
            policyType.setAccountNo( msg.getAttributeAsString(Fields.ACCOUNT_NO));
            policyType.setCardNo( msg.getAttributeAsString(Fields.PAN));
            policyType.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            marshaller.marshal(policyType, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside EndedPolicyResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

