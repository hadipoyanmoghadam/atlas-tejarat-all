package dpi.atlas.service.cfs.handler.fault;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class FinancialFaultHandler extends CFSFaultHandler {
    private static Log log = LogFactory.getLog(FinancialFaultHandler.class);

    public FinancialFaultHandler() {
        super();
    }                                                  

    public String getActionCode(Object obj, Map holder) throws CFSFault {
        if (log.isDebugEnabled()) log.debug("In Financial Fault Handler");
        CMMessage msg = ((CMMessage) obj);
        String faultCode = (String) msg.getAttribute(CMMessage.FAULT_CODE);
        if (log.isDebugEnabled()) log.debug("faultCode : " + faultCode);
        String actionCode = ActionCode.GENERAL_ERROR;
        if (faultCode.equals(CFSFault.FLT_FROM_ACC_BAD_STATUS))
            actionCode = ActionCode.FROM_ACCOUNT_BAD_STATUS;
        else if (faultCode.equals(CFSFault.FLT_EXCEEDS_MAX_TRANSFER_AMOUNT))
            actionCode = ActionCode.EXCEEDS_MAX_TRANSFER_AMOUNT;
        else if (faultCode.equals(CFSFault.FLT_TO_ACC_BAD_STATUS))
            actionCode = ActionCode.TO_ACCOUNT_BAD_STATUS;
        else if (faultCode.equals(CFSFault.FLT_INVALID_OPERATION))
            actionCode = ActionCode.INVALID_OPERATION;
        else if (faultCode.equals(CFSFault.ACC_BAD_STATUS))
            actionCode = ActionCode.ACCOUNT_BAD_STATUS;
        else if (faultCode.equals(CFSFault.FLT_CLOSED_ACCOUNT))
            actionCode = ActionCode.CLOSED_ACCOUNT;
        else if (faultCode.equals(CFSFault.FLT_INVALID_CARD_NUMBER))
            actionCode = ActionCode.INVALID_CARD_NUMBER;
        else if (faultCode.equals(CFSFault.FLT_NOT_SUFFICIENT_FUNDS))
            actionCode = ActionCode.NOT_SUFFICIENT_FUNDS;
        else if (faultCode.equals(CFSFault.FLT_ENABLED_BEFORE))
            actionCode = ActionCode.ACCOUNT_ENABLED_BEFORE;
        else if (faultCode.equals(CFSFault.FLT_DISABLED_BEFORE))
            actionCode = ActionCode.ACCOUNT_DISABLED_BEFORE;
        else if (faultCode.equals(CFSFault.FLT_CUSTOMER_VENDOR_NOT_FOUND))
            actionCode = ActionCode.CUSTOMER_VENDOR_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_CUSTOMER_VENDOR_ACCOUNT_INVALID))
            actionCode = ActionCode.CUSTOMER_VENDOR_ACCOUNT_INVALID;
        else if (faultCode.equals(CFSFault.FLT_BANK_NOT_FOUND))
            actionCode = ActionCode.BANK_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED))
            actionCode = ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED;
        else if (faultCode.equals(CFSFault.FLT_CARD_HAS_BEEN_DEFINED_BEFORE))
            actionCode = ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE;
        else if (faultCode.equals(CFSFault.FLT_DESTINATION_ACCOUNT_INVALID))
            actionCode = ActionCode.DESTINATION_ACCOUNT_INVALID;
        else if (faultCode.equals(CFSFault.FLT_CREDIT_BATCH_AMOUNT_ERROR))
            actionCode = ActionCode.CREDIT_BATCH_AMOUNT_ERROR;
        else if (faultCode.equals(CFSFault.FLT_DEBIT_BATCH_AMOUNT_ERROR))
            actionCode = ActionCode.DEBIT_BATCH_AMOUNT_ERROR;
        else if (faultCode.equals(CFSFault.FLT_DEVICE_NOT_FOUND))
            actionCode = ActionCode.DEVICE_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_CUSTOMER_NOT_FOUND))
            actionCode = ActionCode.CUSTOMER_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_DUPLICATE_NEW_RECORD_REJECTED))
            actionCode = ActionCode.DUPLICATE_NEW_RECORD_REJECTED;
        else if (faultCode.equals(CFSFault.FLT_DESTINATION_BRANCH_NOT_FOUND))
            actionCode = ActionCode.DESTINATION_BRANCH_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_UNABLE_TO_LOCATE_RECORD_ON_FILE))
            actionCode = ActionCode.UNABLE_TO_LOCATE_RECORD_ON_FILE;
        else if (faultCode.equals(CFSFault.FLT_NULL_VALUE_FOR_NOT_NULL_FIELD))
            actionCode = ActionCode.NULL_VALUE_FOR_NOT_NULL_FIELD;
        else if (faultCode.equals(CFSFault.FLT_ACCOUNT_NOT_ASSIGNED_TO_CARD))
            actionCode = ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD;
        else if (faultCode.equals(CFSFault.FLT_LOCKED_ACCOUNT))
            actionCode = ActionCode.ACCOUNT_BALANCE_INFORMATION_NOT_AVAILABLE;
        else if (faultCode.equals(CFSFault.FLT_TRANSACTION_NOT_FOUND))
            actionCode = ActionCode.TRANSACTION_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_ACCOUNT_RANGE_NOT_FOUND))
            actionCode = ActionCode.ACCOUNT_RANGE_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_UNSUPPORTED_MESSAGE))
            actionCode = ActionCode.UNSUPPORTED_MESSAGE;
        else if (faultCode.equals(CFSFault.FLT_ALREADY_REVERSED))
            actionCode = ActionCode.ALREADY_REVERSED;
        else if (faultCode.equals(CFSFault.REVERSE_HAS_NO_ORIGINAL))
            actionCode = ActionCode.REVERSE_HAS_NO_ORIGINAL;
        else if (faultCode.equals(CFSFault.FLT_SGB_BATCH_NOT_AVAILABLE))
            actionCode = ActionCode.ACTIVE_BATCH_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_FORMAT_NOT_SUPPORTED))
            actionCode = ActionCode.FORMAT_NOT_SUPPORTED;
        else if (faultCode.equals(CFSFault.FLT_LOCKED_HOST))
            actionCode = ActionCode.LOCKED_HOST;
        else if (faultCode.equals(CFSFault.FLT_CUTOVER_BATCH_ALREADY_EXISTS))
            actionCode = ActionCode.CUTOVER_BATCH_ALREADY_EXISTS;
        else if (faultCode.equals(CFSFault.FLT_BILL_PAYMENT_NO_ACCOUNT_FOR_BARCODE))
            actionCode = ActionCode.NO_ACCOUNT_FOR_BAR_CODE_BILL_PAYMENT;
        else if (faultCode.equals(CFSFault.FLT_BILL_PAYMENT_INVALID_DIGITS))
            actionCode = ActionCode.PAYMENT_INVALID_DIGITS;
        else if (faultCode.equals(CFSFault.FLT_BILL_PAYMENT_BEFORE_PAID))
            actionCode = ActionCode.BILL_PAYMENT_BEFORE_PAID;
        else if (faultCode.equals(CFSFault.FLT_DUPLICATE))
            actionCode = ActionCode.DUPLICATE;
        else if (faultCode.equals(CFSFault.ACCOUNT_ALREADY_BLOCKED))
          actionCode  = ActionCode.ACCOUNT_ALREADY_BLOCKED;
        else if (faultCode.equals(CFSFault.ACCOUNT_ALREADY_ACTIVE))
          actionCode  = ActionCode.ACCOUNT_ALREADY_ACTIVE;
        else if (faultCode.equals(CFSFault.BLCK_HAS_NO_SUFFICIENT_FUNDS ))
          actionCode  = ActionCode.NOT_SUFFICIENT_FUNDS;
        else if (faultCode.equals(CFSFault.CLOSED_ACCOUNT ))
          actionCode  = ActionCode.CLOSED_ACCOUNT;
        else if (faultCode.equals(CFSFault.NOT_ACTIVE_SHARE_STOCK_ACC ))
          actionCode  = ActionCode.NOT_ACTIVE_SHARE_STOCK_ACC;
        else if (faultCode.equals(CFSFault.NOT_VALID_SHARE_STOCK_ACC_TO_BROKER ))
          actionCode  = ActionCode.NOT_VALID_SHARE_STOCK_ACC_TO_BROKER;
        else if (faultCode.equals(CFSFault.DUPLICATE_BLCK_NO ))
          actionCode  = ActionCode.DUPLICATE_BLCK_NO;
        else if (faultCode.equals(CFSFault.REQ_AMNT_EXCEED_BLCK_AMNT))
          actionCode  = ActionCode.REQ_AMNT_EXCEED_BLCK_AMNT;
        else if (faultCode.equals(CFSFault.NO_ACCOUNT_OF_TYPE_REQUESTED))
          actionCode  = ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED;
        else if (faultCode.equals(CFSFault.NO_REALTED_BLCK_EXIST))
          actionCode  = ActionCode.NO_REALTED_BLCK_EXIST;
        else if (faultCode.equals(CFSFault.EMPTY_LIST))
          actionCode  = ActionCode.EMPTY_LIST;
        else if (faultCode.equals(CFSFault.FLT_ACTIVE_BATCH_NOT_FOUND))
            actionCode = ActionCode.ACTIVE_BATCH_NOT_FOUND;
        else if (faultCode.equals(CFSFault.ACCOUNT_ALREADY_E_BLOCKED))
            actionCode = ActionCode.ACCOUNT_ALREADY_E_BLOCKED;
        else if (faultCode.equals(CFSFault.E_BLOCKED_ACCOUNT))
            actionCode = ActionCode.ACCOUNT_IS_E_BLOCKED;
        else if (faultCode.equals(CFSFault.ACCOUNT_ALREADY_E_UNBLOCKED))
            actionCode = ActionCode.ACCOUNT_ALREADY_E_UNBLOCKED;
        else if (faultCode.equals(CFSFault.FLT_GENERAL_DATABASE_ERROR))
            actionCode = ActionCode.DATABASE_ERROR;
        else if (faultCode.equals(CFSFault.FLT_BRANCH_NOT_FOUND))
            actionCode = ActionCode.BRANCH_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_GENERAL_DATA_ERROR))
            actionCode = ActionCode.GENERAL_DATA_ERROR;
        else if (faultCode.equals(CFSFault.FLT_SYSTEM_MALFUNCTION))
            actionCode = ActionCode.SYSTEM_MALFUNCTION;
        else if (faultCode.equals(CFSFault.FLT_INVALID_AMOUNT))
            actionCode = ActionCode.INVALID_AMOUNT;
        else if (faultCode.equals(CFSFault.FLT_TRANSACTION_NOT_PERMITTED_TO_TERMINAL))
            actionCode = ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL;
        else if (faultCode.equals(CFSFault.FLT_ACCOUNT_HAS_BEEN_ONLINED_BEFORE))
            actionCode = ActionCode.ACCOUNT_HAS_BEEN_ONLINED_BEFORE;
        else if (faultCode.equals(CFSFault.FLT_CANCELLATION_ALREADY_EXISTS))
            actionCode = ActionCode.CMS_CANCELLATION_ALREADY_EXISTS;
        else if (faultCode.equals(CFSFault.FLT_REQUEST_NUMBER_NOT_FOUND))
            actionCode = ActionCode.CMS_REQUEST_NUMBER_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_DOCUMENT_NOT_FOUND))
            actionCode = ActionCode.DOCUMENT_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_REQUEST_NUMBER_HAS_BEEN_ASSIGNED_TO_DOCUMENT_BEFORE))
            actionCode = ActionCode.CMS_REQUEST_NUMBER_HAS_BEEN_ASSIGNED_TO_DOCUMENT_BEFORE;
        else if (faultCode.equals(CFSFault.FLT_ROW_ASSIGNED_TO_CARD))
            actionCode = ActionCode.ROW_ASSIGNED_TO_CARD;
        else if (faultCode.equals(CFSFault.FLT_CHARGE_RECORDS_NOT_FOUND))
            actionCode = ActionCode.CHARGE_RECORDS_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_ACCOUNT_IS_E_BLOCKED))
            actionCode = ActionCode.ACCOUNT_IS_E_BLOCKED;
        else if (faultCode.equals(CFSFault.FLT_ROW_NOT_FOUND))
            actionCode = ActionCode.ROW_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_ACCOUNT_HAS_BALANCE))
            actionCode = ActionCode.ACCOUNT_HAS_BALANCE;
        else if (faultCode.equals(CFSFault.FLT_ACCOUNT_HAS_BLOCKED_AMOUNT))
            actionCode = ActionCode.ACCOUNT_HAS_BLOCKED_AMOUNT;
        else if (faultCode.equals(CFSFault.FAULT_AUTH_GENERAL_INVALID_ACCOUNT))
            actionCode = ActionCode.FUND_TRANSFER_HAVE_NOT_BEEN_DONE;
        else if (faultCode.equals(CFSFault.FLT_ACCOUNT_IS_DEPOSIT_BLOCK))
            actionCode = ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT;
        else if (faultCode.equals(CFSFault.FLT_PAYA_IS_INACTIVE))
            actionCode = ActionCode.PAYA_IS_INACTIVE;
        else if (faultCode.equals(CFSFault.FLT_PAYA_REQUEST_NOT_FOUND))
            actionCode = ActionCode.PAYA_REQUEST_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_CAN_NOT_GENERATE_PAYA_REQUEST))
            actionCode = ActionCode.CAN_NOT_GENERATE_PAYA_REQUEST;
        else if (faultCode.equals(CFSFault.FLT_CAN_NOT_SEND_TO_PAYA))
            actionCode = ActionCode.CAN_NOT_SEND_TO_PAYA;
        else if (faultCode.equals(CFSFault.FLT_WAGE_INFORMATION_IS_NOT_DEFINED))
            actionCode = ActionCode.WAGE_INFORMATION_IS_NOT_DEFINED;
        return actionCode;

    }
}
