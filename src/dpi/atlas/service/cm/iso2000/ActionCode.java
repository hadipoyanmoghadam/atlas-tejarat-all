package dpi.atlas.service.cm.iso2000;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @version $Revision: 1.16 $ $Date: 2007/11/01 08:26:28 $
 */

public class ActionCode {
    public static final String REQUEST_ACTION_CODE = "0001";
    public static final String APPROVED = "0000";
    public static final String APPROVED_FOR_PARTIAL_AMOUNT = "0002";
    public static final String TOO_MANY_RECORDS_REQUESTED = "0800";
    public static final String DETAIL_NOT_AVAILABLE = "0801";
    public static final String PASSWORD_CHANGE_REQUIRED = "0802";
    public static final String HISTORY_NOT_AVAILABLE_FOR_FULL_DATE_RANGE = "0803";
    public static final String ACCOUNT_BALANCE_INFORMATION_NOT_AVAILABLE = "0804";
    public static final String EXTENDED_ACCOUNT_BALANCE_INFORMATION_NOT_AVAILABLE = "0805";
    public static final String STOP_CHEQUE_IN_PROCESS = "0806";
    public static final String AUTHORIZATION_HELD_OVER = "0807";
    public static final String ALLOWABLE_PIN_TRIES_EXCEEDED = "1006";
    public static final String INVALID_AMOUNT = "1010";
    public static final String INVALID_CARD_NUMBER = "1011";
    public static final String PIN_DATA_REQUIRED = "1012";
    public static final String INVALID_ROW = "1013";
    public static final String NO_ACCOUNT_OF_TYPE_REQUESTED = "1014";
    public static final String REQUESTED_FUNCTION_NOT_SUPPORTED = "1015";
    public static final String NOT_SUFFICIENT_FUNDS = "1016";
    public static final String INCORRECT_PIN = "1017";
    public static final String ACCOUNT_ENABLED_BEFORE = "0018";
    public static final String ACCOUNT_DISABLED_BEFORE = "1019";
    public static final String TRANSACTION_NOT_PERMITTED_TO_TERMINAL = "1020";
    public static final String EXCEEEDS_WITHDRAWAL_AMOUNT_LIMIT = "1021";
    public static final String SECURITY_VIOLATION = "1022";
    public static final String EXCEEEDS_WITHDRAWAL_FREQUENCY_LIMIT = "1023";
    public static final String NOT_AUTHORIZED_AND_FEES_DISPUTED = "1031";
    public static final String LOST_STOLEN_CARD = "1032";
    public static final String CLOSED_ACCOUNT = "1035";
    public static final String ACCOUNT_BAD_STATUS = "1040";
    public static final String FROM_ACCOUNT_BAD_STATUS = "1041";
    public static final String TO_ACCOUNT_BAD_STATUS = "1042";
    //cheque action codes
    public static final String ALREADY_PASSED = "1043";
    public static final String ALREADY_REGISTERED = "1044";
    public static final String ALREADY_REVOKE = "1045";
    //==================
    public static final String PIN_CHANGE_REQUIRED = "1047";
    public static final String INVALID_NEW_PIN = "1048";
    public static final String BANK_NOT_FOUND = "1049";
    public static final String CUSTOMER_VENDOR_NOT_FOUND = "1051";
    public static final String CUSTOMER_VENDOR_ACCOUNT_INVALID = "1053";
    public static final String PAYMENT_DATE_INVALID = "1057";
    public static final String PERSONAL_IDENTIFICATION_NOT_FOUND = "1058";
    public static final String SCHEDULED_TRANSACTIONS_EXIST = "1059";
    public static final String TRANSACTION_NOT_SUPPORTED_BY_CARD_ISSUER = "1061";
    public static final String INVALID_OPERATION = "1062";
    public static final String ACCOUNT_ALREADY_ACTIVE = "1063";
    public static final String ACCOUNT_ALREADY_BLOCKED = "1064";
    public static final String ACCOUNT_ALREADY_E_BLOCKED = "1065";
    public static final String ACCOUNT_ALREADY_E_UNBLOCKED = "1066";
    public static final String ACCOUNT_IS_E_BLOCKED = "1067";
    public static final String ACCOUNT_ALREADY_EBANKING_E_BLOCKED = "1068";
    public static final String ACCOUNT_IS_EBANKING_E_BLOCKED = "1069";
    public static final String ACCOUNT_ALREADY_EXISTS = "1078";           // Nasim
    public static final String ACCOUNT_HAS_BEEN_ONLINED_BEFORE = "1079";  // Nasim
    public static final String CARD_HAS_BEEN_DEFINED_BEFORE = "1077";  // CMS
    public static final String CARD_HAS_BEEN_REVOKED_BEFORE = "1075";  // CMS
    public static final String ACCOUNT_IS_OFFLINE = "1076";  // CMS
    public static final String POLICY_HAS_BEEN_DEFINED_BEFORE = "1088";  // CMS
    public static final String INVALID_START_DATE_OF_GROUPCARD_POLICY = "1091";  // CMS
    public static final String INVALID_COUNT_OF_GROUPCARD_POLICY = "1092";  // CMS
    public static final String INVALID_INTERVAL_OF_GROUPCARD_POLICY = "1083";  // CMS
    public static final String GROUPCARD_POLICY_NOT_FOUND = "1089";  // CMS
    public static final String CHARGE_RECORDS_NOT_FOUND = "1090";  // CMS
    public static final String PARENT_HAS_ACTIVE_CHILD_CARD = "1095";  // CMS
    public static final String GIFT_CARD_LIMIT_EXCEEDED = "1825";

