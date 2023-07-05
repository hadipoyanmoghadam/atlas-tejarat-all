package dpi.atlas.service.cm.iso2000;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @version $Revision: 1.4 $ $Date: 2007/10/29 14:04:26 $
 */


public class ActionCodeMsg {
    public static final String APPROVED = "APPROVED";
    public static final String APPROVED_CODE = "0000";
    public static final String TOO_MANY_RECORDS_REQUESTED = "0800";
    public static final String DETAIL_NOT_AVAILABLE = "0801";
    public static final String PASSWORD_CHANGE_REQUIRED = "0802";
    public static final String HISTORY_NOT_AVAILABLE_FOR_FULL_DATE_RANGE = "0803";
    public static final String ACCOUNT_BALANCE_INFORMATION_NOT_AVAILABLE = "0804";
    public static final String EXTENDED_ACCOUNT_BALANCE_INFORMATION_NOT_AVAILABLE = "0805";
    public static final String STOP_CHEQUE_IN_PROCESS = "0806";
    public static final String AUTHORIZATION_HELD_OVER = "0807";

    public static final String ALLOWABLE_PIN_TRIES_EXCEEDED = "1006";
    public static final String INVALID_CARD_NUMBER = "1011";
    public static final String NO_ACCOUNT_OF_TYPE_REQUESTED = "1014";
    public static final String REQUESTED_FUNCTION_NOT_SUPPORTED = "1015";
    public static final String NOT_SUFFICIENT_FUNDS = "1016";
    public static final String NOT_AUTHORIZED_AND_FEES_DISPUTED = "1031";
    public static final String LOST_STOLEN_CARD = "1032";
    public static final String CLOSED_ACCOUNT = "1035";
    public static final String FROM_ACCOUNT_BAD_STATUS = "1041";
    public static final String TO_ACCOUNT_BAD_STATUS = "1042";
    public static final String CHEQUE_ALREADY_POSTED = "1043";
    public static final String PIN_CHANGE_REQUIRED = "1047";
    public static final String INVALID_NEW_PIN = "1048";
    public static final String BANK_NOT_FOUND = "1049";
    public static final String CUSTOMER_VENDOR_NOT_FOUND = "1051";
    public static final String CUSTOMER_VENDOR_ACCOUNT_INVALID = "1053";
    public static final String TRANSACTION_NOT_SUPPORTED_BY_CARD_ISSUER = "1061";
    public static final String GENERAL_DATA_ERROR = "General data base error";
    public static final String GENERAL_ERROR = "General error";
    public static final String SYSTEM_NOT_AVAILABLE = "1802";
    public static final String FUNCTION_NOT_AVAILABLE = "1803";
    public static final String UNSUPPORTED_SERVICE = "1804";
    public static final String UNSUPPORTED_MESSAGE = "1805";
    public static final String DUPLICATE = "1806";
    public static final String REQUIRED_ELEMENT_NOT_INCLUDED = "1807";
    public static final String UNKNOWN_OBJECT_ID = "1808";
    public static final String SERVICE_eNOT_ENABLED = "1809";
    public static final String AMOUNT_TOO_SMALL = "1810";
    public static final String AMOUNT_TOO_LARGE = "1811";
    public static final String INVALID_DATE_TIME_RANGE = "1812";
    public static final String SOURCE_ACCOUNT_INVALID = "1813";
    public static final String DESTINATION_ACCOUNT_INVALID = "1814";
    public static final String SOURCE_AND_DESTINATION_ACCOUNTS_ARE_IDENTICAL = "1815";
    public static final String INVALID_CHEQUE_NUMBER_RANGE = "1816";
    public static final String CHEQUE_NUMBER_NOT_FOUND = "1817";
    public static final String TOO_MANY_CHEQUES_TO_PROCESS = "1818";
    public static final String CHEQUE_BOOK_STYLE_NOT_AVAILABLE = "1819";
    public static final String INVALID_NUMBER_OF_CHEQUE = "1820";
    public static final String INVALID_CURERNCY_CODE = "1821";
    public static final String INVALID_LANGUAGE_CODE = "1822";
    public static final String UNSUPPORTED_FREQUENCY = "1823";
    public static final String DUPLICATE_PAYMENT_TRANSFER_EXISTS = "1824";
    public static final String CASH_DEPOSIT_LIMIT_EXCEEDED = "1825";
    public static final String OBJECT_TYPE_NOT_SUPPORTED = "1826";
    public static final String FORMAT_NOT_SUPPORTED = "1827";
    public static final String CUSTOMER_NOT_FOUND = "1828";
    public static final String CUSTOMER_NOT_RELATED_TO_ACCOUNT = "1829";
    public static final String STOP_PAYMENT_MATCH = "1830";
    public static final String NO_ADDRESS_DATA = "1831";
    public static final String DESTIANTION_BRANCH_NOT_FOUND = "1832";
    public static final String CREDIT_BATCH_AMOUNT_ERROR = "1900";
    public static final String DEBIT_BATCH_AMOUNT_ERROR = "1901";
    public static final String DEVICE_NOT_FOUND = "1902";
    public static final String ORGANIZATION_BRANCH_NOT_FOUND = "1903";

