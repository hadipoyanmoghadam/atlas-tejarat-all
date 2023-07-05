package branch.dpi.atlas.service.cm.handler.pg.wfp;

import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReportList;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReportReq;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReportResp;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.TransactionList;
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
 * Created by sh.Behnaz on 7/2/19.
 */
public class StatementResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {
            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            StatementReq StatementMsg = (StatementReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(StatementResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StatementResp statementResp = new StatementResp();
            statementResp.setActioncode(actionCode);
            statementResp.setDesc(actionMsg);
            statementResp.setRrn(StatementMsg.getRrn());
            statementResp.setAccountNo(StatementMsg.getAccountNo());
            statementResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                statementResp.setTrans((WFPTransactionList) msg.getAttribute(Fields.TRANSACTION));
            }
            marshaller.marshal(statementResp, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside WFP StatementResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

