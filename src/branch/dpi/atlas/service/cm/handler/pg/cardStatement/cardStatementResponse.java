package branch.dpi.atlas.service.cm.handler.pg.cardStatement;

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
import java.util.List;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: April 26, 2017
 * Time: 12:04:03
 */
public class cardStatementResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            CardStatementReq statementMsg = (CardStatementReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(CardStatementResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            CardStatementResp statementResp = new CardStatementResp();
            statementResp.setActioncode(actionCode);
            statementResp.setDesc(actionMsg);
            statementResp.setRrn(statementMsg.getRrn());
            statementResp.setCardno(statementMsg.getCardno());
            statementResp.setFromDate(statementMsg.getFromDate());
            statementResp.setToDate(statementMsg.getToDate());
            statementResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                statementResp.setTransCount(msg.getAttributeAsString(Fields.TRANS_COUNT).trim());
                statementResp.setTx((StatementTxList) msg.getAttribute(Fields.TRANSACTION));
            }
            marshaller.marshal(statementResp, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside cardStatementResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

