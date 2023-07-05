package branch.dpi.atlas.service.cm.handler.pg.cardBalance;

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
 * Date: April 26, 2017
 * Time: 12:04:03
 */
public class cardBalanceResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            CardBalanceReq balanceMsg = (CardBalanceReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(CardBalanceResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            CardBalanceResp balanceResp = new CardBalanceResp();
            balanceResp.setActioncode(actionCode);
            balanceResp.setDesc(actionMsg);
            balanceResp.setRrn(balanceMsg.getRrn());
            balanceResp.setCardno(balanceMsg.getCardno());
            balanceResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                balanceResp.setActualBalance(msg.getAttributeAsString(Fields.ACTUAL_BALANCE).trim());
                balanceResp.setActualBalanceSign(msg.getAttributeAsString(Fields.ACTUAL_BALANCE_SIGN).trim());
                balanceResp.setAvailableBalance(msg.getAttributeAsString(Fields.AVAILABLE_BALANCE).trim());
                balanceResp.setAvailableBalanceSign(msg.getAttributeAsString(Fields.AVAILABLE_BALANCE_SIGN).trim());
            }
            marshaller.marshal(balanceResp, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside cardBalanceResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

