package dpi.atlas.service.cm.source.jms;

import branch.dpi.atlas.service.cm.handler.cms.groupCharge.*;
import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementReq;
import branch.dpi.atlas.service.cm.handler.pg.cardBalance.CardBalanceReq;
import branch.dpi.atlas.service.cm.handler.pg.cardStatement.CardStatementReq;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.ChargeRecordsReq;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReportReq;
import branch.dpi.atlas.service.cm.handler.pg.policyHistory.PolicyHistoryReq;
import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSInquiryReq;
import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSRegisterReq;
import branch.dpi.atlas.service.cm.handler.pg.childInfo.ChildInfoReq;
import branch.dpi.atlas.service.cm.handler.pg.stockDeposit.StockDepositReq;
import branch.dpi.atlas.service.cm.handler.pg.stockFollowUp.StockFollowUpReq;
import branch.dpi.atlas.service.cm.handler.pg.summaryReport.SummaryReportReq;
import branch.dpi.atlas.service.cm.handler.pg.wfp.ChargeReq;
import branch.dpi.atlas.service.cm.handler.pg.wfp.StatementReq;
import branch.dpi.atlas.service.cm.source.branch.message.FormatException;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.source.CMSource;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: September 10, 2017
 * Time: 13:56
 */
public class PGMessageProcessor implements Runnable {
    private static Log log = LogFactory.getLog(PGMessageProcessor.class);

    Message message;
    CMMessage msg;
    Map holder;
    Connector connector = null;
    private CMHandler handler;
    private CMSource cmSource;


    public PGMessageProcessor(Message message, CMHandler handler, CMSource cmSource, Connector connector) {
        this.message = message;
        this.handler = handler;
        this.cmSource = cmSource;
        this.connector = connector;
    }

