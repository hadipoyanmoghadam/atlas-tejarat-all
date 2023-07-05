package branch.dpi.atlas.service.cm.handler.pg;


import branch.dpi.atlas.service.cm.handler.cms.giftDeposit.GiftDepositReq;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.CardPolicy;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.DChargeReq;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.ImmediateChargeReq;
import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementReq;
import branch.dpi.atlas.service.cm.handler.pg.cardStatement.CardStatementReq;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.ChargeRecordsReq;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReportReq;
import branch.dpi.atlas.service.cm.handler.pg.policyHistory.PolicyHistoryReq;
import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSRegisterReq;
import branch.dpi.atlas.service.cm.handler.pg.stockDeposit.StockDepositReq;
import branch.dpi.atlas.service.cm.handler.pg.stockFollowUp.StockFollowUpReq;
import branch.dpi.atlas.service.cm.handler.pg.summaryReport.SummaryReportReq;
import branch.dpi.atlas.service.cm.handler.pg.wfp.ChargeReq;
import branch.dpi.atlas.service.cm.handler.pg.wfp.StatementReq;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import branch.dpi.atlas.util.ImmediateCardUtil;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: September 11, 2017
 * Time: 17:04:03 PM
 */
public class ValidateMessage extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {
            String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);

            String rrn = msg.getAttributeAsString(Fields.RRN);
            String cardNo = msg.getAttributeAsString(Fields.PAN);
            String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);

            if (!ImmediateCardUtil.validateElement(rrn, BranchMessage.MESSAGE_SEQUENCE)) {
                msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID RRN!");
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }

            if (requestType.equals(Constants.POLICY_PRESENTATION_ELEMENT) || requestType.equals(Constants.BALANCE_GROUPCARD_ELEMENT) ||
                    requestType.equals(Constants.POLICY_ENDED_PRESENTATION_ELEMENT)
                    || requestType.equals(Constants.SMS_INQUIRY_ELEMENT) || requestType.equals(Constants.POLICY_ENDED_ELEMENT)) {
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            }else if (requestType.equals(Constants.POLICY_HISTORY_ELEMENT)) {
                PolicyHistoryReq policyHistoryReq = (PolicyHistoryReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(policyHistoryReq.getFromDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID FromDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(policyHistoryReq.getToDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID ToDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.CHILD_INFO_ELEMENT)) {
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.startsWith(Constants.POLICY_ELEMENT)) {
                CardPolicy policyMsg = (CardPolicy) msg.getAttribute(Fields.POLICY_CLASS);
                if (!requestType.startsWith(Constants.POLICY_UPDATE_ALL_ELEMENT) && !requestType.startsWith(Constants.POLICY_UPDATE_COLLECTION_ELEMENT) && !ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (ImmediateCardUtil.validateElementISZero(policyMsg.getAmount()) || ImmediateCardUtil.validateElementISZero(String.valueOf(policyMsg.getCount()))
                        || ImmediateCardUtil.validateElementISZero(String.valueOf(policyMsg.getInterval())) || !ImmediateCardUtil.validateElement(policyMsg.getIntervalType(), 1))

                {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Policy Data!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!policyMsg.getType().equals("0") && !policyMsg.getType().equals("1"))

                {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Policy Charge Type!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!policyMsg.getIntervalType().equals("D") && !policyMsg.getIntervalType().equals("W") && !policyMsg.getIntervalType().equals("M"))

                {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Policy Charge Interval Type!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

                if (!ImmediateCardUtil.validateElement(policyMsg.getStartDate(), BranchMessage.REQUEST_DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Charge Start Date!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.CHARGE_REPORT_ELEMENT)) {
                ChargeReportReq chargeReportType = (ChargeReportReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(chargeReportType.getFromDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID FromDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(chargeReportType.getToDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID ToDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.WFP_CHARGE_ELEMENT)) {
                ChargeReq chargeType = (ChargeReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(chargeType.getFromDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID FromDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(chargeType.getToDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID ToDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.WFP_STATEMENT_ELEMENT)) {
                StatementReq wfpStmType = (StatementReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(wfpStmType.getFromDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID FromDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(wfpStmType.getToDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID ToDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.DCHARGE_ELEMENT)) {
                DChargeReq dChargeMsg = (DChargeReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElement(dChargeMsg.getType().trim(), BranchMessage.REQUEST_TYPE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID DCharge Type!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.SMS_REGISTER_ELEMENT)) {
                SMSRegisterReq smsMsg = (SMSRegisterReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
               if(!ImmediateCardUtil.validateElement(smsMsg.getParentNotify().trim(), BranchMessage.REQUEST_TYPE) || !ImmediateCardUtil.validateElement(smsMsg.getChildNotify().trim(), BranchMessage.REQUEST_TYPE)
                       || !ImmediateCardUtil.validateElement(smsMsg.getTransNotify().trim(), BranchMessage.REQUEST_TYPE) || !ImmediateCardUtil.validateElement(smsMsg.getChargeNotify().trim(), BranchMessage.REQUEST_TYPE)){
                   msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Notify Parameter!");
                   throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
               }
            } else if (requestType.equals(Constants.IMMEDIATE_CHARGE_ELEMENT)) {
                ImmediateChargeReq immediateChargeMsg = (ImmediateChargeReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (ImmediateCardUtil.validateElementISZero(immediateChargeMsg.getAmount())) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Amount!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else  if (requestType.equals(Constants.STATEMENT_GROUPCARD_ELEMENT)) {
                CardStatementReq statementType = (CardStatementReq)msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(statementType.getFromDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID FromDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(statementType.getToDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID ToDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            }else  if (requestType.equals(Constants.SUMMARY_ELEMENT)) {
                SummaryReportReq summaryReportReq = (SummaryReportReq)msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(summaryReportReq.getFromDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID FromDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(summaryReportReq.getToDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID ToDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            }else  if (requestType.equals(Constants.CHARGE_PRESENTATION_ELEMENT)) {
                ChargeRecordsReq chargeType = (ChargeRecordsReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(cardNo, BranchMessage.CARD_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID CardNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(chargeType.getFromDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID FromDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(chargeType.getToDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID ToDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            }else  if (requestType.equals(Constants.STATEMENT_ELEMENT)) {
                AccountStatementReq statementType = (AccountStatementReq)msg.getAttribute(CMMessage.COMMAND_OBJ);

                if (!ImmediateCardUtil.validateElementNotZero(accountNo, BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID AccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(statementType.getFromDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID FromDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(statementType.getToDate(), BranchMessage.DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID ToDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.STOCK_DEPOSIT_ELEMENT)) {
                StockDepositReq stockDepositReq = (StockDepositReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(stockDepositReq.getSrcAccountNo(), BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID srcAccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(stockDepositReq.getDestAccountNo(), BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID destAccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (ImmediateCardUtil.validateElementISZero(stockDepositReq.getAmount())) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Amount!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
            } else if (requestType.equals(Constants.STOCK_FOLLOWUP_ELEMENT)) {
                StockFollowUpReq stockFollowUpReq = (StockFollowUpReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
                if (!ImmediateCardUtil.validateElementNotZero(stockFollowUpReq.getSrcAccountNo(), BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID srcAccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElementNotZero(stockFollowUpReq.getDestAccountNo(), BranchMessage.ACCOUNT_NO)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID destAccountNo!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (ImmediateCardUtil.validateElementISZero(stockFollowUpReq.getAmount())) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID Amount!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElement(stockFollowUpReq.getOrigRrn(), BranchMessage.MESSAGE_SEQUENCE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID origRRN!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElement(stockFollowUpReq.getOrigTransDate(), Constants.TRANSACTION_DATE)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID origTransactionDate!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }
                if (!ImmediateCardUtil.validateElement(stockFollowUpReq.getOrigTransTime(), BranchMessage.TRANS_TIME)) {
                    msg.setAttribute(Fields.ACTION_MESSAGE, "INVALID origTransTime!");
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
                }

            }

        } catch (Exception e) {
            log.error("ERROR :::Inside ValidateMessage.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.FORMAT_ERROR));
        }

    }
}
