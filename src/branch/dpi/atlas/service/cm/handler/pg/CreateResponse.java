package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.cms.groupCharge.*;
import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementResponse;
import branch.dpi.atlas.service.cm.handler.pg.cardBalance.cardBalanceResponse;
import branch.dpi.atlas.service.cm.handler.pg.cardStatement.cardStatementResponse;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.chargeRecordsResponse;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReportResponse;
import branch.dpi.atlas.service.cm.handler.pg.policyHistory.PolicyHistoryResponse;
import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSInquiryResponse;
import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSRegisterResponse;
import branch.dpi.atlas.service.cm.handler.pg.childInfo.ChildInfoResponse;
import branch.dpi.atlas.service.cm.handler.pg.stockDeposit.StockDepositResp;
import branch.dpi.atlas.service.cm.handler.pg.stockDeposit.StockDepositResponse;
import branch.dpi.atlas.service.cm.handler.pg.stockFollowUp.StockFollowUpResp;
import branch.dpi.atlas.service.cm.handler.pg.stockFollowUp.StockFollowUpResponse;
import branch.dpi.atlas.service.cm.handler.pg.summaryReport.SummaryReportResponse;
import branch.dpi.atlas.service.cm.handler.pg.wfp.ChargeResponse;
import branch.dpi.atlas.service.cm.handler.pg.wfp.StatementResponse;
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
 * User: SH.Behnaz
 * Date: Feb 15, 2015
 * Time: 10:04:03 PM
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
        if (msg.getAttributeAsString(Fields.ACTION_MESSAGE) == null)
            msg.setAttribute(Fields.ACTION_MESSAGE, " ");

        try {

            String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);

            if (requestType.equals(Constants.POLICY_UPDATE_ALL_ELEMENT)) {
                UpdateAllPolicyResponse policyResp = new UpdateAllPolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_UPDATE_COLLECTION_ELEMENT)) {
                UpdateCollectionPolicyResponse policyResp = new UpdateCollectionPolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_UPDATE_ELEMENT)) {
                UpdatePolicyResponse policyResp = new UpdatePolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_PRESENTATION_ELEMENT)) {
                PresentationPolicyResponse policyResp = new PresentationPolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_ENDED_PRESENTATION_ELEMENT)) {
                PresentationEndedPolicyResponse policyResp = new PresentationEndedPolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.BALANCE_GROUPCARD_ELEMENT)) {
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
            } else if (requestType.equals(Constants.POLICY_CREATE_ELEMENT)) {
                CreatePolicyResponse policyResp = new CreatePolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_HISTORY_ELEMENT)) {
                PolicyHistoryResponse policyResp = new PolicyHistoryResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.CHARGE_REPORT_ELEMENT)) {
                ChargeReportResponse policyResp = new ChargeReportResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.DCHARGE_ELEMENT)) {
                DChargeResponse dChargeResp = new DChargeResponse();
                dChargeResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.SMS_REGISTER_ELEMENT)) {
                SMSRegisterResponse smsResp = new SMSRegisterResponse();
                smsResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.SMS_INQUIRY_ELEMENT)) {
                SMSInquiryResponse smsResp = new SMSInquiryResponse();
                smsResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_ENDED_ELEMENT)) {
                EndedPolicyResponse policyResp = new EndedPolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.WFP_STATEMENT_ELEMENT)) {
                StatementResponse stmResp = new StatementResponse();
                stmResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.WFP_CHARGE_ELEMENT)) {
                ChargeResponse chargeResp = new ChargeResponse();
                chargeResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.IMMEDIATE_CHARGE_ELEMENT)) {
                ImmediateChargeResponse immediateChargeResponse = new ImmediateChargeResponse();
                immediateChargeResponse.doProcess(msg, holder);
            } else if (requestType.equals(Constants.STATEMENT_ELEMENT)) {
                AccountStatementResponse accountStatementResponse = new AccountStatementResponse();
                accountStatementResponse.doProcess(msg, holder);
            } else if (requestType.equals(Constants.SUMMARY_ELEMENT)) {
                SummaryReportResponse summaryReportResponse = new SummaryReportResponse();
                summaryReportResponse.doProcess(msg, holder);
            } else if (requestType.equals(Constants.STOCK_DEPOSIT_ELEMENT)) {
                StockDepositResponse stockDepositResponse = new StockDepositResponse();
                stockDepositResponse.doProcess(msg, holder);
            } else if (requestType.equals(Constants.STOCK_FOLLOWUP_ELEMENT)) {
                StockFollowUpResponse stockFollowUpResponse = new StockFollowUpResponse();
                stockFollowUpResponse.doProcess(msg, holder);
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

