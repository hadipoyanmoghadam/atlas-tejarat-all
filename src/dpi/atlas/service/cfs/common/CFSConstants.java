// Decompiled by DJ v3.2.2.67 Copyright 2002 Atanas Neshkov  Date: 1/16/2008 5:40:18 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   CFSConstants.java

package dpi.atlas.service.cfs.common;


public class CFSConstants {


    public CFSConstants() {
    }

    public static final long ACC_STATUS_INACTIVE = 0L;
    public static final long ACC_STATUS_ACTIVE = 1L;
    public static final long ACC_STATUS_TEMP = 2L;
    public static final String ACCOUNT_SRC_TYPE_CM = "0";
    public static final String ACTION_CODE = "action-code";
    public static final String RESULT = "result";
    public static final String ACCOUNT_DESCRIPTION = "account-description";
    public static final String AMOUNT_FIELD = "amount-field";
    public static final String PARAMETER_ID = "parameter-id";
    public static final String PARAMETER_ID_VALUE = "parameter-id-value";
    public static final String FEE_FIELD = "fee-field";
    public static final String OPERATION = "operation";
    public static final String DEBIT = "debit";
    public static final String CREDIT = "credit";
    public static final String STATEMENT = "statement";
    public static final String HANDLER_DESCRIPTION = "description";
    public static final String FROM_ACCOUNT = "from-account";
    public static final String TO_ACCOUNT = "to-account";
    public static final String CUSTOMER_FEE_FROM_ACCOUNT = "customer-fee-from-account";
    public static final String CUSTOMER_FEE_TO_ACCOUNT = "customer-fee-to-account";
    public static final String IMD_FEE_FROM_ACCOUNT = "imd-fee-from-account";
    public static final String IMD_FEE_TO_ACCOUNT = "imd-fee-to-account";
    public static final String SOURCE_IMD_DEBIT_ACCOUNT = "Source Imd Debit Account";
    public static final String DESTINATION_IMD_DEBIT_ACCOUNT = "Destination Imd Debit Account";
    public static final String SOURCE_IMD_CREDIT_ACCOUNT = "Source Imd Credit Account";
    public static final String DESTINATION_IMD_CREDIT_ACCOUNT = "Destination Imd Credit Account";
    public static final String IMD_TYPE = "imd-type";
    public static final String DAILY_WITHDRAWAL_LIMIT = "daily-withdrawal-limit";

    public static final String TRANSACTION_TYPE = "transaction-type";
    public static final String CUSTOMER_FEE = "customer-fee";
    public static final String IMD_FEE = "imd-fee";
    public static final String DEBIT_OR_CREDIT = "debit-or-credit";
    public static final String ONLINE_OR_OFFLINE = "online-or-offline";
    public static final String ONLINE = "online";
    public static final String OFFLINE = "offline";
    public static final String STATUS = "status";
    public static final String DIRECTION = "direction";
    public static final String IN_COMING = "incoming";
    public static final String OUT_GOING = "outgoing";

    public static final int ACTIVE_BATCH = 0;
    public static final int CUT_OVER_BATCH = 1;
    public static final int CLOSED_BATCH = 2;
     public static final int ACTIVE_BATCH_UNBLOCK = 3;
    public static final int CUT_OVER_BATCH_UNBLOCK = 4;
    public static final int CLOSED_BATCH_UNBLOCK = 5;
    public static final String CURRENT_BATCH = "current batch";

