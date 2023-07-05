package branch.dpi.atlas.service.cm.handler.groupWeb;

import branch.dpi.atlas.service.cm.handler.cms.CMSLogger;
import branch.dpi.atlas.service.cm.handler.groupWeb.accountStatement.AccountStatementResponse;
import branch.dpi.atlas.service.cm.handler.groupWeb.summaryReport.SummaryReportResponse;
import branch.dpi.atlas.service.cm.handler.groupWeb.cardBalance.cardBalanceResponse;
import branch.dpi.atlas.service.cm.handler.groupWeb.cardStatement.cardStatementResponse;
import branch.dpi.atlas.service.cm.handler.groupWeb.chargeRecords.chargeRecordsResponse;
import branch.dpi.atlas.service.cm.handler.groupWeb.childInfo.ChildInfoResponse;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 14, 2020
 * Time: 10:30 AM
 */
public class ExternalFaultHandler extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(ExternalFaultHandler.class);
    Connector connector;

    public void doProcess(CMMessage msg, Map holder) {
        if (log.isInfoEnabled()) log.info("Inside ExternalFaultHandler:process()");

        try {
            String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);
            String actionCode = msg.getAttributeAsString(Fields.ACTION_CODE);
            if (actionCode == null) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
                msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.GENERAL_ERROR);
                log.error("Returned ActionCode is null");
            }

            if (requestType.equals(Constants.BALANCE_GROUPCARD_ELEMENT)) {
                cardBalanceResponse cardBalanceResp = new cardBalanceResponse();
                cardBalanceResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.STATEMENT_GROUPCARD_ELEMENT)) {
                cardStatementResponse cardStatementResp = new cardStatementResponse();
                cardStatementResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.CHARGE_PRESENTATION_ELEMENT)) {
                chargeRecordsResponse chargeRecordsResp = new chargeRecordsResponse();
                chargeRecordsResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.CHILD_INFO_ELEMENT)) {
                ChildInfoResponse childInfoResponse = new ChildInfoResponse();
                childInfoResponse.doProcess(msg, holder);
            } else if (requestType.equals(Constants.STATEMENT_ELEMENT)) {
                AccountStatementResponse accountStatementResponse = new AccountStatementResponse();
                accountStatementResponse.doProcess(msg, holder);
            } else if (requestType.equals(Constants.SUMMARY_ELEMENT)) {
                SummaryReportResponse summaryReportResponse = new SummaryReportResponse();
                summaryReportResponse.doProcess(msg, holder);
            } else
                msg.setAttribute(CMMessage.RESPONSE, GenerateError(ActionCode.FORMAT_ERROR, msg.getAttributeAsString(Fields.RRN)));

            int msgId = 1;
            if (msg.getAttribute(Fields.MESSAGE_ID) != null)
                msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
            msg.setAttribute(Fields.MESSAGE_ID, msgId + "");

            CMSLogger cmsLogger = new CMSLogger();
            cmsLogger.process(msg, holder);
            msg.setAttribute(Constants.RESULT, msg.getAttributeAsString(CMMessage.RESPONSE));
            connector.sendAsyncText(msg);

        } catch (Exception e) {
            log.error("Error ::: ExternalFaultHandler  for GroupWeb >>> catching exception 1 ::: " + msg.getAttributeAsString(Fields.MESSAGE_ID) + " -- " + e.getMessage());
            log.debug("msg = " + msg);

            try {
                if (!msg.hasAttribute(CMMessage.RESPONSE)) {
                    log.error("Error ::: ExternalFaultHandler for GroupWeb>>> catching exception 1 ::: There is no response, so it's set ");
                    msg.setAttribute(CMMessage.RESPONSE, GenerateError(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE, msg.getAttributeAsString(Fields.RRN)));
                }
                connector.sendAsyncText(msg);
            } catch (Exception e1) {
                log.error("Error ::: ExternalFaultHandler for GroupWeb>>> catching exception 2 ::: Error in sending message :: " + e.getMessage());
            }

        }
    }

    private static String GenerateError(String actionCodeStr,String rrn) throws Exception {
        String xmlstr = "GENERAL_RESPONSE"+Constants.GROUP_WEB_MEG_SEPARATOR+actionCodeStr+Constants.GROUP_WEB_MEG_SEPARATOR+rrn;
        return xmlstr;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}