    public static final String GENERAL_ERROR = "1800";
    public static final String GENERAL_DATA_ERROR = "1801";
    public static final String SYSTEM_NOT_AVAILABLE = "1802";
    public static final String FUNCTION_NOT_AVAILABLE = "1803";
    public static final String UNSUPPORTED_SERVICE = "1804";
    public static final String UNSUPPORTED_MESSAGE = "1805";
    public static final String DUPLICATE = "1806";
    public static final String REQUIRED_ELEMENT_NOT_INCLUDED = "1807";
    public static final String UNKNOWN_OBJECT_ID = "1808";
    public static final String SERVICE_NOT_ENABLED = "1809";
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
    public static final String BRANCH_NOT_FOUND = "1832";
    public static final String SATANA_CONN_ERROR = "1833";
    public static final String INVALID_SRC_HOST = "1834";
    public static final String INVALID_DST_HOST = "1856";
    public static final String FARAGIR_DUPLICATE = "1835";
    public static final String INVALID_BANK_CODE = "1836";
    public static final String CREDIT_BATCH_AMOUNT_ERROR = "1900";
    public static final String DEBIT_BATCH_AMOUNT_ERROR = "1901";
    public static final String DEVICE_NOT_FOUND = "1902";
    public static final String ACCOUNT_RANGE_NOT_FOUND = "1903";
    public static final String REGION_NOT_FOUND = "1904";
    public static final String ACTIVE_BATCH_NOT_FOUND = "1905";
    public static final String ACCOUNT_NOT_ASSIGNED_TO_CARD = "1906";
    public static final String ORGANIZATION_BRANCH_NOT_FOUND = "1907";
    public static final String SERVER_SOURCE_NOT_FOUND = "1908";
    public static final String TRANSACTION_NOT_SUPPORTED_BY_SERVER_SOURCE = "1909";
    public static final String TRANSACTION_NOT_SUPPORTED_BY_SERVER = "1910";
    public static final String SERVICE_NOT_ASSIGNED_TO_CUSTOMER = "1911";
    public static final String SERVICE_NOT_ASSIGNED_TO_CUSTOMER_TEMPLATE = "1912";
    public static final String INVLAID_COUNTRY_CODE = "1913";
    public static final String INVALID_STYLE = "1914";
    public static final String ORGANIZATION_NOT_FOUND = "1915";
    public static final String CHECK_DIGIT_ERROR = "1916";
    public static final String FUND_TRANSFER_HAVE_BEEN_DONE = "1917";
    public static final String INVALID_OLD_PIN = "1918";
    public static final String DESTINATION_BRANCH_NOT_FOUND = "1919";
    public static final String EXCEEDS_MAX_TRANSFER_AMOUNT = "1920";
    public static final String LOCKED_ACCOUNT = "1921";
    public static final String TRANSACTION_NOT_FOUND = "1922";
    //1385/12/26 Boroon Added for completing the reversal of txns scheme - START
    public static final String ALREADY_REVERSED = "1923";
    //1385/12/26 Boroon Added for completing the reversal of txns scheme - END
    public static final String REVERSE_HAS_NO_ORIGINAL_LORO = "1924";
    public static final String REVERSE_HAS_NO_ORIGINAL = "1925";
    public static final String CUTOVER_BATCH_ALREADY_EXISTS = "1926";
    public static final String INVALID_BAR_CODE_BILL_PAYMENT = "1927";
    public static final String NO_ACCOUNT_FOR_BAR_CODE_BILL_PAYMENT = "1928";
    public static final String PAYMENT_INVALID_DIGITS = "1929";
    public static final String BILL_PAYMENT_INVALID_LENGTH = "1930";
    public static final String BILL_PAYMENT_BEFORE_PAID = "1931";
    public static final String ACCOUNT_HAS_NO_CUST_INFO = "1932";
    public static final String BILL_PAYMENT_INITIALIZATION = "1933";
    public static final String TEMPLATE_NOT_FOUND = "1934";
    public static final String DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT = "1935";
    public static final String INVALID_PARAMETERS = "1936"; //--- for date to date, time to time & amount to amount in fara stm
    public static final String PAYMENT_CODE_IS_INVALID = "1937"; //--- for date to date, time to time & amount to amount in fara stm
    public static final String REVOKE_HAS_NO_ORIGINAL = "1938";
    public static final String INVALID_NUMBER_OF_PAYMENT_CODE = "1939";
    public static final String CUTOVER_DONE_BEFORE = "1837";
    public static final String ACCOUNT_IS_IN_SGB_ONLINE_BRANCH = "1838";
    public static final String INPUT_OUTPUT_ERROR = "1839";
    public static final String SENDER_NOT_OPENED = "1840";
    public static final String CHANNEL_NOT_EXIST = "1841";
    public static final String FUND_TRANSFER_HAVE_NOT_BEEN_DONE = "1842";
    public static final String ACCOUNT_NOT_FOUND = "1843";
    public static final String LOCKED_HOST = "1844";
    public static final String CLOSED_ACH = "1845";
    public static final String PAN_NOT_FOUND = "1850";
    public static final String FAR_FT_HAS_NO_ORIG = "1860";
    public static final String MORE_THAN_ONE_CFS_ACCOUNT_ASSIGNED_TO_CARD = "1851";
    public static final String MORE_THAN_ONE_FAR_ACCOUNT_ASSIGNED_TO_CARD = "1852";
    public static final String SPECIAL_CONDITIONS = "2007";
    public static final String UNABLE_TO_LOCATE_RECORD_ON_FILE = "3002";
    public static final String DUPLICAT_RECORD_OLD_RECORD_REJECTED = "3003";
    public static final String CHANNEL_CAN_NOT_CREATE_RESPONSE = "3006";
    public static final String FORMAT_ERROR = "3007";
    public static final String SHEBA_FORMAT_ERROR = "3009";
    public static final String PAYMENT_CODE_FORMAT_ERROR = "3010";
    public static final String CHECK_DIGIT_ALGORITHM_IS_NEEDED = "3011";
    public static final String TRACK_CODE_FORMAT_ERROR = "3012";
    public static final String DUPLICATE_NEW_RECORD_REJECTED = "3008";
    public static final String NULL_VALUE_FOR_NOT_NULL_FIELD = "3800";
    public static final String MESSAGE_CAN_NOT_BE_REVERSED = "4800";
    // Ebanking action ceodes
    public static final String SRC_TRANSACTION_NOT_FOUND = "5001";
    public static final String PENDING = "5002";
    public static final String FLW_HAS_NO_ORIGINAL = "5003";
    public static final String EBANKING_GENERAL_ERROR = "5004";
    public static final String INVALID_TRANSACTION = "9012";
    public static final String RE_INTER_TRANSACTION = "9013";
    public static final String SOCKET_CLOSED = "9108";
    public static final String SYSTEM_MALFUNCTION = "9109";
    public static final String SECURITY_SOFTWARE_HARDWARE_ERROR_TRY_AGAIN = "9120";
    public static final String DATABASE_ERROR = "9125";
    public static final String TIME_OUT = "9126";