    public static final String ACTIVE = "Active";
    public static final String ORIGINAL_TRANSACTION = "Original Transaction";
    public static final int BACTH_TYPE_CR2_TRANS = 1;
    public static final int BACTH_TYPE_SGB_TRANS = 2;
    public static final int BACTH_TYPE_CR2_BALANCE = 3;
    public static final int BACTH_TYPE_SGB_BALANCE = 4;
    public static final int BACTH_TYPE_ACC_RANGE = 5;
    public static final int BACTH_TYPE_ACC_SPEC = 6;
    public static final String FP_AUTHORIZE_REQUEST = "0";
    public static final String FP_FORCE_POST = "1";
    public static final String FP_SUSPECT_REVERSAL = "2";
    public static final String FP_POSIBLE_DUPLICATE = "3";
    public static final String FP_POS_POSTING_MESSAGE = "5";
    public static final String CUSTOMER = "Customer";
    public static final String CARD = "Card";
    public static final String TRUE = "1";
    public static final String FALSE = "0";
    public static final String FAIL_CONDITION = "fail-condition";
    public static final String ASSIGNED = "assigned";
    public static final String UNASSIGNED = "unassigned";
    public static final String CARD_ACCOUNT = "card-account";
    public static final String CFS_FACADE_SPRING_BEAN_NAME = "cfsFacade";
    public static final String CHANNEL_FACADE_SPRING_BEAN_NAME = "channelFacade";
    public static final String CFS_TXN_FACADE_SPRING_BEAN_NAME = "cfsTxnFacade";
    public static final String RESULT_TYPE = "result-type";
    public static final Object TX_CLASS = "Tx";
    public static final int MAX_MINISTATEMENT_SIZE = 150;
    public static final int MIN_MINISTATEMENT_SIZE = 3;
    public static final Object TX_LOG = "tx-log";
    public static final Object DUPLICATE_MESSAGE = "duplicate-message";
    public static final Object NO_ACTIVE_BATCH = "no_active_batch";
    public static final String USE_DEFAULT_VALUES = "use-default-values";
    public static final Object ACCOUNT_CATEGORY_CUSTOMER = "customer-category";
    public static final Object ACCOUNT_CATEGORY_DEVICE = "device-category";
    public static final Object ACCOUNT_CATEGORY_IMD = "imd-category";
    public static final Object ACCOUNT_CATEGORY_IMD_DEVICE = "imd-device-category";
    public static final Object UNKNOWN_CATEGORY = "unknown-category";
    public static final String FROM_ACCOUNT_CATEGORY = "from-account-category";
    public static final String TO_ACCOUNT_CATEGORY = "to-account-category";
    public static final String FILE_PATH = "file-path";
    public static final String GIFT_CARD_APPLIER_OPERATION_CODE = "giftCardOperationCode";
    public static final String DELIVERY_BLOCK_APPLIER_OPERATION_CODE = "deliveryAndBlockOperationCode";
    public static final String UNBLOCK_APPLIER_OPERATION_CODE = "unBlockOperationCode";
    public static final String APPLIER_COMMIT_SIZE = "commitsize";
    public static final String OPERATION_COMMAND = "OperationCommand";
    public static final String TXN_SRC_SGB = "SGB";
    public static final String TXN_SRC_PRG = "PRG";
    public static final String CFS_CONSTANT_STRING = "cfs-str";
    public static final char SGB_DEBIT = 48;
    public static final char SGB_CREDIT = 49;
    public static final String SGB_DEBIT_STR = "DT";
    public static final String SGB_CREDIT_STR = "CR";
    public static final String CURRENT_CURRENCY = "current-currency";
    public static final String CURRENT_ISSUER = "current-issuer";
    public static final String CURRENT_ISSUER_SGB = "current-issuer-sgb";
    public static final String SGB_LOG_PROCESS_STATUS_NOT_PROCESSED = "0";
    public static final String SGB_LOG_PROCESS_STATUS_PROCESSED = "1";
    public static final String IMD_ACCOUNT_NO = "imd-account-no";
    public static final String ACCOUNT_FIELD = "account-field";
    public static final String FROM_ACCOUNT_FIELD = "from-account-field";
    public static final String TO_ACCOUNT_FIELD = "to-account-field";
    public static final String IMD_ACCOUNT_FIELD = "imd-account";
    public static final long IRANIAN_CURRENCY = 1L;
    public static final int NOT_CUTOVERED = 0;
    public static final int NOT_REVERSED = 0;
    public static final int REVERSED = 1;
    public static final String ADMIN_ENABLE = "admin-source";
    public static final int REVERSED_BATCH = 3;
    public static final String OFFSET = "offset";
    public static final String CFS_PORT = "8000";
    public static final int PARTITION_SIZE = 92;
    public static final int CFSTX_PARTITION_SIZE = 92;
    public static final String IMD = "IMDObject";
    public static final String ISSUER_BRANCHE = "Issuer_Branche";
    public static final String DBCONNECTION = "dbConnection";


    public static final int BLPY_ACTIVE_BATCH = 0;
    public static final int BLPY_CUT_OVER_BATCH = 1;
    public static final String BLPY_CURRENT_BATCH = "blpy_current_batch";

