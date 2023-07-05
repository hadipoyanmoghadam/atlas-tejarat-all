package dpi.atlas.service.cfs.handler;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.facade.CFSFacadeNew;

import java.util.Map;

import org.jpos.iso.ISOUtil;


public class GetMessageFields extends CFSHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        holder.put(CMMessage.REQUEST, command);


        String message = command.toString();
        if (message != null && !message.equals(""))
            msg.setAttribute(Fields.MSG_STR, message);

        String requestStr = command.getParam(Fields.REQUEST_STR);
        if (requestStr != null && !requestStr.equals(""))
            msg.setAttribute(Fields.REQUEST_STR, requestStr);

        String transactionSource = command.getHeaderParam(Fields.SERVICE_TYPE);
        if (transactionSource != null && !transactionSource.equals(""))
            msg.setAttribute(Fields.SERVICE_TYPE, transactionSource);

        String applierCommitSize = command.getHeaderParam(CFSConstants.APPLIER_COMMIT_SIZE);
        if (applierCommitSize != null && !applierCommitSize.equals(""))
            msg.setAttribute(CFSConstants.APPLIER_COMMIT_SIZE, applierCommitSize);

        String messageType;
        if (transactionSource.equalsIgnoreCase(Constants.ISO_SERVICE) || transactionSource.equalsIgnoreCase(Fields.SERVICE_NASIM)) {
            messageType = command.getCommandName();
        } else
        if (transactionSource.equalsIgnoreCase(Fields.SERVICE_BRANCH) || transactionSource.equalsIgnoreCase(Fields.SERVICE_PG)||
                transactionSource.equalsIgnoreCase(Fields.SERVICE_CMS)|| transactionSource.equalsIgnoreCase(Fields.SERVICE_TOURIST_CARD) ||
                transactionSource.equalsIgnoreCase(Fields.SERVICE_CREDITS)|| transactionSource.equalsIgnoreCase(Fields.SERVICE_AMX)
                || transactionSource.equalsIgnoreCase(Fields.SERVICE_SIMIN) || transactionSource.equalsIgnoreCase(Fields.SERVICE_SAFE_BOX)||
                transactionSource.equalsIgnoreCase(Fields.SERVICE_REG_SMS) || transactionSource.equalsIgnoreCase(Fields.SERVICE_GROUP_WEB)
                || transactionSource.equalsIgnoreCase(Fields.SERVICE_MARHONAT)|| transactionSource.equalsIgnoreCase(Fields.SERVICE_MANZUME)) {
            messageType = command.getCommandName();
        } else {
            messageType = command.getHeaderParam(Fields.MESSAGE_TYPE);
        }
        if (messageType != null && !messageType.equals(""))
            msg.setAttribute(Fields.MESSAGE_TYPE, messageType);


        String txcode = command.getParam(Fields.TX_CODE);
        if (txcode != null && !txcode.equals(""))
            msg.setAttribute(Fields.TX_CODE, txcode);

        //Municipality bill payment followup
        if (command.getCommandName().equalsIgnoreCase(TJCommand.MUNICIPALITY_BILL_PAYMENY_FOLLOWUP_CMD)) {
            msg.setAttribute(Constants.MNCPLTY_BILL_ID, command.getParam(Fields.MNCPLTY_BILL_ID));
            msg.setAttribute(Constants.MNCPLTY_PAYMENT_ID, command.getParam(Fields.MNCPLTY_PAYMENT_ID));
        } else if (command.getCommandName().equalsIgnoreCase(TJCommand.ACH_FUNDTRANSFER_CMD) ||
                command.getCommandName().equalsIgnoreCase(TJCommand.ACH_FUND_TRANSFER_REVERSAL_CMD)) {
            String realDst = command.getParam(Constants.DEST_HOST_ID) != null ?
                                                      command.getParam(Constants.DEST_HOST_ID) : "";
            msg.setAttribute(Constants.REAL_DEST_HOST_ID, realDst);
            command.addParam(Constants.DEST_HOST_ID, Constants.SGB_HOSTID);
        } else if (command.getCommandName().equalsIgnoreCase(TJCommand.RTGS_FUND_TRANSFER_CMD) ||
                command.getCommandName().equalsIgnoreCase(TJCommand.RTGS_FUND_TRANSFER_REVERSAL_CMD) ||
                command.getCommandName().equalsIgnoreCase(TJCommand.ACH_FILE_REG_CMD))
            command.addParam(Constants.DEST_HOST_ID, Constants.SGB_HOSTID);
        //interanet block, unblock & get status by block number
        if (command.getCommandName().equalsIgnoreCase(TJCommand.BLOCK_ACC_BY_BLOCK_NO_CMD)) {
            msg.setAttribute(Constants.BLOCK_NO, command.getParam(Fields.BLOCK_NO));
            msg.setAttribute(Constants.BLOCK_AMOUNT, command.getParam(Fields.BLOCK_AMOUNT));
            msg.setAttribute(Constants.IS_FORCE, command.getParam(Fields.IS_FORCE));
        } else if (command.getCommandName().equalsIgnoreCase(TJCommand.UNBLOCK_ACC_BY_BLOCK_NO_CMD))
            msg.setAttribute(Constants.BLOCK_NO, command.getParam(Fields.BLOCK_NO));
        else if (command.getCommandName().equalsIgnoreCase(TJCommand.SHOW_BLOCK_AMNT_STATUS_CMD))
            msg.setAttribute(Constants.BLOCK_NO, command.getParam(Fields.BLOCK_NO));
        else
            //share stock actions
            if (messageType != null && messageType.startsWith("SHR")) {
                if (messageType.equals(TJCommand.SHARE_STCK_FT_CMD) || messageType.equals(TJCommand.SHARE_STCK_FTR_CMD)) {
                    msg.setAttribute(Constants.SRC_ACCOUNT, command.getParam(Fields.SRC_ACC_NO));
                    msg.setAttribute(Constants.DST_ACCOUNT, command.getParam(Fields.DEST_ACCOUNT_NO));
                    msg.setAttribute(Constants.AMOUNT, command.getParam(Fields.TRANS_AMOUNT));
                    msg.setAttribute(Constants.PAYMENT_CODE, command.getParam(Fields.PAYMENT_CODE));
                    msg.setAttribute(Constants.OP_CODE, command.getParam(Fields.OPERATION_CODE));
                    msg.setAttribute(Constants.TRANS_DESC, command.getParam(Fields.TRANS_DESC));
                    msg.setAttribute(Fields.DOCUMENT_DESCRIPTION, command.getParam(Fields.TRANS_DESC));
                    String time = command.getParam(Fields.TIME);
                    if (time != null)
                        msg.setAttribute(Constants.CREATE_TIME, time);
                }
                if (messageType.startsWith(TJCommand.SH_STCK_TRANS_LIST_CMD)) {
                    msg.setAttribute(Constants.MIN_AMOUNT, command.getParam(Fields.MIN_AMOUNT));
                    msg.setAttribute(Constants.MAX_AMOUNT, command.getParam(Fields.MAX_AMOUNT));
                    msg.setAttribute(Constants.FROM_DATE_TIME, command.getParam(Fields.FROM_DATE_TIME));
                    msg.setAttribute(Constants.TO_DATE_TIME, command.getParam(Fields.TO_DATE_TIME));
                    msg.setAttribute(Constants.BLOCK_STATUS, command.getParam(Fields.BLOCK_STATUS));
                    msg.setAttribute(Constants.TRANS_COUNT, command.getParam(Fields.TRANS_COUNT));
                }
                if (command.getParam(Fields.ACCOUNT_NO) != null)
                    msg.setAttribute(Constants.ACC_NO, command.getParam(Fields.ACCOUNT_NO));
                msg.setAttribute(Constants.BROKER_NO, ISOUtil.zeroUnPad(command.getParam(Fields.BROKER_NO)));
                msg.setAttribute(Constants.PROVIDER_ID, ISOUtil.zeroUnPad(command.getParam(Fields.PROVIDER_ID)));
                String date = command.getParam(Fields.DATE);
                if (date != null)
                    msg.setAttribute(Constants.CREATE_DATE, date);
                String time = command.getParam(Fields.TIME);
                if (time != null)
                    msg.setAttribute(Constants.CREATE_TIME, time);
                String blckAmnt = command.getParam(Fields.BLOCK_AMOUNT);
                if (blckAmnt != null)
                    msg.setAttribute(Constants.BLOCK_ACCOUNT, blckAmnt);
                String blckDesc = command.getParam(Fields.BLOCK_DESC);
                if (blckDesc != null)
                    msg.setAttribute(Constants.BLOCK_DESC, blckDesc);
                String blckNo = command.getParam(Fields.BLOCK_NO);
                if (blckNo != null)
                    msg.setAttribute(Constants.BLOCK_NO, blckNo);
            }

        String fPayCode = command.getParam(Fields.FIRST_PAY_CODE);
        if (fPayCode != null)
            msg.setAttribute(Fields.FIRST_PAY_CODE, fPayCode);
        else
            msg.setAttribute(Fields.FIRST_PAY_CODE, "");

        String sPayCode = command.getParam(Fields.SECOND_PAY_CODE);
        if (sPayCode != null)
            msg.setAttribute(Fields.SECOND_PAY_CODE, sPayCode);
        else
            msg.setAttribute(Fields.SECOND_PAY_CODE, "");

        String cbPayId = command.getParam(Fields.CB_PAY_ID);
        if (cbPayId != null)
            msg.setAttribute(Fields.CB_PAY_ID, cbPayId);
        else
            msg.setAttribute(Fields.CB_PAY_ID, "");

        String amount = command.getParam(Fields.AMOUNT);
        if (amount != null && !amount.equals("")) {
            if (amount.length() > 0 && amount.charAt(0) == '+')
                amount = amount.substring(1);
            msg.setAttribute(Fields.AMOUNT, amount);
        }
        String amounCurrency = command.getParam(Fields.AMOUNT_CURRENCY);
        if (amounCurrency != null && !amounCurrency.equals(""))
            msg.setAttribute(Fields.AMOUNT_CURRENCY, amounCurrency);
        else
            msg.setAttribute(Fields.AMOUNT_CURRENCY, "364");

        String currOfSrcAc = command.getParam(Fields.SRC_CURRENCY);
        if (currOfSrcAc != null && !currOfSrcAc.equals(""))
            msg.setAttribute(Fields.SRC_CURRENCY, currOfSrcAc);

        String currOfDestDisp = command.getParam(Fields.DEST_CURRENCY);
        if (currOfDestDisp != null && !currOfDestDisp.equals(""))
            msg.setAttribute(Fields.DEST_CURRENCY, currOfDestDisp);

        String srcAcc = command.getParam(Fields.SRC_ACCOUNT);
        if (log.isDebugEnabled()) log.debug("srcAccount=" + srcAcc);
        if (srcAcc != null && !srcAcc.equals("")) {
            msg.setAttribute(Fields.SRC_ACCOUNT, srcAcc.substring(3));
        }


        String destAcc = command.getParam(Fields.DEST_ACCOUNT);
        if (destAcc != null && !destAcc.equals("")) {
            if (destAcc.length() > 13)
                destAcc = destAcc.substring(0, 13);
            msg.setAttribute(Fields.DEST_ACCOUNT, destAcc);
        }

        String totalDestAcc = command.getParam(Fields.DEST_ACCOUNT);
        if (totalDestAcc != null && !totalDestAcc.equals(""))
            msg.setAttribute(Fields.TOTAL_DEST_ACCOUNT, totalDestAcc);

        String terminalID = command.getParam(Fields.TERMINAL_ID);
        if (terminalID != null && !terminalID.equals(""))
            msg.setAttribute(Fields.TERMINAL_ID, terminalID);

        String userID = command.getParam(Fields.USER_ID);
        if (userID != null && !userID.equals(""))
            msg.setAttribute(Fields.USER_ID, userID);

        String dueDate = command.getParam(Fields.DUE_DATE);
        if (dueDate != null && !dueDate.equals(""))
            msg.setAttribute(Fields.DUE_DATE, dueDate);

        String terminalType = command.getParam(Fields.TERMINAL_TYPE);
        if(terminalType != null && !terminalType.equals(""))
            msg.setAttribute(Fields.TERMINAL_TYPE, terminalType);

        String settleDate = command.getParam(Fields.SETTLEMENT_DATE);
        if(settleDate != null && !settleDate.equals(""))
            msg.setAttribute(Fields.SETTLEMENT_DATE, settleDate);

        String merchantID = command.getParam(Fields.MERCHANT_ID);
        if (merchantID != null && !merchantID.equals(""))
            msg.setAttribute(Fields.MERCHANT_ID, merchantID);

        String pan = command.getParam(Fields.PAN);
        if (pan != null && !pan.equals(""))
            msg.setAttribute(Fields.PAN, pan);

        String trnSeqNo = command.getParam(Fields.TRANSACTION_SEQUENCE_NUMBER);
        if (trnSeqNo != null && !trnSeqNo.equals(""))
            msg.setAttribute(Fields.TRANSACTION_SEQUENCE_NUMBER, trnSeqNo);

        String reqNo = command.getParam(Fields.REQUEST_NO);
        if (reqNo != null && !reqNo.equals(""))
            msg.setAttribute(Fields.REQUEST_NO, reqNo);

        String srcBIN = command.getParam(Fields.SRC_BIN);
        if (srcBIN != null && !srcBIN.equals(""))
            msg.setAttribute(Fields.SRC_BIN, srcBIN);

        String destBIN = command.getParam(Fields.DEST_BIN);
        if (destBIN != null && !destBIN.equals(""))
            msg.setAttribute(Fields.DEST_BIN, destBIN);

        String sequenceNumber = command.getHeaderParam(Fields.MESSAGE_ID);
        if (sequenceNumber != null && !sequenceNumber.equals(""))
            msg.setAttribute(Fields.MESSAGE_ID, sequenceNumber);

        String date = command.getParam(Fields.DATE);
        if (date != null && !date.equals(""))
            msg.setAttribute(Fields.DATE, date);

        String time = command.getParam(Fields.TIME);
        if (time != null && !time.equals(""))
            msg.setAttribute(Fields.TIME, time);

        String sessionID = command.getHeaderParam(Fields.SESSION_ID);
        if (sessionID != null && !sessionID.equals(""))
            msg.setAttribute(Fields.SESSION_ID, sessionID);

        String forcePost = command.getParam(Fields.FORCE_POST);
        if (forcePost != null && !forcePost.equals(""))
            msg.setAttribute(Fields.FORCE_POST, forcePost);

        String customerId = command.getParam(Fields.CUSTOMER_ID);
        if (customerId != null && !customerId.equals(""))
            msg.setAttribute(Fields.CUSTOMER_ID, customerId);

        String customername = command.getParam(Fields.CUSTOMER_NAME);
        customername = doLimitStringParameter(customername, 15);
        if (customername != null && !customername.equals(""))
            msg.setAttribute(Fields.CUSTOMER_NAME, customername);

        String customersurname = command.getParam(Fields.CUSTOMER_SUR_NAME);
        customersurname = doLimitStringParameter(customersurname, 15);
        if (customersurname != null && !customersurname.equals(""))
            msg.setAttribute(Fields.CUSTOMER_SUR_NAME, customersurname);

        String customernamef = command.getParam(Fields.CUSTOMER_NAME_F);
        customernamef = doLimitStringParameter(customernamef, 15);
        if (customernamef != null && !customernamef.equals(""))
            msg.setAttribute(Fields.CUSTOMER_NAME_F, customernamef);

        String customersurnamef = command.getParam(Fields.CUSTOMER_SUR_NAME_F);
        customersurnamef = doLimitStringParameter(customersurnamef, 15);
        if (customersurnamef != null && !customersurnamef.equals(""))
            msg.setAttribute(Fields.CUSTOMER_SUR_NAME_F, customersurnamef);

        String birthid = command.getParam(Fields.BIRTH_ID);
        if (birthid != null && !birthid.equals(""))
            msg.setAttribute(Fields.BIRTH_ID, birthid);

        String nationalcode = command.getParam(Fields.NATIONAL_CODE);
        if (nationalcode != null && !nationalcode.equals(""))
            msg.setAttribute(Fields.NATIONAL_CODE, nationalcode);

        String externalIdNumber = command.getParam(Fields.EXTERNAL_ID_NUMBER);
        if (externalIdNumber != null)
            msg.setAttribute(Fields.EXTERNAL_ID_NUMBER, externalIdNumber);

        String address = command.getParam(Fields.ADDRESS);
        address = doLimitStringParameter(address, 15);
        if (address != null && !address.equals(""))
            msg.setAttribute(Fields.ADDRESS, address);

        String phoneno = command.getParam(Fields.PHONE_NO);
        if (phoneno != null && !phoneno.equals(""))
            msg.setAttribute(Fields.PHONE_NO, phoneno);

        String feeAmountOpCode = command.getParam(Fields.OPERATION_CODE_FEE_AMOUNT);
        if (feeAmountOpCode != null && !feeAmountOpCode.equals(""))
            msg.setAttribute(Fields.OPERATION_CODE_FEE_AMOUNT, feeAmountOpCode);

        String remittanceDate = command.getParam(Fields.REMITTANCE_DATE);
        if (remittanceDate != null && !remittanceDate.equals(""))
            msg.setAttribute(Fields.REMITTANCE_DATE, remittanceDate);

        String cellphone = command.getParam(Fields.CELL_PHONE);
        if (cellphone != null && !cellphone.equals(""))
            msg.setAttribute(Fields.CELL_PHONE, cellphone);

        String faxno = command.getParam(Fields.FAX_NO);
        if (faxno != null && !faxno.equals(""))
            msg.setAttribute(Fields.FAX_NO, faxno);

        String email = command.getParam(Fields.E_MAIL);
        if (email != null && !email.equals(""))
            msg.setAttribute(Fields.E_MAIL, email);

        String language = command.getParam(Fields.LANGUAGE_ID);
        if (language != null && !language.equals(""))
            msg.setAttribute(Fields.LANGUAGE_ID, language);

        String accountType = command.getParam(Fields.ACCOUNT_TYPE);
        if (accountType != null && !accountType.equals(""))
            msg.setAttribute(Fields.ACCOUNT_TYPE, accountType);

        String accountGroup = command.getParam(Constants.SRC_ACCOUNT_GROUP);
        if (accountGroup != null && !accountGroup.equals(""))
            msg.setAttribute(Constants.SRC_ACCOUNT_GROUP, accountGroup);

        msg.setAttribute(Fields.CARD_TYPE, CFSConstants.NORMAL_CARD);
        msg.setAttribute(Fields.DEST_CARD_TYPE, CFSConstants.NORMAL_CARD);

        String status = command.getParam(Fields.STATUS);
        if (status != null && !status.equals(""))
            msg.setAttribute(Fields.STATUS, status);

        String srcBranchCode = command.getParam(Fields.SRC_BRANCH);
        if (srcBranchCode != null && !srcBranchCode.equals(""))
            msg.setAttribute(Fields.SRC_BRANCH, srcBranchCode);

        String destBranchCode = command.getParam(Fields.DEST_BRANCH);
        if (destBranchCode != null && !destBranchCode.equals(""))
            msg.setAttribute(Fields.DEST_BRANCH, destBranchCode);

        String branchCode = command.getParam(Fields.BRANCH_CODE);
        if (branchCode != null && !branchCode.equals(""))
            msg.setAttribute(Fields.BRANCH_CODE, branchCode);

        String issuerBranchCode = command.getParam(Fields.ISSUER_BRANCH_CODE);
        if (issuerBranchCode != null && !issuerBranchCode.equals(""))
            msg.setAttribute(Fields.ISSUER_BRANCH_CODE, issuerBranchCode);

        String sgbCode = command.getParam(Fields.SGB_TX_CODE);
        if (sgbCode != null && !sgbCode.equals(""))
            msg.setAttribute(Fields.SGB_TX_CODE, sgbCode);

        String log_id = command.getParam(Fields.LOG_ID);
        if (log_id != null && !log_id.equals(""))
            msg.setAttribute(Fields.LOG_ID, log_id);

        String terminalCode = command.getParam(Fields.TERMINAL_CODE);
        if (terminalCode != null && !terminalCode.equals(""))
            msg.setAttribute(Fields.TERMINAL_CODE, terminalCode);

        String branchAltCode = command.getParam(Fields.BRANCH_ALT_CODE);
        if (branchAltCode != null && !branchAltCode.equals(""))
            msg.setAttribute(Fields.BRANCH_ALT_CODE, branchAltCode);

        String branchName = command.getParam(Fields.BRANCH_NAME);
        if (branchName != null && !branchName.equals(""))
            msg.setAttribute(Fields.BRANCH_NAME, branchName);

        String regionId = command.getParam(Fields.REGION_ID);
        if (regionId != null && !regionId.equals(""))
            msg.setAttribute(Fields.REGION_ID, regionId);

        String chiefName = command.getParam(Fields.CHIEF_NAME);
        chiefName = doLimitStringParameter(chiefName, 15);
        if (chiefName != null && !chiefName.equals(""))
            msg.setAttribute(Fields.CHIEF_NAME, chiefName);

        String subHostNo = command.getParam(Fields.SUB_HOST_NO);
        if (subHostNo != null && !subHostNo.equals(""))
            msg.setAttribute(Fields.SUB_HOST_NO, subHostNo);

        String brnStatus = command.getParam(Fields.BRANCH_STATUS);
        if (brnStatus != null && !brnStatus.equals(""))
            msg.setAttribute(Fields.BRANCH_STATUS, brnStatus);

        String range = command.getParam(Fields.ACCOUNT_RANGE);
        if (range != null && !range.equals(""))
            msg.setAttribute(Fields.ACCOUNT_RANGE, range);

        String accRangeType = command.getParam(Fields.ACCOUNT_RANGE_TYPE);
        if (accRangeType != null && !accRangeType.equals(""))
            msg.setAttribute(Fields.ACCOUNT_RANGE_TYPE, accRangeType);

        String accSrcType = command.getParam(Fields.ACCOUNT_SRC_TYPE);
        if (accSrcType != null && !accSrcType.equals(""))
            msg.setAttribute(Fields.ACCOUNT_SRC_TYPE, accSrcType);

        String refNo = command.getHeaderParam(Fields.REFRENCE_NUMBER);
        if (refNo != null && !refNo.equals(""))
            msg.setAttribute(Fields.REFRENCE_NUMBER, refNo);
        else {
            refNo = command.getParam(Fields.REFRENCE_NUMBER);
            if (refNo != null && !refNo.equals(""))
                msg.setAttribute(Fields.REFRENCE_NUMBER, refNo);
        }

        String accountNo = command.getParam(Fields.ACCOUNT_NO);
        if (accountNo != null && !accountNo.equals(""))
            msg.setAttribute(Fields.ACCOUNT_NO, accountNo);

        String branchRequestIsAccountBased = command.getParam(Fields.REQUEST_IS_ACCOUNT_BASED);
        if (branchRequestIsAccountBased != null && !branchRequestIsAccountBased.equals(""))
            msg.setAttribute(Fields.REQUEST_IS_ACCOUNT_BASED, branchRequestIsAccountBased);

        String origMessageData = command.getParam(Fields.ORIG_MESSAGE_DATA);
        if (origMessageData != null && !origMessageData.equals(""))
            msg.setAttribute(Fields.ORIG_MESSAGE_DATA, origMessageData);

        String WithdrawType = command.getParam(Fields.REQUEST_TYPE);
        if ( WithdrawType!= null && !WithdrawType.equals(""))
            msg.setAttribute(Fields.REQUEST_TYPE, WithdrawType);

        String accountStatus = command.getParam(Fields.ACCOUNT_STATUS);
        if ( accountStatus!= null && !accountStatus.equals(""))
            msg.setAttribute(Fields.ACCOUNT_STATUS, accountStatus);

        String blockRow = command.getParam(Fields.BLOCK_ROW);
        if ( blockRow!= null && !blockRow.equals(""))
            msg.setAttribute(Fields.BLOCK_ROW, blockRow);

        String blockDate = command.getParam(Fields.BLCK_DATE);
        if ( blockDate!= null && !blockDate.equals(""))
            msg.setAttribute(Fields.BLCK_DATE, blockDate);

        String blockTime = command.getParam(Fields.BLCK_TIME);
        if ( blockTime!= null && !blockTime.equals(""))
            msg.setAttribute(Fields.BLCK_TIME, blockTime);

        String fromDate = command.getParam(Fields.FROM_DATE);
        if (fromDate != null && !fromDate.equals(""))
            msg.setAttribute(Fields.FROM_DATE, fromDate);

        String toDate = command.getParam(Fields.TO_DATE);
        if (toDate != null && !toDate.equals(""))
            msg.setAttribute(Fields.TO_DATE, toDate);

        String fromTime = command.getParam(Fields.FROM_TIME);
        if (fromTime != null && !fromTime.equals(""))
            msg.setAttribute(Fields.FROM_TIME, fromTime);

        String toTime = command.getParam(Fields.TO_TIME);
        if (toTime != null && !toTime.equals(""))
            msg.setAttribute(Fields.TO_TIME, toTime);

        String chargeType = command.getParam(Fields.DCHARGE_TYPE);
        if (chargeType != null && !chargeType.equals(""))
            msg.setAttribute(Fields.DCHARGE_TYPE, chargeType);

        String sgbFileDate = command.getParam(Fields.SGB_FILE_DATE);
        if (sgbFileDate != null && !sgbFileDate.equals(""))
            msg.setAttribute(Fields.SGB_FILE_DATE, sgbFileDate);

        String transCount = command.getParam(Fields.TRANS_COUNT);
        if (transCount != null && !transCount.equals(""))
            msg.setAttribute(Fields.TRANS_COUNT, transCount);

        String minAmount = command.getParam(Fields.MIN_AMOUNT);
        if (minAmount != null && !minAmount.equals(""))
            msg.setAttribute(Fields.MIN_AMOUNT, minAmount);

        String maxAmount = command.getParam(Fields.MAX_AMOUNT);
        if (maxAmount != null && !maxAmount.equals(""))
            msg.setAttribute(Fields.MAX_AMOUNT, maxAmount);

        String branchDocNo = command.getParam(Fields.BRANCH_DOC_NO);
        if (branchDocNo != null && !branchDocNo.equals(""))
            msg.setAttribute(Fields.BRANCH_DOC_NO, branchDocNo);

        String srcHostId = command.getParam(Constants.SRC_HOST_ID);
        if (srcHostId != null && !srcHostId.equals(""))
            msg.setAttribute(Constants.SRC_HOST_ID, srcHostId);

        String destHostId = command.getParam(Constants.DEST_HOST_ID);
        if (destHostId != null && !destHostId.equals(""))
            msg.setAttribute(Constants.DEST_HOST_ID, destHostId);

        String msgFollowSequence = command.getParam(Fields.MESSAGE_SEQUENCE);
        if (msgFollowSequence != null && !msgFollowSequence.equals(""))
            msg.setAttribute(Fields.MESSAGE_SEQUENCE, msgFollowSequence);

        String docDescription = command.getParam(Fields.DOCUMENT_DESCRIPTION);
