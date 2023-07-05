package branch.dpi.atlas.service.cm.source.branch;

import branch.dpi.atlas.service.cm.handler.cms.groupCharge.*;
import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementReq;
import branch.dpi.atlas.service.cm.handler.pg.cardBalance.CardBalanceReq;
import branch.dpi.atlas.service.cm.handler.pg.cardStatement.CardStatementReq;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.ChargeRecordsReq;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReportReq;
import branch.dpi.atlas.service.cm.handler.pg.childInfo.ChildInfoReq;
import branch.dpi.atlas.service.cm.handler.pg.policyHistory.PolicyHistoryReq;
import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSInquiryReq;
import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSRegisterReq;
import branch.dpi.atlas.service.cm.handler.pg.summaryReport.SummaryReportReq;
import branch.dpi.atlas.service.cm.handler.pg.wfp.ChargeReq;
import branch.dpi.atlas.service.cm.handler.pg.wfp.StatementReq;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchSource;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.source.CMSource;
import dpi.atlas.service.cm.source.sparrow.message.FormatException;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

/**
 * User: R.Nasiri
 * Date: March 12, 2020
 * Time: 10:56 AM
 */
public class GroupWebListener implements BranchRequestListener {
    private static Log log = LogFactory.getLog(GroupWebListener.class);

    private CMHandler cmHandler;

    public GroupWebListener() {
    }

    public boolean process(BranchSource source, String msg_str) {
        if (log.isInfoEnabled()) log.info("Inside GroupWebListener::process()");
        if (log.isInfoEnabled()) log.info("Incoming GroupWebMessage:" + msg_str);

        CMMessage msg = new CMMessage();
        HashMap holder = new HashMap();
        try {

            try {

                processMessage(msg, msg_str, source);
                getChainHandler().process(msg, holder);
            }catch (FormatException e){
                try {
                    source.sendBranch(msg_str);
                } catch (Exception e1) {
                    log.error("Can not send response to Client: " + e1);
                }
            }

        } catch (CMFault fault) {
            if (log.isInfoEnabled()) log.info("faultCode:" + fault.getFaultCode());
            CMSource cmSource = getChainHandler().getCMSource();
            CMHandler faultHandler = cmSource.getFaultHandler(fault.getFaultCode());

            if (log.isInfoEnabled()) log.info("FaultHandler : " + faultHandler.getClass().getName());
            try {
                msg.setAttribute(CMMessage.FAULT, fault);
                msg.setAttribute(CMMessage.FAULT_CODE, fault.getFaultCode());
                faultHandler.process(msg, holder);
            } catch (CMFault cmFault) {
                //TODO: handle Exception
                log.error(cmFault);
                try {
                    source.sendBranch(msg_str);
                } catch (IOException e) {
                    log.error("Can not send response to Client: " + e);
                }
            }

        } catch (Exception e) {
            //TODO: handle Exception
            e.printStackTrace();
            log.error(e);
            try {
                source.sendBranch(msg_str);
            } catch (IOException e1) {
                log.error("Can not send response to Client: " + e1);
            }
        }
        return true;
    }