    public static final String NFT_TRANSACTION_TYPE = "NFT";
    public static final String NFT_TRANSACTION_TYPE_PG = "NFTPG";
    public static final String NFT_TRANSACTION_TYPE_MP = "NFTMP";
    public static final String NFT_TRANSACTION_TYPE_POS = "NFTPOS";
    public static final String NFT_TRANSACTION_TYPE_KIOSK = "NFTKS";
    public static final String TRANSACTION_TYPE_MP = "MP";
    public static final String TRANSACTION_TYPE_PG = "PG";
    public static final String TRANSACTION_TYPE_POS = "PS";
    public static final String TRANSACTION_TYPE_KIOSK = "KS";
    public static final String TRANSACTION_TYPE_POS_BRANCH = "PB";
    public static final String NBAL_TRANSACTION_TYPE = "NBAL";
    public static final String POSNBAL_BRANCH_TRANSACTION_TYPE = "SPBSBAL";
    public static final String POS_BRANCH_SHETAB_CASH_REQUEST = "SPBSCSHR";
    public static final String NFT_DAILY_WITHDRAWAL_LIMIT = "dailyWithdrawalLimit";
    public static final String REVERSE_ORIG_DATE = "REVERSE_ORIG_DATE";
    public static final String NFT_PREFIX = "NFT";
    public static final String BRANCH_ACQUIRE = "585983";
    public static final String ACCOUNT_TYPE_ID = "ACCOUNT_TYPE_ID";

    public static final String GIFT_CARD_007 = "007";
    public static final long IGNORE_MAX_TRANS_LIMIT = -1000000000000000000L;
    public static final String SOROSH_BIN = "soroshBin";
    public static final String SOROSH_PAN = "soroshPAN";

    // ERROR Message
    //TODO, ModelException must become finer in such a way that can be distinguishable, say ModelInsuffientException,ModelWithdrawLimitException and so on.  
    public static final String BLPY_BEFORE_PAID_ERROR = "blpyBeforePaid";
    public static final String BLPY_NO_RECORD_FOR_BILLID_PAYMENTID_ERROR = "blpyNoRecordForBillidPaymentidError";
    public static final String NFT_WITHDRAWAL_LIMIT_EXCEED_ERROR = "nftWithdrawalLimitExceed";

    public static final String SAVE = "save";
    public static final String COMMIT = "commit";
    public static final long COMMIT_SIZE = 1000;

    public static final String SMS_SRC_RESULT = "srcResult";
    public static final String SMS_DEST_RESULT = "destResult";

    public static final String INST_LOG_PROCESS_STATUS_NOT_PROCESSED = "0";
    public static final String INST_LOG_PROCESS_STATUS_PROCESSED = "1";



    //Group card
    public static final String GROUP_CARD_PARENT = "1";
    public static final int GROUP_CARD_PARENT_ROW = 1;
    public static final String GROUP_CARD_CHILD = "2";
    public static final String SOROSH_CHILD = "3";
    public static final String NORMAL_CARD="0";
    public static final String GROUP_CARD="GRPCD";
    public static final String DISCHARGE="DCHARGE";
    public static final String CHARGE="CHARGE";
    public static final short BLOCK=0;
    public static final short UNBLOCK=1;
    public static final String CHARGE_LOG_PROCESS_STATUS_NOT_PROCESSED = "0";
    public static final String CHARGE_LOG_PROCESS_STATUS_DISCHARGE_PROCESSED = "1";
    public static final String CHARGE_LOG_PROCESS_STATUS_COMPLETE_PROCESSED = "2";
    public static final String CHILD_CARD = "childCard";
    public static final String CHILD_CARD_NO = "childCardNo";
    public static final String OUTPUT_PATH = "outPut-path";
    public static final String SMS_OUTPUT_PATH = "sms-outPut-path";
    public static final String SMS_OUTPUT_PATH_BAK = "sms-outPut-path-backup";

    public static final String SHAPARAK_APPLIER_OPERATION_CODE = "shaparakOperationCode";

    //Tourist Card
    public static final String TOURIST_SERVICE = "TOURIST";
    public static final String CHARGE_TRANSACTION = "D";
    public static final String DCHARGE_TRANSACTION = "W";

    //Remittance
    public static final String NOTIFICATION_STR = "notificationStr";

    //Applier
    public static final String APPLIER_SESSION_ID = "ApplierSessionId";

    //Stock Deposit
    public static final String SMS_RESULT = "smsResult";

    //PAYA
    public static final String PAYA_IS_ACTIVE = "0";
    public static final String PAYA_IS_INACTIVE = "1";
    public static final String ACTIVE_PAYA_REQUEST = "1";

}
