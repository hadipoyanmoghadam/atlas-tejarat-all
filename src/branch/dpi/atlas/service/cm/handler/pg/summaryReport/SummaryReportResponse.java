package branch.dpi.atlas.service.cm.handler.pg.summaryReport;

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
 * Time: 11:40 AM
 */
public class SummaryReportResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            SummaryReportReq statementMsg = (SummaryReportReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(SummaryReportResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            SummaryReportResp statementResp = new SummaryReportResp();
            statementResp.setActioncode(actionCode);
            statementResp.setDesc(actionMsg);
            statementResp.setRrn(statementMsg.getRrn());
            statementResp.setCardno(statementMsg.getCardNo());

            String[] result= (String[]) msg.getAttribute(Fields.SUMMARY_REPORT);

            statementResp.setTransactionAmountC(result[0]!=null ? result[0] : "0");
            statementResp.setTransactionAmountD(result[1]!=null ? result[1] : "0");
            statementResp.setChargeAmount(result[2]!=null ? result[2] : "0");
            statementResp.setdChargeAmount(result[3]!= null ? result[3] : "0");
            statementResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            marshaller.marshal(statementResp, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside SummaryReportResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

