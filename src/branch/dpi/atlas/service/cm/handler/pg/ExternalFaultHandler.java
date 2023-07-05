package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.cms.CMSLogger;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.*;
import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementResponse;
import branch.dpi.atlas.service.cm.handler.pg.cardBalance.cardBalanceResponse;
import branch.dpi.atlas.service.cm.handler.pg.cardStatement.cardStatementResponse;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.chargeRecordsResponse;
import branch.dpi.atlas.service.cm.handler.pg.childInfo.ChildInfoResponse;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReportResponse;
import branch.dpi.atlas.service.cm.handler.pg.policyHistory.PolicyHistoryResponse;
import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSInquiryResponse;
import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSRegisterResponse;
import branch.dpi.atlas.service.cm.handler.pg.stockDeposit.StockDepositResponse;
import branch.dpi.atlas.service.cm.handler.pg.stockFollowUp.StockFollowUpResponse;
import branch.dpi.atlas.service.cm.handler.pg.summaryReport.SummaryReportResponse;
import branch.dpi.atlas.service.cm.handler.pg.wfp.ChargeResponse;
import branch.dpi.atlas.service.cm.handler.pg.wfp.StatementResponse;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: September 11, 2017
 * Time: 9:54:53 PM
 */
public class ExternalFaultHandler extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(ExternalFaultHandler.class);
    Connector connector;

    public void doProcess(CMMessage msg, Map holder) {
        if (log.isInfoEnabled()) log.info("Inside ExternalFaultHandler:process()");

        try {
                String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);
                String actionMsg = msg.getAttributeAsString(Fields.ACTION_MESSAGE);
                String actionCode = msg.getAttributeAsString(Fields.ACTION_CODE);
                if (actionMsg == null)
                    msg.setAttribute(Fields.ACTION_MESSAGE, " ");
                if (actionCode == null) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
                    msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.GENERAL_ERROR);
                    log.error("Returned ActionCode is null");
                }

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
             }else if (requestType.equals(Constants.POLICY_UPDATE_ALL_ELEMENT)) {
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
             } else if (requestType.equals(Constants.CHILD_INFO_ELEMENT)) {
                 ChildInfoResponse childInfoResponse = new ChildInfoResponse();
                 childInfoResponse.doProcess(msg, holder);
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
             } else
                    msg.setAttribute(CMMessage.RESPONSE, GenerateErrorXML(ActionCode.FORMAT_ERROR, "Invalid format of Request "));

            int msgId = 1;
            if (msg.getAttribute(Fields.MESSAGE_ID) != null)
                msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
            msg.setAttribute(Fields.MESSAGE_ID, msgId + "");

            CMSLogger cmsLogger = new CMSLogger();
            cmsLogger.process(msg, holder);
            msg.setAttribute(Constants.RESULT,msg.getAttributeAsString(CMMessage.RESPONSE) );
            connector.sendAsync(msg);

        } catch (Exception e) {
            log.error("Error ::: ExternalFaultHandler  for PG >>> catching exception 1 ::: " + msg.getAttributeAsString(Fields.MESSAGE_ID) + " -- " + e.getMessage());
            log.debug("msg = " + msg);

            try {
                if (!msg.hasAttribute(CMMessage.RESPONSE)) {
                    log.error("Error ::: ExternalFaultHandler for PG>>> catching exception 1 ::: There is no response, so it's set ");
                    msg.setAttribute(Constants.RESULT, GenerateErrorXML(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE, "Channel can not create response!!!"));
                }
                connector.sendAsync(msg);
            } catch (Exception e1) {
                log.error("Error ::: ExternalFaultHandler for PG>>> catching exception 2 ::: Error in sending message :: " + e.getMessage());
            }

        }
    }

    private static String GenerateErrorXML(String actionCodeStr, String desc_str) throws Exception {
        String xmlstr = "";
        Element root = new Element("root");
        Document doc = new Document(root);
        String comment = " Generated: " + DateUtil.getSystemDate() + " " + DateUtil.getSystemTime();
        doc.getContent().add(0, new Comment(comment));

        Element actionCode = new Element("ACTIONCODE");
        actionCode.setText(actionCodeStr);
        root.addContent(actionCode);
        Element desc = new Element("DESC");
        desc.setText(desc_str);
        root.addContent(desc);

        XMLOutputter out = new XMLOutputter();

        xmlstr = out.outputString(doc);
        return xmlstr;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}
