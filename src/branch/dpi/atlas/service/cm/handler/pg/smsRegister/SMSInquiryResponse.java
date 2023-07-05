package branch.dpi.atlas.service.cm.handler.pg.smsRegister;

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
 * Date: October 28, 2018
 * Time: 10:04:03
 */
public class SMSInquiryResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            SMSInquiryReq smsMsg = (SMSInquiryReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(SMSInquiryResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            SMSInquiryResp smsResp = new SMSInquiryResp();
            smsResp.setActioncode(actionCode);
            smsResp.setDesc(actionMsg);
            smsResp.setRrn(smsMsg.getRrn());
            smsResp.setCardNo(smsMsg.getCardno());
            smsResp.setAccountNo(smsMsg.getAccountNo());
            smsResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
              String smsStr=  msg.getAttributeAsString(Fields.SMS_NOTIFICATION);
                smsResp.setParentNotify(smsStr.substring(0,1));
                smsResp.setChildNotify(smsStr.substring(1,2));
                smsResp.setTransNotify(smsStr.substring(2,3));
                smsResp.setChargeNotify(smsStr.substring(3));
            }

            marshaller.marshal(smsResp, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside SMSInquiryResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }

    }
}

