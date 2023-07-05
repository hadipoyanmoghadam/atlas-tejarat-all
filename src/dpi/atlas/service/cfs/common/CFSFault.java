package dpi.atlas.service.cfs.common;


import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;

public class CFSFault extends CMFault {
    //1385/12/26 Boroon Added for completing the reversal of txns scheme - START
    public static final String FLT_ALREADY_REVERSED = "fault.external.cfs.financial.AlreadyReversed";
    public static final String REVERSE_HAS_NO_ORIGINAL = "fault.external.cfs.financial.ReverseHasNoOriginal";

    //1385/12/26 Boroon Added for completing the reversal of txns scheme - END
    //1386/07/19 BAYAT Added for checking unsupported reversal tag value- START
    public static final String FLT_FORMAT_NOT_SUPPORTED = "fault.external.cfs.financial.FormatNotSupported";
    //1386/07/19 BAYAT Added for checking unsupported reversal tag value- END
    // 1385/12/21 Boroon Changed to fix bug in setting SGB_BRANCH_ID - START
    public static final String FLT_ACCOUNT_RANGE_NOT_FOUND = "fault.external.cfs.financial.AccountRangeNotFound";
    // 1385/12/21 Boroon Changed to fix bug in setting SGB_BRANCH_ID - END
    //        1385/12/22 Boroon Added to put SGB Tx Code in TBCFSTx - START
    public static final String FLT_UNSUPPORTED_MESSAGE = "fault.external.cfs.financial.UnsupportedMessage";
    //        1385/12/22 Boroon Added to put SGB Tx Code in TBCFSTx - END
    public static final String FLT_FROM_ACC_BAD_STATUS = "fault.external.cfs.financial.FromAccBadStatus";
    public static final String FLT_INVALID_OPERATION = "fault.external.cfs.financial.InvalidOperation";
    public static final String FLT_TO_ACC_BAD_STATUS = "fault.external.cfs.financial.ToAccBadStatus";
    public static final String ACC_BAD_STATUS = "fault.external.cfs.financial.AccBadStatus";
    public static final String FLT_FEE_ACC_BAD_STATUS = "fault.external.cfs.financial.FeeAccBadStatus";
    //    public static final String FLT_SPECIAL_CONDITIONS = "fault.external.cfs.other.SpecialConditions";
    public static final String FLT_LOCKED_ACCOUNT = "fault.external.cfs.financial.LockedAccount";
    public static final String FLT_CLOSED_ACCOUNT = "fault.external.cfs.financial.ClosedAccount";
    public static final String FLT_NOT_SUFFICIENT_FUNDS = "fault.external.cfs.financial.NotSufficientFunds";
    public static final String FLT_ENABLED_BEFORE = "fault.external.cfs.financial.EnabledBefore";
    public static final String FLT_DISABLED_BEFORE = "fault.external.cfs.financial.DisabledBefore";
    public static final String FLT_BILL_PAYMENT_INVALID_BAR_CODE = "fault.external.cfs.financial.InvalidBarCodeBillPayment";
    public static final String FLT_BILL_PAYMENT_NO_ACCOUNT_FOR_BARCODE = "fault.external.cfs.financial.NoAccountForBarCodeBillPayment";
    public static final String FLT_BILL_PAYMENT_INVALID_DIGITS = "fault.external.cfs.financial.BillPaymentInvalidDigits";
    public static final String FLT_BILL_PAYMENT_BEFORE_PAID = "fault.external.cfs.financial.BillPaymentBeforePaid";
    public static final String FLT_BILL_PAYMENT_INVALID_LENGTH = "fault.external.cfs.financial.BillPaymentInvalidLength";
    public static final String FLT_INVALID_CARD_NUMBER = "fault.external.cfs.financial.InvalidCardNumber";
    public static final String FLT_CUSTOMER_VENDOR_NOT_FOUND = "fault.external.cfs.financial.CustomerVendorNotfound";
    public static final String FLT_CUSTOMER_VENDOR_ACCOUNT_INVALID = "fault.external.cfs.financial.CustomerVendorAccountInvalid";
    public static final String FLT_BANK_NOT_FOUND = "fault.external.cfs.financial.BankNotFound";
    public static final String FLT_NO_ACCOUNT_OF_TYPE_REQUESTED = "fault.external.cfs.financial.NoAccountOfTypeRequested";
    public static final String FLT_CARD_HAS_BEEN_DEFINED_BEFORE = "fault.external.cfs.financial.CardHasBeenDefinedBefore";
    public static final String FLT_DESTINATION_ACCOUNT_INVALID = "fault.external.cfs.financial.DestinationAccountInvalid";
    public static final String FLT_CREDIT_BATCH_AMOUNT_ERROR = "fault.external.cfs.financial.CreditBatchAmountError";
    public static final String FLT_DEBIT_BATCH_AMOUNT_ERROR = "fault.external.cfs.financial.DebitBatchAmountError";
    public static final String FLT_DEVICE_NOT_FOUND = "fault.external.cfs.financial.DeviceNotFoundError";
    public static final String FLT_BRANCH_NOT_FOUND = "fault.external.cfs.financial.BranchNotFoundError";
    public static final String FLT_CUSTOMER_NOT_FOUND = "fault.external.cfs.financial.CustomerNotFoundError";
    public static final String FLT_DUPLICATE_NEW_RECORD_REJECTED = "fault.external.cfs.financial.DuplicateNewRecordRejectedError";
    public static final String FLT_DESTINATION_BRANCH_NOT_FOUND = "fault.external.cfs.financial.DestinationBranchNotFoundError";
    public static final String FLT_UNABLE_TO_LOCATE_RECORD_ON_FILE = "fault.external.cfs.financial.UnableToLocateRecordOnFile";
    public static final String FLT_NULL_VALUE_FOR_NOT_NULL_FIELD = "fault.external.cfs.financial.NullValueForNotNullField";
    public static final String FLT_ACCOUNT_NOT_ASSIGNED_TO_CARD = "fault.external.cfs.financial.AccountNotAssignedToCard";
    public static final String FLT_TRANSACTION_NOT_FOUND = "fault.external.cfs.financial.TransactionNotFound";
    public static final String FLT_CUTOVER_BATCH_ALREADY_EXISTS = "fault.external.cfs.financial.CutOverBatchAlreadyExists";