//        docDescription = doLimitStringParameter(docDescription, 4);
        if (docDescription != null && !docDescription.equals(""))
            msg.setAttribute(Fields.DOCUMENT_DESCRIPTION, docDescription);

        String extraInfo = command.getParam(Fields.EXTRA_INFO);
        if (extraInfo != null && !extraInfo.equals(""))
            msg.setAttribute(Fields.EXTRA_INFO, extraInfo);

        String feeAccount = command.getParam(Fields.FEE_ACCOUNT);
        if (feeAccount != null && !feeAccount.equals(""))
            msg.setAttribute(Fields.FEE_ACCOUNT, feeAccount);

        String feeAmount = command.getParam(Fields.FEE_AMOUNT);
        if (feeAmount != null && !feeAmount.equals(""))
            msg.setAttribute(Fields.FEE_AMOUNT, feeAmount);

        String feeDocDescription = command.getParam(Fields.FEE_DOCUMENT_DESCRIPTION);
        if (feeDocDescription != null && !feeDocDescription.equals(""))
            msg.setAttribute(Fields.FEE_DOCUMENT_DESCRIPTION, feeDocDescription);

        String feeExtraInfo = command.getParam(Fields.FEE_EXTRA_INFO);
        if (feeExtraInfo != null && !feeExtraInfo.equals(""))
            msg.setAttribute(Fields.FEE_EXTRA_INFO, feeExtraInfo);

        String srcBranchId = command.getParam(Fields.CHANNEL_BRANCH_NO);
        if (srcBranchId != null && !srcBranchId.equals(""))
            msg.setAttribute(Fields.CHANNEL_BRANCH_NO, srcBranchId);

        String srcOpCode = command.getParam(Fields.OPERATION_CODE);
        if (srcOpCode != null && !srcOpCode.equals(""))
            msg.setAttribute(Fields.OPERATION_CODE, srcOpCode);

        String CRDB = command.getParam(Fields.DEBIT_CREDIT);
        if (CRDB != null && !CRDB.equals(""))
            msg.setAttribute(Fields.DEBIT_CREDIT, CRDB);

        String srcBillId = command.getParam(Fields.BLPY_BILL_ID);
        if (srcBillId != null && !srcBillId.equals(""))
            msg.setAttribute(Fields.BLPY_BILL_ID, srcBillId);

        String srcPaymentId = command.getParam(Fields.BLPY_PAYMENT_ID);
        if (srcPaymentId != null && !srcPaymentId.equals(""))
            msg.setAttribute(Fields.BLPY_PAYMENT_ID, srcPaymentId);

        String batchPk = command.getParam(CFSConstants.CURRENT_BATCH);
        if (batchPk != null && !batchPk.equals(""))
            msg.setAttribute(CFSConstants.CURRENT_BATCH, batchPk);

        String rrn = command.getParam(Fields.MN_RRN);
        if (rrn != null && !rrn.equals(""))
            msg.setAttribute(Fields.MN_RRN, rrn);

        String mnId = command.getParam(Fields.MN_ID);
        if (mnId != null && !mnId.equals(""))
            msg.setAttribute(Fields.MN_ID, mnId);

        String ID2 = command.getParam(Fields.PAYID2);
        if (ID2 != null && !ID2.equals(""))
            msg.setAttribute(Fields.PAYID2, ID2);

        String merchantTerminalId = command.getParam(Fields.MERCHANT_TERMINAL_ID);
        if (merchantTerminalId != null && !merchantTerminalId.equals(""))
            msg.setAttribute(Fields.MERCHANT_TERMINAL_ID, merchantTerminalId);

        String srcSMSNotification = command.getParam(Constants.SRC_SMS_NOTIFICATION);
        if (srcSMSNotification != null && !srcSMSNotification.equals(""))
            msg.setAttribute(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);

        String destSMSNotification = command.getParam(Constants.DEST_SMS_NOTIFICATION);
        if (destSMSNotification != null && !destSMSNotification.equals(""))
            msg.setAttribute(Constants.DEST_SMS_NOTIFICATION, destSMSNotification);

        String destAccountNature = command.getParam(Constants.DEST_ACCOUNT_NATURE);
        if (destAccountNature != null && !destAccountNature.equals(""))
            msg.setAttribute(Constants.DEST_ACCOUNT_NATURE, destAccountNature);

        String srcAccountNature = command.getParam(Constants.SRC_ACCOUNT_NATURE);
        if (srcAccountNature != null && !srcAccountNature.equals(""))
            msg.setAttribute(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);

        String operationType = command.getParam(Fields.OPERATION_TYPE);
        if (operationType != null && !operationType.equals(""))
            msg.setAttribute(Fields.OPERATION_TYPE, operationType);

        String groupType = command.getParam(Fields.GROUP_TYPE);
        if (groupType != null && !groupType.equals(""))
            msg.setAttribute(Fields.GROUP_TYPE, groupType);

        String groupNo = command.getParam(Fields.GROUP_NO);
        if (groupNo != null && !groupNo.equals(""))
            msg.setAttribute(Fields.GROUP_NO, groupNo);

        String filler = command.getParam(Fields.FILLER);
        if (filler != null && !filler.equals(""))
            msg.setAttribute(Fields.FILLER, filler);

        String isReversed = command.getParam(Constants.ISREVERSED);   // ISREVERSED includes both EBNK and SPW
        if (isReversed != null && !isReversed.equals(""))
            msg.setAttribute(Constants.ISREVERSED, isReversed);

        String isTjACHFileRec = command.getParam(Constants.IS_TJ_ACH_FILE);
        if (isTjACHFileRec != null)
            msg.setAttribute(Constants.IS_TJ_ACH_FILE, isTjACHFileRec);
        else
            msg.setAttribute(Constants.IS_TJ_ACH_FILE, "NO");
        String serviceType = command.getParam(Fields.SERVICE_TYPE);

        String cbiFlag = command.getParam(Fields.CBI_FLAG);
        if (cbiFlag != null && !cbiFlag.equals(""))
            msg.setAttribute(Fields.CBI_FLAG, cbiFlag);

        String organization = command.getParam(Fields.ORGANIZATION);
        if (organization != null && !organization.equals(""))
            msg.setAttribute(Fields.ORGANIZATION, organization);

        if(serviceType !=null &&  serviceType.equals(Constants.ISO_SERVICE))  {

            String txCode = command.getParam(Fields.TX_CODE_HOST);
            if(txCode != null && !txCode.equals("")){
                msg.setAttribute(Fields.COMMAND, txCode);
                msg.setAttribute(Fields.MESSAGE_TYPE, txCode);
            }
            if ((messageType.equalsIgnoreCase(TJCommand.CMD_BILL_PAYMENT) || messageType.equalsIgnoreCase(TJCommand.CMD_NETWORK_BILL_PAYMENT))) {
                  totalDestAcc = command.getParam(Fields.TOTAL_DEST_ACCOUNT);
                  if (totalDestAcc != null && !totalDestAcc.equals(""))
                  msg.setAttribute(Fields.TOTAL_DEST_ACCOUNT, totalDestAcc);
            }

            try {
                if (accountGroup != null && CFSFacadeNew.isGroupCard(accountGroup) && (totalDestAcc != null) && !"".equals(totalDestAcc) && totalDestAcc.startsWith(Constants.BANKE_TEJARAT_BIN_NEW))
                    getGroupCardTxCode(txCode,msg,command);
            } catch (Exception e) {
                log.error(e);
                throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
            }

         }
  }

    private void getGroupCardTxCode(String messageType,CMMessage msg,CMCommand command){

        if (messageType.equalsIgnoreCase(TJCommand.CMD_TRANSFER_FUND) || messageType.equalsIgnoreCase(TJCommand.CB_FUND_TRANSFER_CMD)) {
            msg.setAttribute(Fields.MESSAGE_TYPE, TJCommand.CMD_TRANSFER_FUND_GROUP_CARD);
            msg.setAttribute(Fields.COMMAND, TJCommand.CMD_TRANSFER_FUND_GROUP_CARD);
            command.setCommandName(TJCommand.CMD_TRANSFER_FUND_GROUP_CARD);
        }
        if (messageType.equalsIgnoreCase(TJCommand.CMD_TRANSFER_FUND_REVERSAL)) {
            msg.setAttribute(Fields.MESSAGE_TYPE, TJCommand.CMD_TRANSFER_FUND_GROUP_CARD_REVERSAL);
            msg.setAttribute(Fields.COMMAND, TJCommand.CMD_TRANSFER_FUND_GROUP_CARD_REVERSAL);
            command.setCommandName(TJCommand.CMD_TRANSFER_FUND_GROUP_CARD_REVERSAL);
        }
        if (!messageType.equals("") && (messageType.startsWith(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ) || messageType.startsWith(TJCommand.CMD_FUND_TRANSFER_TEJ2TEJ))){
            if(messageType.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_PG) || messageType.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_REVERSAL_PG)){
                messageType = messageType.substring(1);
                messageType = messageType.replace("T2T", "GRP");
                msg.setAttribute(Fields.MESSAGE_TYPE, messageType);
                msg.setAttribute(Fields.COMMAND, messageType);
                command.setCommandName(messageType);
            }else{
                messageType = messageType.replace("T2T", "GRP");
                msg.setAttribute(Fields.MESSAGE_TYPE, messageType);
                msg.setAttribute(Fields.COMMAND, messageType);
                command.setCommandName(messageType);
            }
        }

    }

    private String doLimitStringParameter(String parameter, int limit) {
        String limitedParameter = "";
        if (null == parameter) return limitedParameter;
        if (parameter.length() > limit)
            limitedParameter = parameter.substring(0, limit);
        else
            limitedParameter = parameter;
        return limitedParameter;
    }
}