    public static final String SPARROW_IS_NOT_READY = "9999";

    public static final String TRANSACTION_PENDING = "1000";
    public static final String TRANSACTION_OFFLINE = "2000";
    public static final String TRANSACTION_IN_PROGRESS = "3000";
    public static final String TRANSACTION_FAILED_IN_SGB = "4000";
    public static final String LOCKED_CHANNEL = "6001";
    public static final String INVALID_MESSAGE = "6002";
    public static final String INVALID_VERSION = "6003";

    public static final String UNABLE_TO_GET_ACCOUNT_LIST_FROM_FARAGIR = "0810";

    public static final String THREADPOOL_ACTIVES_NOT_FOUND = "6004";

    public static final String EMPTY_LIST = "7001";

    //Stock Account Action code
    //1016 is also use for stock fund transfer
    public static final String NOT_REPRESENTATIVE_ACCOUNT = "7011";
    public static final String STOCK_ACC_ALREAY_BLOCK = "7012"; //share or represntative
    public static final String REPRESENTATIVE_ACC_ALREAY_UNBLOCK = "7013";
    public static final String NO_REALTED_BLCK_EXIST = "7014";
    public static final String FT_HAS_NO_REALTED_BLCK = "7015";
    public static final String DUPLICATE_BLCK_NO = "7016";
    public static final String REQUESTED_AMNT_MORE_THAN_BLCK_AMNT = "7017";
    public static final String NOT_VALID_SHARE_STOCK_ACC_TO_BROKER = "7018"; //not in tbbource
    public static final String NOT_ACTIVE_SHARE_STOCK_ACC = "7019";
    public static final String INVALID_BROKER_NO = "7020";
    public static final String REQ_AMNT_EXCEED_BLCK_AMNT = "7021";
    public static final String ALREADY_ACTIVE_ABDC = "7023";
    public static final String ALREADY_DEACTIVE_ABDC = "7024";
    public static final String FT_ABDC_DEACTIVE = "7025";//no withdraw from abdicative accounts
    public static final String ABDC_ACC_NOT_ALLOWED_OTHER_FT = "7026";
    public static final String ABDC_ACC_NOT_ALLOWED_TO_E_BLOCK = "7027";
    public static final String NOT_ENEX_ACC = "7031";
    public static final String NO_BLCK_UNBLCK_FOR_THIS_ACCOUNT = "7040";