    //    public static final String FLT_OBJECT_TYPE_NOT_SUPPORTED = "fault.external.cfs.other.ObjectTypeNotSupported";
    public static final String FLT_DUPLICATE = "fault.external.cfs.other.Duplicate";

    public static final String FLT_GENERAL_DATA_ERROR_RETRY = "fault.internal.cfs.retryfault.GeneralDataError";

    public static final String FLT_EXCEEDS_MAX_TRANSFER_AMOUNT = "fault.external.cfs.financial.exceed.transfer.amount";
    //1386/04/26 Boroon added for running CutOver,SGBFileReader & SGBFileApplier fby user request - START
    public static final String FLT_SGB_BATCH_NOT_AVAILABLE = "fault.external.cfs.financial.SGBBatchNotAvailable";
//1386/04/26 Boroon added for running CutOver,SGBFileReader & SGBFileApplier fby user request - END    

    public static final String FLT_GENERAL_DATA_ERROR = "fault.internal.cfs.GeneralDataError";
    public static final String FLT_GENERAL_DATABASE_ERROR = "fault.internal.cfs.GeneralDataBaseError";
    public static final String FLT_GENERAL_ERROR = "fault.internal.cfs.GeneralError";
    public static final String FLT_ACTIVE_BATCH_NOT_FOUND = "fault.internal.cfs.ActiveBatchNotFound";
    public static final String FLT_CUTOVER_DONE_BEFORE = "fault.internal.cfs.CutOverDoneBefore";
    public static final String FLT_SYSTEM_MALFUNCTION = "fault.internal.cfs.SystemMalfunction";

    public static final String FLT_FROM_ACC_BAD_STATUS_LOG = "fault.external.cfs.logfault.FromAccBadStatus";
    public static final String FLT_TO_ACC_BAD_STATUS_LOG = "fault.external.cfs.logfault.ToAccBadStatus";
    public static final String FLT_ACC_BAD_STATUS_LOG = "fault.external.cfs.logfault.AccBadStatus";
    public static final String FLT_CLOSED_ACCOUNT_LOG = "fault.external.cfs.logfault.ClosedAccount";
    public static final String FLT_NOT_SUFFICIENT_FUNDS_LOG = "fault.external.cfs.logfault.NotSufficientFunds";
    public static final String FLT_NO_ACCOUNT_OF_TYPE_REQUESTED_LOG = "fault.external.cfs.logfault.NoAccountOfTypeRequested";
    public static final String FLT_DESTINATION_ACCOUNT_INVALID_LOG = "fault.external.cfs.logfault.DestinationAccountInvalid";
    public static final String FLT_CUSTOMER_NOT_FOUND_LOG = "fault.external.cfs.logfault.CustomerNotFoundError";
    public static final String FLT_DUPLICATE_NEW_RECORD_REJECTED_LOG = "fault.external.cfs.logfault.DuplicateNewRecordRejectedError";
    public static final String FLT_DESTINATION_BRANCH_NOT_FOUND_LOG = "fault.external.cfs.logfault.DestinationBranchNotFoundError";
    public static final String FLT_NULL_VALUE_FOR_NOT_NULL_FIELD_LOG = "fault.external.cfs.logfault.NullValueForNotNullField";
    public static final String FLT_ACCOUNT_RANGE_NOT_FOUND_LOG = "fault.external.cfs.logfault.AccountRangeNotFound";
    public static final String FLT_DUPLICATE_LOG = "fault.external.cfs.logfault.Duplicate";
    public static final String FLT_REGION_NOT_FOUND_LOG = "fault.external.cfs.logfault.RegionNotFound";
    public static final String FLT_DEVICE_NOT_FOUND_LOG = "fault.external.cfs.logfault.DeviceNotFound";
    public static final String FLT_GENERAL_DATA_ERROR_LOG = "fault.external.cfs.logfault.GeneralDataError";
    public static final String FLT_ACCOUNT_NOT_ASSIGNED_TO_CARD_LOG = "fault.external.cfs.logfault.AccountNotAssignedToCard";
    public static final String FLT_LOCKED_HOST = "fault.external.cfs.financial.HostIsLocked";
    public static final String ACCOUNT_ALREADY_BLOCKED = "fault.external.cfs.other.acc.already.block";
    public static final String ACCOUNT_ALREADY_ACTIVE = "fault.external.cfs.other.acc.already.unblock";
    public static final String CLOSED_ACCOUNT = "fault.external.cfs.other.acc.closed";
    public static final String NOT_ACTIVE_SHARE_STOCK_ACC = "fault.external.cfs.other.not.ative.share.stock.acc";
    public static final String NOT_VALID_SHARE_STOCK_ACC_TO_BROKER = "fault.external.cfs.other.share.stock.acc.not.assign.broker"; 
    public static final String BLCK_HAS_NO_SUFFICIENT_FUNDS = "fault.external.cfs.other.blck.acc.no.sufficent.fund";
    public static final String DUPLICATE_BLCK_NO = "fault.external.cfs.other.duplicate.blck.no";
    public static final String NO_REALTED_BLCK_EXIST = "fault.external.cfs.other.not.related.block.exist";
    public static final String REQ_AMNT_EXCEED_BLCK_AMNT = "fault.external.cfs.other.amnt.exceed.blck.amnt";
    public static final String NO_ACCOUNT_OF_TYPE_REQUESTED = "fault.external.cfs.other.no.acc.type.requested.exist";
    public static final String EMPTY_LIST = "fault.external.cfs.other.empty.list";
    public static final String ACCOUNT_ALREADY_E_BLOCKED = "fault.external.cfs.acc.already.eblock";
    public static final String ACCOUNT_ALREADY_E_UNBLOCKED = "fault.external.cfs.acc.already.e.unblock";
    public static final String E_BLOCKED_ACCOUNT = "fault.external.cfs.acc.eblock";

