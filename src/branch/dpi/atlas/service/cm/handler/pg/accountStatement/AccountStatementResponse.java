package branch.dpi.atlas.service.cm.handler.pg.accountStatement;

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
 * Date: Nov 24, 2019
 * Time: 10:00 AM
 */
public class AccountStatementResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            AccountStatementReq statementMsg = (AccountStatementReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(AccountStatementResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            AccountStatementResp statementResp = new AccountStatementResp();
            statementResp.setActioncode(actionCode);
            statementResp.setDesc(actionMsg);
            statementResp.setRrn(statementMsg.getRrn());
            statementResp.setAccount_no(statementMsg.getAccountNo());
            statementResp.setFromDate(statementMsg.getFromDate());
            statementResp.setToDate(statementMsg.getToDate());
            statementResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                statementResp.setTransCount(msg.getAttributeAsString(Fields.TRANS_COUNT).trim());
                statementResp.setTx((AccountStatementTxList) msg.getAttribute(Fields.TRANSACTION));
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