    private void processMessage(CMMessage msg, String msg_str, BranchSource source) throws FormatException {
        try {
            if (msg_str.startsWith(Constants.BALANCE_GROUPCARD_ELEMENT)) {
                //BALANCE_GROUPCARD#ChannelCod#RRN#CardNo#AccountNo
                String[] msgArray=msg_str.split(Constants.GROUP_WEB_MEG_SEPARATOR);
                if(msgArray.length!=5){
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                    throw new FormatException(ActionCode.FORMAT_ERROR);
                }
                String serviceType=msgArray[1];
                CardBalanceReq balanceType = new CardBalanceReq();
                balanceType.setRrn(msgArray[2]);
                balanceType.setCardno(msgArray[3]);
                balanceType.setAccountNo(msgArray[4]);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, balanceType);
                msg.setAttribute(Fields.RRN, balanceType.getRrn().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, balanceType.getAccountNo().trim());
                msg.setAttribute(Fields.PAN, balanceType.getCardno().trim());
                msg.setAttribute(Fields.ROW, Constants.GROUP_CARD_CHILD);
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.BALANCE_GROUPCARD_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.BALANCE_GROUPCARD_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, serviceType);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            } else if (msg_str.startsWith(Constants.STATEMENT_GROUPCARD_ELEMENT)) {
                //STATEMENT_GROUPCARD#ChannelCod#RRN#CardNo#AccountNo#FromDate#ToDate
                String[] msgArray=msg_str.split(Constants.GROUP_WEB_MEG_SEPARATOR);
                if(msgArray.length!=7){
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                    throw new FormatException(ActionCode.FORMAT_ERROR);
                }
                String serviceType=msgArray[1];
                CardStatementReq statementType = new CardStatementReq();
                statementType.setRrn(msgArray[2]);
                statementType.setCardno(msgArray[3]);
                statementType.setAccountNo(msgArray[4]);
                statementType.setFromDate(msgArray[5]);
                statementType.setToDate(msgArray[6]);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, statementType);
                msg.setAttribute(Fields.RRN, statementType.getRrn().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, statementType.getAccountNo().trim());
                msg.setAttribute(Fields.PAN, statementType.getCardno().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.STATEMENT_GROUPCARD_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.STATEMENT_GROUPCARD_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, serviceType);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);

            } else if (msg_str.startsWith(Constants.CHARGE_PRESENTATION_ELEMENT)) {
                //CHARGE_PRESENTATION#ChannelCod#RRN#CardNo#AccountNo#FromDate#ToDate
                String[] msgArray=msg_str.split(Constants.GROUP_WEB_MEG_SEPARATOR);
                if(msgArray.length!=7){
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                    throw new FormatException(ActionCode.FORMAT_ERROR);
                }
                String serviceType=msgArray[1];
                ChargeRecordsReq chargeType = new ChargeRecordsReq();
                chargeType.setRrn(msgArray[2]);
                chargeType.setCardno(msgArray[3]);
                chargeType.setAccountNo(msgArray[4]);
                chargeType.setFromDate(msgArray[5]);
                chargeType.setToDate(msgArray[6]);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, chargeType);
                msg.setAttribute(Fields.RRN, chargeType.getRrn().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, chargeType.getAccountNo().trim());
                msg.setAttribute(Fields.PAN, chargeType.getCardno().trim());
                msg.setAttribute(Fields.ROW, Constants.GROUP_CARD_CHILD);
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.CHARGE_PRESENTATION_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CHARGE_RECORDS_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, serviceType);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            } else if (msg_str.startsWith(Constants.CHILD_INFO_ELEMENT)) {
                //CHILDINFO#ChannelCod#RRN#CardNo
                String[] msgArray=msg_str.split(Constants.GROUP_WEB_MEG_SEPARATOR);
                if(msgArray.length!=4){
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                    throw new FormatException(ActionCode.FORMAT_ERROR);
                }
                String serviceType=msgArray[1];
                ChildInfoReq childInfo = new ChildInfoReq();
                childInfo.setRrn(msgArray[2]);
                childInfo.setCardno(msgArray[3]);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, childInfo);
                msg.setAttribute(Fields.RRN, childInfo.getRrn().trim());
                msg.setAttribute(Fields.ACCOUNT_NO,"");
                msg.setAttribute(Fields.PAN, childInfo.getCardno().trim());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.CHILD_INFO_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.CHILD_INFO_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, serviceType);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);

            } else if (msg_str.startsWith(Constants.STATEMENT_ELEMENT)) {
                //STATEMENT#ChannelCod#RRN#AccountNo#FromDate#ToDate
                String[] msgArray=msg_str.split(Constants.GROUP_WEB_MEG_SEPARATOR);
                if(msgArray.length!=6){
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                    throw new FormatException(ActionCode.FORMAT_ERROR);
                }
                String serviceType=msgArray[1];
                AccountStatementReq statementType = new AccountStatementReq();
                statementType.setRrn(msgArray[2]);
                statementType.setAccountNo(msgArray[3]);
                statementType.setFromDate(msgArray[4]);
                statementType.setToDate(msgArray[5]);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, statementType);
                msg.setAttribute(Fields.RRN, statementType.getRrn().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, statementType.getAccountNo().trim());
                msg.setAttribute(Fields.PAN, "");
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.STATEMENT_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.STATEMENT_ACCOUNT_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, serviceType);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            } else if (msg_str.contains(Constants.SUMMARY_ELEMENT)) {
                //CHR_TRANS_SUMMARY#ChannelCod#RRN#CardNo#AccountNo#FromDate#ToDate
                String[] msgArray=msg_str.split(Constants.GROUP_WEB_MEG_SEPARATOR);
                if(msgArray.length!=7){
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                    throw new FormatException(ActionCode.FORMAT_ERROR);
                }
                String serviceType=msgArray[1];
                SummaryReportReq summaryReportReq = new SummaryReportReq();
                summaryReportReq.setRrn(msgArray[2]);
                summaryReportReq.setCardNo(msgArray[3]);
                summaryReportReq.setAccountNo(msgArray[4]);
                summaryReportReq.setFromDate(msgArray[5]);
                summaryReportReq.setToDate(msgArray[6]);
                log.info("PARSED");
                msg.setAttribute(CMMessage.COMMAND_OBJ, summaryReportReq);
                msg.setAttribute(Fields.RRN, summaryReportReq.getRrn().trim());
                msg.setAttribute(Fields.ACCOUNT_NO, summaryReportReq.getAccountNo().trim());
                msg.setAttribute(Fields.PAN, summaryReportReq.getCardNo().trim());
                msg.setAttribute(Fields.FROM_DATE, summaryReportReq.getFromDate());
                msg.setAttribute(Fields.TO_DATE, summaryReportReq.getToDate());
                msg.setAttribute(CMMessage.REQUEST_TYPE, Constants.SUMMARY_ELEMENT);
                msg.setAttribute(Fields.MESSAGE_TYPE, Constants.SUMMARY_MSG_TYPE);
                msg.setAttribute(CMMessage.SERVICE_TYPE, serviceType);
                msg.setAttribute(CMMessage.REQUEST_STR, msg_str);
            } else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new FormatException(ActionCode.FORMAT_ERROR);

            }

            msg.setAttribute(CMMessage.MESSAGE_SOURCE, source);

        } catch (FormatException e) {
            log.error("Incoming Branch message has invalid format::"+msg_str);
            throw e;
        } catch (Exception e) {
            log.error(e);
            try {
                source.sendBranch(msg_str);
            } catch (IOException e1) {
                log.error("Can not send response to Client: " + e1);
            }

        }

    }

    public void setChainHandler(CMHandler cmHandler) {
        this.cmHandler = cmHandler;
    }

    public CMHandler getChainHandler() {
        return cmHandler;
    }
}