    public static final String FLT_SMS_FORMAT = "fault.external.cfs.SMSNotification";
    public static final String FLT_PAYMENT_INQUIRY_FORMAT = "fault.internal.paymentInquiry";

    public static final String FLT_INVALID_AMOUNT = "fault.external.cfs.financial.invalidAmount";
    public static final String FLT_TRANSACTION_NOT_PERMITTED_TO_TERMINAL = "fault.external.cfs.financial.transaction.not.permitted.to.terminal";
    public static final String FLT_ACCOUNT_HAS_BEEN_ONLINED_BEFORE = "fault.external.cfs.financial.account.has.been.onlined.before";

    public static final String FLT_CANCELLATION_ALREADY_EXISTS = "fault.external.cfs.cms.cancellation.transaction.already.exist";
    public static final String FLT_REQUEST_NUMBER_NOT_FOUND = "fault.external.cfs.cms.requestnumber.notfound";
    public static final String FLT_DOCUMENT_NOT_FOUND = "fault.external.cfs.branch.document.notfound";
    public static final String FLT_REQUEST_NUMBER_HAS_BEEN_ASSIGNED_TO_DOCUMENT_BEFORE = "fault.external.cfs.cms.requestnumber.assigned.before";
    public static final String FLT_ROW_ASSIGNED_TO_CARD = "fault.external.cfs.branch.row.assignd.to.card";
    public static final String FLT_ACCOUNT_IS_E_BLOCKED = "fault.external.cfs.branch.account.is.eBlocked";
    public static final String FLT_CHARGE_RECORDS_NOT_FOUND = "fault.external.cfs.cms.chargeRecord.notfound";
    public static final String FLT_ACCOUNT_HAS_BALANCE = "fault.external.cfs.account.has.balance";
    public static final String FLT_ACCOUNT_HAS_BLOCKED_AMOUNT = "fault.external.cfs.account.has.blocked.amount";
    public static final String FLT_ROW_NOT_FOUND = "fault.external.cfs.row.not.found";
    public static final String FLT_ACCOUNT_IS_DEPOSIT_BLOCK = "fault.external.cfs.acount.is.deposit.block";

    public static final String FLT_REMITTANCE_FORMAT = "fault.external.cfs.remittanceNotification";

    public static final String FLT_PAYA_IS_INACTIVE = "fault.external.cfs.financial.payaIsInactive";
    public static final String FLT_PAYA_REQUEST_NOT_FOUND = "fault.external.cfs.financial.payaRequestNotFound";
    public static final String FLT_CAN_NOT_GENERATE_PAYA_REQUEST = "fault.external.cfs.financial.canNotGeneratePayaRequest";
    public static final String FLT_CAN_NOT_SEND_TO_PAYA = "fault.internal.cfs.canNotSendToPaya";

    public static final String FLT_WAGE_INFORMATION_IS_NOT_DEFINED = "fault.internal.cfs.wageInfoIsNotDefined";

    public CFSFault(String s, String s1) {
        super(s, s1);
    }

    public CFSFault(String s) {
        super(s);
    }

    public CFSFault(String s, Exception e) {
        super(s, e);
        try {
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public CFSFault(Exception e) {
        super(e);
    }
}