    public static final String FUND_TRANSFER_MUST_BE_REVERSED = "8000";

    public static final String SMS_NOTIFICATION_FORMAT_ERROR = "3005";
    public static final String MELLICODE_NOT_FOUND = "1940";
    public static final String MELLICODE_INVALID = "1941";

    public static final String SERVICE_NOT_SUPPORTED = "1950";
    //=====paya
    //FT
    public static final String PAYA_APPROVED = "0000000";
    public static final String PAYA_REQUEST = "0000001";
    //File FT
    public static final String INVALID_ACH_FILENAME = "1846";
    public static final String ACH_FILENAME_NOT_EXIST = "1847";
    public static final String ACH_FILE_ALREADY_REG = "1848";
    public static final String ACH_FILE_IS_NOT_VALID = "1849";
    //=====end of paya

    public static final String IVALID_RTGS_PAYA_FT_SRC_HOST = "";

    public static final String AMOUNT_DEPENDED_PAY_CODE = "1960";

    public static final String CMS_ACTION_CODE_START = "0050";
    public static final String CMS_ACTION_CODE_END = "0059";
    public static final String RESPONSE_NOT_FOUND = "1080";
    public static final String CMS_CANCELLATION_ALREADY_EXISTS= "1085";
    public static final String CMS_REQUEST_NUMBER_NOT_FOUND= "1086";
    public static final String DOCUMENT_NOT_FOUND= "1087";
// Iran Insurance
    public static final String INVALID_INSTALLMENT_ID = "2805";
    public static final String INSTALLMENT_GENERAL_ERROR = "2800";
    public static final String INVALID_INSTALLMENT_AMOUNT = "2806";
    public static final String INSTALLMENT_PAID_BEFORE = "2807";
    public static final String INSTALLMENT_TIMEOUT = "2826";
    public static final String CONTROL_CODE_NOT_FOUND = "2808";
    public static final String PAYMENT_CODE_IS_INVALID_BY_PATTERN = "1944";
    public static final String CMS_REQUEST_NUMBER_HAS_BEEN_ASSIGNED_TO_DOCUMENT_BEFORE= "1084";