    public String GenerateErrorXML(String actionCodeStr, String desc_str) {
        String xmlstr;
        Element root = new Element("root");
        Document doc = new Document(root);
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

    public void processMessage() throws Exception {
        msg = new CMMessage();
        holder = new HashMap();
        TextMessage textMessage;
        String msg_str = "";

        try {
            if (message.getJMSCorrelationID() != null && !message.getJMSCorrelationID().equals("")) {
                msg.setAttribute("JMSCorrelationID", message.getJMSCorrelationID());
                log.debug("message.JMSCorrelationID() = " + msg.getAttributeAsString("JMSCorrelationID"));
            } else
                msg.setAttribute("JMSCorrelationID", message.getJMSMessageID());

            if (message.getJMSReplyTo() != null)
                msg.setAttribute("JMSReplyTo", message.getJMSReplyTo());

            if (message instanceof TextMessage) {
                textMessage = (TextMessage) message;
                msg_str = textMessage.getText();
                log.debug("message arrived : " + msg_str);
            }

            if (msg_str.equals(Constants.CONNECTION_TEST)) {
                try {
                    CMMessage responseMsg = new CMMessage();
                    responseMsg.setAttribute("result", Constants.CONNECTION_TEST);
                    if (msg.hasAttribute("JMSReplyTo")) {
                        responseMsg.setAttribute("JMSReplyTo", msg.getAttribute("JMSReplyTo"));
                    }
                    if (msg.hasAttribute("JMSCorrelationID")) {
                        responseMsg.setAttribute("JMSCorrelationID", msg.getAttribute("JMSCorrelationID"));
                    }
                    connector.sendAsync(responseMsg);
                    return;
                } catch (Exception e) {
                    throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, e);
                }
            } else {
                if (msg_str.contains(Constants.POLICY_ELEMENT)) {
                    CardPolicy cardPolicy = new CardPolicy();
                    if (msg_str.contains(Constants.POLICY_UPDATE_ALL_ELEMENT)) {
                        JAXBContext context = JAXBContext.newInstance(UpdateAllPolicyReq.class);
                        StringReader stringReader = new StringReader(msg_str);
                        Unmarshaller u = context.createUnmarshaller();
                        UpdateAllPolicyReq updatePolicyReq = (UpdateAllPolicyReq) u.unmarshal(stringReader);
                        cardPolicy.getPolicyMsg(updatePolicyReq, Constants.POLICY_UPDATE_ALL_ELEMENT);
                        log.info("PARSED");
                        msg.setAttribute(CMMessage.COMMAND_OBJ, updatePolicyReq);
                        msg.setAttribute(Fields.RRN, updatePolicyReq.getRrn().trim());
                        msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_UPDATE_ALL_ELEMENT);
                        msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_UPDATE_ALL_MSG_TYPE);
                        msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                        msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                        msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
                        msg.setAttribute(Fields.PAN, "");
                        msg.setAttribute(Fields.ACCOUNT_NO, updatePolicyReq.getAccountNo().trim());
                    } else if (msg_str.contains(Constants.POLICY_UPDATE_COLLECTION_ELEMENT)) {
                        JAXBContext context = JAXBContext.newInstance(UpdateCollectionPolicyReq.class);
                        StringReader stringReader = new StringReader(msg_str);
                        Unmarshaller u = context.createUnmarshaller();
                        UpdateCollectionPolicyReq updatePolicyReq = (UpdateCollectionPolicyReq) u.unmarshal(stringReader);
                        cardPolicy.getPolicyMsg(updatePolicyReq, Constants.POLICY_UPDATE_COLLECTION_ELEMENT);
                        log.info("PARSED");
                        msg.setAttribute(CMMessage.COMMAND_OBJ, updatePolicyReq);
                        msg.setAttribute(Fields.RRN, updatePolicyReq.getRrn().trim());
                        msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_UPDATE_COLLECTION_ELEMENT);
                        msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_UPDATE_COLLECTION_MSG_TYPE);
                        msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                        msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                        msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
                        msg.setAttribute(Fields.PAN, "");
                        msg.setAttribute(Fields.ACCOUNT_NO, updatePolicyReq.getAccountNo().trim());
                    } else if (msg_str.contains(Constants.POLICY_UPDATE_ELEMENT)) {
                        JAXBContext context = JAXBContext.newInstance(UpdatePolicyReq.class);
                        StringReader stringReader = new StringReader(msg_str);
                        Unmarshaller u = context.createUnmarshaller();
                        UpdatePolicyReq updatePolicyReq = (UpdatePolicyReq) u.unmarshal(stringReader);
                        cardPolicy.getPolicyMsg(updatePolicyReq, Constants.POLICY_UPDATE_ELEMENT);
                        log.info("PARSED");
                        msg.setAttribute(CMMessage.COMMAND_OBJ, updatePolicyReq);
                        msg.setAttribute(Fields.RRN, updatePolicyReq.getRrn().trim());
                        msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_UPDATE_ELEMENT);
                        msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_UPDATE_MSG_TYPE);
                        msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                        msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                        msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
                        msg.setAttribute(Fields.PAN, updatePolicyReq.getCardno().trim());
                        msg.setAttribute(Fields.ACCOUNT_NO, updatePolicyReq.getAccountNo().trim());

                    } else if (msg_str.contains(Constants.POLICY_CREATE_ELEMENT)) {
                        JAXBContext context = JAXBContext.newInstance(CreatePolicyReq.class);
                        StringReader stringReader = new StringReader(msg_str);
                        Unmarshaller u = context.createUnmarshaller();
                        CreatePolicyReq createPolicyReq = (CreatePolicyReq) u.unmarshal(stringReader);
                        cardPolicy.getPolicyMsg(createPolicyReq, Constants.POLICY_CREATE_ELEMENT);
                        log.info("PARSED");
                        msg.setAttribute(CMMessage.COMMAND_OBJ, createPolicyReq);
                        msg.setAttribute(Fields.RRN, createPolicyReq.getRrn().trim());
                        msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_CREATE_ELEMENT);
                        msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_CREATE_MSG_TYPE);
                        msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                        msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                        msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
                        msg.setAttribute(Fields.PAN, createPolicyReq.getCardno().trim());
                        msg.setAttribute(Fields.ACCOUNT_NO, createPolicyReq.getAccountNo().trim());
                    } else if (msg_str.contains(Constants.POLICY_PRESENTATION_ELEMENT)) {
                        JAXBContext context = JAXBContext.newInstance(PresentationPolicyReq.class);
                        StringReader stringReader = new StringReader(msg_str);
                        Unmarshaller u = context.createUnmarshaller();
                        PresentationPolicyReq presentationPolicyReq = (PresentationPolicyReq) u.unmarshal(stringReader);
                        log.info("PARSED");
                        msg.setAttribute(CMMessage.COMMAND_OBJ, presentationPolicyReq);
                        msg.setAttribute(Fields.RRN, presentationPolicyReq.getRrn().trim());
                        msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_PRESENTATION_ELEMENT);
                        msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_PRESENTATION_MSG_TYPE);
                        msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                        msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                        msg.setAttribute(Fields.PAN, presentationPolicyReq.getCardno().trim());
                        msg.setAttribute(Fields.ACCOUNT_NO, presentationPolicyReq.getAccountNo().trim());
                        cardPolicy.getPolicyMsg(presentationPolicyReq, Constants.POLICY_PRESENTATION_ELEMENT);
                        msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);
                    } else if (msg_str.contains(Constants.POLICY_ENDED_PRESENTATION_ELEMENT)) {
                        JAXBContext context = JAXBContext.newInstance(PresentationEndedPolicyReq.class);
                        StringReader stringReader = new StringReader(msg_str);
                        Unmarshaller u = context.createUnmarshaller();
                        PresentationEndedPolicyReq presentationPolicyReq = (PresentationEndedPolicyReq) u.unmarshal(stringReader);
                        log.info("PARSED");
                        msg.setAttribute(CMMessage.COMMAND_OBJ, presentationPolicyReq);
                        msg.setAttribute(Fields.RRN, presentationPolicyReq.getRrn().trim());
                        msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_ENDED_PRESENTATION_ELEMENT);
                        msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_ENDED_PRESENTATION_MSG_TYPE);
                        msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                        msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                        msg.setAttribute(Fields.PAN, presentationPolicyReq.getCardno().trim());
                        msg.setAttribute(Fields.ACCOUNT_NO, presentationPolicyReq.getAccountNo().trim());
                        cardPolicy.getPolicyMsg(presentationPolicyReq, Constants.POLICY_ENDED_PRESENTATION_ELEMENT);
                        msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);

                    } else if (msg_str.contains(Constants.POLICY_ENDED_ELEMENT)) {
                        JAXBContext context = JAXBContext.newInstance(EndedPolicyReq.class);
                        StringReader stringReader = new StringReader(msg_str);
                        Unmarshaller u = context.createUnmarshaller();
                        EndedPolicyReq endedPolicyReq = (EndedPolicyReq) u.unmarshal(stringReader);
                        log.info("PARSED");
                        msg.setAttribute(CMMessage.COMMAND_OBJ, endedPolicyReq);
                        msg.setAttribute(Fields.RRN, endedPolicyReq.getRrn().trim());
                        msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_ENDED_ELEMENT);
                        msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_ENDED_MSG_TYPE);
                        msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                        msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                        msg.setAttribute(Fields.PAN, endedPolicyReq.getCardno().trim());
                        msg.setAttribute(Fields.ACCOUNT_NO, endedPolicyReq.getAccountNo().trim());
                        cardPolicy.getPolicyMsg(endedPolicyReq, Constants.POLICY_ENDED_ELEMENT);
                        msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);

                    } else if (msg_str.contains(Constants.POLICY_HISTORY_ELEMENT)) {
                        JAXBContext context = JAXBContext.newInstance(PolicyHistoryReq.class);
                        StringReader stringReader = new StringReader(msg_str);
                        Unmarshaller u = context.createUnmarshaller();
                        PolicyHistoryReq policyHistoryReq = (PolicyHistoryReq) u.unmarshal(stringReader);
                        log.info("PARSED");
                        msg.setAttribute(CMMessage.COMMAND_OBJ, policyHistoryReq);
                        msg.setAttribute(Fields.RRN, policyHistoryReq.getRrn().trim());
                        msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.POLICY_HISTORY_ELEMENT);
                        msg.setAttribute(Fields.MESSAGE_TYPE, Constants.POLICY_HISTORY_MSG_TYPE);
                        msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                        msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                        msg.setAttribute(Fields.PAN, policyHistoryReq.getCardno().trim());
                        msg.setAttribute(Fields.ACCOUNT_NO, policyHistoryReq.getAccountNo().trim());
                        msg.setAttribute(Fields.FROM_DATE, policyHistoryReq.getFromDate().trim());
                        msg.setAttribute(Fields.TO_DATE, policyHistoryReq.getToDate().trim());
                        cardPolicy.getPolicyMsg(policyHistoryReq, Constants.POLICY_HISTORY_ELEMENT);
                        msg.setAttribute(Fields.POLICY_CLASS, cardPolicy);

                    }


                } else if (msg_str.contains(Constants.BALANCE_GROUPCARD_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(CardBalanceReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    CardBalanceReq balanceType = (CardBalanceReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, balanceType);
                    msg.setAttribute(Fields.RRN, balanceType.getRrn().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, balanceType.getAccountNo().trim());
                    msg.setAttribute(Fields.PAN, balanceType.getCardno().trim());
                    msg.setAttribute(Fields.ROW, Constants.GROUP_CARD_CHILD);
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.BALANCE_GROUPCARD_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.BALANCE_GROUPCARD_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                } else if (msg_str.contains(Constants.STATEMENT_GROUPCARD_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(CardStatementReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    CardStatementReq statementType = (CardStatementReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, statementType);
                    msg.setAttribute(Fields.RRN, statementType.getRrn().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, statementType.getAccountNo().trim());
                    msg.setAttribute(Fields.PAN, statementType.getCardno().trim());
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.STATEMENT_GROUPCARD_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.STATEMENT_GROUPCARD_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);

                } else if (msg_str.contains(Constants.CHARGE_PRESENTATION_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(ChargeRecordsReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    ChargeRecordsReq chargeType = (ChargeRecordsReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, chargeType);
                    msg.setAttribute(Fields.RRN, chargeType.getRrn().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, chargeType.getAccountNo().trim());
                    msg.setAttribute(Fields.PAN, chargeType.getCardno().trim());
                    msg.setAttribute(Fields.ROW, Constants.GROUP_CARD_CHILD);
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.CHARGE_PRESENTATION_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CHARGE_RECORDS_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                } else if (msg_str.contains(Constants.CHARGE_REPORT_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(ChargeReportReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    ChargeReportReq chargeReportType = (ChargeReportReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, chargeReportType);
                    msg.setAttribute(Fields.RRN, chargeReportType.getRrn().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, chargeReportType.getAccountNo().trim());
                    msg.setAttribute(Fields.PAN, "");
                    msg.setAttribute(Fields.ROW, Constants.GROUP_CARD_CHILD);
                    msg.setAttribute(Fields.FROM_DATE, chargeReportType.getFromDate());
                    msg.setAttribute(Fields.TO_DATE, chargeReportType.getToDate());
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.CHARGE_REPORT_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CHARGE_REPORT_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                } else if (msg_str.contains(Constants.DCHARGE_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(DChargeReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    DChargeReq dChargeReq = (DChargeReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, dChargeReq);
                    msg.setAttribute(Fields.RRN, dChargeReq.getRrn().trim());
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.DCHARGE_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.DCHARGE_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                    msg.setAttribute(Fields.PAN, dChargeReq.getCardNo().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, dChargeReq.getAccountNo().trim());
                } else if (msg_str.contains(Constants.SMS_REGISTER_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(SMSRegisterReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    SMSRegisterReq smsReq = (SMSRegisterReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, smsReq);
                    msg.setAttribute(Fields.RRN, smsReq.getRrn().trim());
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.SMS_REGISTER_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.SMS_REGISTER_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                    msg.setAttribute(Fields.PAN, smsReq.getCardNo().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, smsReq.getAccountNo().trim());
                } else if (msg_str.contains(Constants.SMS_INQUIRY_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(SMSInquiryReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    SMSInquiryReq smsReq = (SMSInquiryReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, smsReq);
                    msg.setAttribute(Fields.RRN, smsReq.getRrn().trim());
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.SMS_INQUIRY_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.SMS_INQUIRY_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                    msg.setAttribute(Fields.PAN, smsReq.getCardno().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, smsReq.getAccountNo().trim());
                } else if (msg_str.contains(Constants.CHILD_INFO_ELEMENT)) {
                     JAXBContext context = JAXBContext.newInstance(ChildInfoReq.class);
                     StringReader stringReader = new StringReader(msg_str);
                     Unmarshaller u = context.createUnmarshaller();
                    ChildInfoReq childInfo = (ChildInfoReq) u.unmarshal(stringReader);
                     log.info("PARSED");
                     msg.setAttribute(CMMessage.COMMAND_OBJ, childInfo);
                     msg.setAttribute(Fields.RRN, childInfo.getRrn().trim());
                     msg.setAttribute(Fields.ACCOUNT_NO,"");
                     msg.setAttribute(Fields.PAN, childInfo.getCardno().trim());
                     msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.CHILD_INFO_ELEMENT);
                     msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CHILD_INFO_MSG_TYPE);
                     msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                     msg.setAttribute(CMMessage.REQUEST_STR, msg_str);

                } else if (msg_str.contains(Constants.WFP_STATEMENT_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(StatementReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    StatementReq wfpStmType = (StatementReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, wfpStmType);
                    msg.setAttribute(Fields.RRN, wfpStmType.getRrn().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, wfpStmType.getAccountNo().trim());
                    msg.setAttribute(Fields.PAN,"");
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.WFP_STATEMENT_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.WFP_STATEMENT_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);

                } else if (msg_str.contains(Constants.WFP_CHARGE_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(ChargeReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    ChargeReq chargeType = (ChargeReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, chargeType);
                    msg.setAttribute(Fields.RRN, chargeType.getRrn().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, chargeType.getAccountNo().trim());
                    msg.setAttribute(Fields.FROM_DATE, chargeType.getFromDate());
                    msg.setAttribute(Fields.TO_DATE, chargeType.getToDate());
                    msg.setAttribute(Fields.PAN,"");
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.WFP_CHARGE_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.WFP_CHARGE_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);

                } else if (msg_str.contains(Constants.IMMEDIATE_CHARGE_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(ImmediateChargeReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    ImmediateChargeReq immediateChargeReq = (ImmediateChargeReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, immediateChargeReq);
                    msg.setAttribute(Fields.RRN, immediateChargeReq.getRrn().trim());
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.IMMEDIATE_CHARGE_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.IMMEDIATE_CHARGE_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                    msg.setAttribute(Fields.PAN, immediateChargeReq.getCardNo().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, immediateChargeReq.getAccountNo().trim());

                } else if (msg_str.contains(Constants.STATEMENT_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(AccountStatementReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    AccountStatementReq statementType = (AccountStatementReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, statementType);
                    msg.setAttribute(Fields.RRN, statementType.getRrn().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, statementType.getAccountNo().trim());
                    msg.setAttribute(Fields.PAN, "");
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.STATEMENT_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.STATEMENT_ACCOUNT_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                } else if (msg_str.contains(Constants.SUMMARY_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(SummaryReportReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    SummaryReportReq summaryReportReq = (SummaryReportReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, summaryReportReq);
                    msg.setAttribute(Fields.RRN, summaryReportReq.getRrn().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, summaryReportReq.getAccountNo().trim());
                    msg.setAttribute(Fields.PAN, summaryReportReq.getCardNo().trim());
                    msg.setAttribute(Fields.FROM_DATE, summaryReportReq.getFromDate());
                    msg.setAttribute(Fields.TO_DATE, summaryReportReq.getToDate());
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.SUMMARY_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.SUMMARY_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                } else if (msg_str.contains(Constants.STOCK_DEPOSIT_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(StockDepositReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    StockDepositReq stockDepositReq = (StockDepositReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, stockDepositReq);
                    msg.setAttribute(Fields.RRN, stockDepositReq.getRrn().trim());
                    msg.setAttribute(Fields.ACCOUNT_NO, stockDepositReq.getSrcAccountNo().trim());
                    msg.setAttribute(Fields.SRC_ACCOUNT, stockDepositReq.getSrcAccountNo().trim());
                    msg.setAttribute(Fields.DEST_ACCOUNT, stockDepositReq.getDestAccountNo().trim());
                    msg.setAttribute(Fields.AMOUNT, stockDepositReq.getAmount().trim());
                    msg.setAttribute(Fields.TRANS_DATE, stockDepositReq.getTransDate());
                    msg.setAttribute(Fields.TRANS_TIME, stockDepositReq.getTransTime());
                    msg.setAttribute(Fields.PAN, "");
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.STOCK_DEPOSIT_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.STOCK_DEPOSIT_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                } else if (msg_str.contains(Constants.STOCK_FOLLOWUP_ELEMENT)) {
                    JAXBContext context = JAXBContext.newInstance(StockFollowUpReq.class);
                    StringReader stringReader = new StringReader(msg_str);
                    Unmarshaller u = context.createUnmarshaller();
                    StockFollowUpReq stockFollowUpReq = (StockFollowUpReq) u.unmarshal(stringReader);
                    log.info("PARSED");
                    msg.setAttribute(CMMessage.COMMAND_OBJ, stockFollowUpReq);
                    msg.setAttribute(Fields.RRN, stockFollowUpReq.getRrn().trim());
                    msg.setAttribute(Fields.SRC_ACCOUNT, stockFollowUpReq.getSrcAccountNo().trim());
                    msg.setAttribute(Fields.DEST_ACCOUNT, stockFollowUpReq.getDestAccountNo().trim());
                    msg.setAttribute(Fields.AMOUNT, stockFollowUpReq.getAmount().trim());
                    msg.setAttribute(Fields.TRANS_DATE, stockFollowUpReq.getTransDate());
                    msg.setAttribute(Fields.TRANS_TIME, stockFollowUpReq.getTransTime());
                    String origMsgData=stockFollowUpReq.getOrigRrn().trim()+stockFollowUpReq.getOrigTransDate()+stockFollowUpReq.getOrigTransTime();
                    msg.setAttribute(Fields.ORIG_MESSAGE_DATA, origMsgData);
                    stockFollowUpReq.setOrigMsgData(origMsgData);
                    msg.setAttribute(Fields.PAN, "");
                    msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.STOCK_FOLLOWUP_ELEMENT);
                    msg.setAttribute(Fields.MESSAGE_TYPE, Constants.STOCK_FOLLOWUP_MSG_TYPE);
                    msg.setAttribute(CMMessage.SERVICE_TYPE, Fields.SERVICE_PG);
                    msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
                } else {
                    msg.setAttribute(Params.ACTION_MESSAGE, "Invalid format of Request ");
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                    throw new FormatException(ActionCode.FORMAT_ERROR);

                }

                msg.setAttribute(CMMessage.MESSAGE_SOURCE, cmSource);

            }

        } catch (FormatException e) {
            log.error("<<<<<message arrived >>>> " + msg_str);
            try {
                msg.setAttribute(Constants.RESULT, GenerateErrorXML(ActionCode.FORMAT_ERROR, "Invalid format of Request "));
                connector.sendAsync(msg);
            } catch (Exception e1) {
                log.error("Can not send response to Client: " + e1);
            }
            throw e;
        } catch (Exception e) {
            log.error(e + "<<<<<message arrived >>>> " + msg_str);
            e.printStackTrace();
            throw e;
        }
    }

    public void run() {
        try {
            processMessage();
            getChainHandler().process(msg, holder);
        } catch (CMFault fault) {
            CMHandler faultHandler = cmSource.getFaultHandler(fault.getFaultCode());
            try {
                msg.setAttribute(CMMessage.FAULT, fault);
                msg.setAttribute(CMMessage.FAULT_CODE, fault.getFaultCode());
                faultHandler.process(msg, holder);
            } catch (CMFault cmFault) {
                log.error(cmFault);
            }
        } catch (Exception e) {
            log.error(e);
        } catch (Throwable e) {
            log.error(e);
        }
    }

    public CMHandler getChainHandler() {
        log.debug("getHandler");
        return handler;
    }

}
