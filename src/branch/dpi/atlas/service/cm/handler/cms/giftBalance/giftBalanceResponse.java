package branch.dpi.atlas.service.cm.handler.cms.giftBalance;

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
 * Date: December 8, 2015
 * Time: 14:04:03
 */
public class giftBalanceResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            GiftBalanceReq giftBalanceMsg = (GiftBalanceReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(GiftBalanceResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            GiftBalanceResp giftBalanceresponseType = new GiftBalanceResp();
            giftBalanceresponseType.setActioncode(actionCode);
            giftBalanceresponseType.setDesc(actionMsg);
            giftBalanceresponseType.setRrn(giftBalanceMsg.getRrn());
            giftBalanceresponseType.setBranchCode(giftBalanceMsg.getBranchCode());
            giftBalanceresponseType.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());


            if (actionCode.equals(ActionCode.APPROVED)) {
                giftBalanceresponseType.setAccountNo(msg.getAttributeAsString(Fields.CENTER_CREDIT_ACCOUNT));
                giftBalanceresponseType.setAccountgroup(Constants.GIFT_CARD_007);
                giftBalanceresponseType.setTransAmount(msg.getAttributeAsString(Fields.TRANS_AMOUNT));
                giftBalanceresponseType.setBranchDocNo(msg.getAttributeAsString(Fields.BRANCH_DOC_NO));
                giftBalanceresponseType.setTransDate(msg.getAttributeAsString(Fields.TRANS_DATE));
            }


            marshaller.marshal(giftBalanceresponseType, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside giftBalanceResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}