    //moshtarak
    public static final String ZERO_ROW_NOT_FOUND = "1074";  // Nasim
    public static final String ROW_NOT_FOUND = "1081";  // Nasim
    public static final String ROW_ASSIGNED_TO_CARD  = "1082";  // Nasim
    public static final String ACCOUNT_ASSIGNED_TO_CARD  = "1093";  // Nasim
    public static final String ACCOUNT_TYPE_HAS_CHANGED_BEFORE  = "1094";  // Nasim

    // AMX
    public static final String ACCOUNT_HAS_BALANCE = "5100";  // Nasim
    public static final String ACCOUNT_HAS_BLOCKED_AMOUNT = "5101";  // Nasim
    public static final String DEST_TRANSACTION_NOT_FOUND = "5005";

    //StockDeposit
    public static final String WAGE_INFORMATION_NOT_FOUND = "5102";  // Nasim

    //PAYA
    public static final String PAYA_IS_INACTIVE = "5550";  // Nasim
    public static final String PAYA_REQUEST_NOT_FOUND= "5551";
    public static final String CAN_NOT_GENERATE_PAYA_REQUEST= "5552";
    public static final String PAYA_REQUEST_IS_DUPLICATE= "8030";
    public static final String CAN_NOT_SEND_TO_PAYA= "2220";

    //Wage
    public static final String WAGE_INFORMATION_IS_NOT_DEFINED= "5553";
    //ATM
    public static final String DEVICE_IS_ONLINE_WITH_ANOTHER_ACCOUNT = "5103";
    public static final String DEVICE_ACCOUNTNO_IS_DIFFERENT = "5104";
    public static final String ATM_INVALID_OPERATION = "5105";
    public static final String DEVICE_IS_ACTIVE = "5106";
    public static final String ATM_IS_ACTIVE_WITH_ANOTHER_ACCOUNT = "5107";
    public static final String DEVICE_TYPE_IS_DIFFERENT = "5108";
    public static final String ACCOUNT_HAS_TRANSACTION = "5109";
    public static final String ACCOUNT_DOSENT_EXIST_IN_FIN = "5110";
    public static final String DEVICE_HAS_BEEN_ONLINED_BEFORE = "5112";
    public static final String DEVICE_BRANCH_CODE_IS_DIFFERENT = "5113";

    //Simin Financial Block UnBlock
    public static final String INVALID_BLOCK_ROW = "1024";


}