    public static final String SPECIAL_CONDITIONS = "2007";

    public static final String UNABLE_TO_LOCATE_RECORD_ON_FILE = "3002";
    public static final String DUPLICATE_NEW_RECORD_REJECTED = "3008";

    public static final String MESSAGE_CAN_NOT_BE_REVERSED = "4800";
    public static final String INCORRECT_PIN = "1017"; //TODO: check

    public static final String SPARROW_IS_NOT_READY = "The sparrow is not ready";

    public static final String PENDING = "Pending";
    public static final String FLW_HAS_NO_ORIGINAL = "Follow up request has no original";
    public static final String EBANKING_GENERAL_ERROR = "Ebanking general error";
    public static final String SRC_TRANSACTION_NOT_FOUND = "Source transaction not found";
    public static final String TRANSACTION_OFFLINE = "offline";
    public static final String MSG_DUPLICATE = "Message is duplicate";
    public static final String ACCOUNT_HAS_NO_CUST_INFO = "No customer information is assigned to requested account";
    public static final String CONNECTION_TIMEOUT = "Connection timeout";
    public static final String INVALID_CHEQUE_NO = "Invalid cheque number";

    //CFD
    public static final String SUCCESSFULL_MESSAGE = "Successfull.";
    public static final String NO_CHANGE_MESSAGE = "Not Changed.";
    public static final String SUCCESSFULL_WITH_ACCOUNT_EXISTANCE_MESSAGE = "Successfull + Warning ACCOUNT_INSERT_BUT_UPDATED: Account has already existed in DataBase.";
    public static final String ERROR_ACCOUNT_NOT_FOUNT_MESSAGE = "Error: Account not Found.";
    public static final String ERROR_IN_GIFT_CARD_AMOUNT_MESSAGE = "Error: Amount of Gift Card is Incorrect.";
    public static final String ERROR_CARD_NOT_FOUND_MESSAGE = "Error: Card Not Found.";
    public static final String ERROR_CARD_NOT_ASSIGNED_TO_ACCOUNT_MESSAGE = "Error:Card Not Assigned To Account.";
    public static final String SUCCESSFULL_WITH_CARD_EXISTANCE_MESSAGE = "Successfull + Warning CARD_INSERT_BUT_UPDATED: Card has already existed in DataBase.";
    public static final String ERROR_IN_GIFT_CARD_INSERT_BUT_IGNORED_MESSAGE = "ERROR: GIFT CARD_INSERT_BUT_EXIST_AND_IGNORED: Card has already existed in DataBase and message is ignored.";
    public static final String ERROR_IN_GIFT_CARD_UPDATE_BUT_IGNORED_MESSAGE = "ERROR: GIFT CARD_UPDATE_BUT_EXIST_AND_IGNORED: Card has already existed in DataBase and message is ignored.";
    public static final String ERROR_CARD_EXIST_AND_IGNORED = "Successfull + Warning GIFT CARD_INSERT_BUT_EXIST_AND_IGNORED: Card has already existed in DataBase and message is ignored.";
    public static final String ERROR_ACCOUNT_NOT_FOUNT_THEN_CARD_IGNORED_MESSAGE = "Error: CARD_PROCCESS_IGNORED_BECAUSE_ACCOUNT_NOT_FOUND.";
    public static final String ERROR_SQL_EXCEPTION_MESSAGE = "ERROR_IN_CUSTOMER_PROCESS: Database has an unkown Error. Please refer to Administrator.";
    public static final String ERROR_REQUESTED_FUNCTION_NOT_SUPPORTED = "Error: Record Flag is invalid.";
    public static final String ERROR_ACCOUNT_DIGIT_INVALID = "Error: Account Digit is invalid.";
    public static final String PAYA_TIME_OUT = "ACH timeout";
    public static final String ERROR_IN_GROUP_CARD_ROW_MESSAGE = "Error: Series of Group Card is Incorrect.";
    public static final String ERROR_IN_CHILD_CARD_PROCESS = "Erro:Child card proccess is ignored because master account not found.";
    //group paya


    //group paya
    public static final String REQ_ACH_FILENAME_NOT_EXIST = "There is not any file name with requested file name ";
    public static final String INVALID_ACH_FILENAME = "Invalid ACH file name";

    //Stock Deposit
    public static final String FUND_TRANSFER_MUST_BE_REVERSED = "Fund transfer must be reversed";
    public static final String TRANSACTION_PENDING = "Transaction pending";
    public static final String WAGE_INFORMATION_NOT_FOUND = "Wage information not found";
    public static final String DEST_TRANSACTION_NOT_FOUND = "Destination transaction not found";


}

