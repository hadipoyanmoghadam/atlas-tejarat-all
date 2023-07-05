package branch.dpi.atlas.service.cm.handler.groupWeb.summaryReport;

import branch.dpi.atlas.service.cm.handler.pg.summaryReport.SummaryReportReq;
import branch.dpi.atlas.service.cm.handler.pg.summaryReport.SummaryReportResp;
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
 * User: R.Nasiri
 * Date: March 14, 2020
 * Time: 09:05 PM
 */
public class SummaryReportResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String rrn = msg.getAttributeAsString(Fields.RRN);
            String pan = msg.getAttributeAsString(Fields.PAN);
            StringBuilder responseStr = new StringBuilder();

            //CHR_TRANS_SUMMARY_RESPONSE#ActionCode#RRN#CardNo#Respdatetime#ChargeAmount#DisChargeAmount#CrTransactionAmount#DbTransactionAmount

            responseStr.append("CHR_TRANS_SUMMARY_RESPONSE")
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(actionCode)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(rrn)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(pan)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {

                String[] result = (String[]) msg.getAttribute(Fields.SUMMARY_REPORT);

                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(result[2] != null ? result[2] : "0");
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(result[3] != null ? result[3] : "0");
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(result[0] != null ? result[0] : "0");
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(result[1] != null ? result[1] : "0");
            }

            msg.setAttribute(CMMessage.RESPONSE, responseStr.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside SummaryReportResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

