package branch.dpi.atlas.service.cm.handler.groupWeb;

import branch.dpi.atlas.service.cm.handler.groupWeb.cardBalance.cardBalanceResponse;
import branch.dpi.atlas.service.cm.handler.groupWeb.cardStatement.cardStatementResponse;
import branch.dpi.atlas.service.cm.handler.groupWeb.childInfo.ChildInfoResponse;
import branch.dpi.atlas.service.cm.handler.groupWeb.chargeRecords.chargeRecordsResponse;
import branch.dpi.atlas.service.cm.handler.groupWeb.accountStatement.AccountStatementResponse;
import branch.dpi.atlas.service.cm.handler.groupWeb.summaryReport.SummaryReportResponse;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 13, 2020
 * Time: 07:25 PM
 */
public class CreateResponse extends CMHandlerBase implements Configurable {
    private String action_code;

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        action_code = configuration.get("action-code");
        if ((action_code == null))
            action_code = "";
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside CreateResponse:process()");
        if (!action_code.equals(""))
            msg.setAttribute(Fields.ACTION_CODE, action_code);

        try {

            String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);

            if (requestType.equals(Constants.BALANCE_GROUPCARD_ELEMENT)) {
                cardBalanceResponse cardBalanceResp = new cardBalanceResponse();
                cardBalanceResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.STATEMENT_GROUPCARD_ELEMENT)) {
                cardStatementResponse cardStatementResp = new cardStatementResponse();
                cardStatementResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.CHILD_INFO_ELEMENT)) {
                ChildInfoResponse childInfoResponse = new ChildInfoResponse();
                childInfoResponse.doProcess(msg, holder);
            } else if (requestType.equals(Constants.CHARGE_PRESENTATION_ELEMENT)) {
                chargeRecordsResponse chargeRecordsResp = new chargeRecordsResponse();
                chargeRecordsResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.STATEMENT_ELEMENT)) {
                AccountStatementResponse accountStatementResponse = new AccountStatementResponse();
                accountStatementResponse.doProcess(msg, holder);
            } else if (requestType.equals(Constants.SUMMARY_ELEMENT)) {
                SummaryReportResponse summaryReportResponse = new SummaryReportResponse();
                summaryReportResponse.doProcess(msg, holder);
            }
            int msgId = 1;
            if (msg.getAttribute(Fields.MESSAGE_ID) != null)
                msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
            msg.setAttribute(Fields.MESSAGE_ID, msgId + "");
            msg.setAttribute(Constants.RESULT, msg.getAttributeAsString(CMMessage.RESPONSE));
        } catch (Exception e) {
            log.error("ERROR :::Inside CreateResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}

