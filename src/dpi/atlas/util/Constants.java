package dpi.atlas.util;
                                                //todo 970928

public class Constants {
    public static final String CUSTOMER = "customer";
    public static final String CUSTOMER_ACCOUNT = "customer-account";
    public static final String CUSTOMER_SERVICE = "customer-service";
    public static final String REQUEST = "1";
    public static final String RESPONSE = "2";
    public static final String DEVICE_ID = "deviceId";

    public static final String RESULT = "result";
    public static final String WAIT_TIME = "wait-time";
    public static final String DEFAULT_SAF_WAIT_TIME = "2";

    public static final int ISO_CUSTOMER_DEFAULT_TEMPLATEID = 1;

    public static final String ACCOUNT_DATA = "account-data";

    public static final String HOST_ID_CFS = "1";
    public static final String HOST_ID_SGB = "2";
    public static final String HOST_ID_FARAGIR = "3";
    public static final String HOST_ID_UNKNOWN = "9";
    public static final String HOST_ID_MANZOOMEH = "6";

    public static final String AUTHORISED = "0";
    public static final String NOTAUTHORISED = "1";

    public static final String SRC_HOST_ID = "hostId";
    public static final String DEST_HOST_ID = "desthostId";
    public static final String REAL_DEST_HOST_ID = "realDestHostId";
    public static final String DEST_ACCOUNT_DATA = "dest-account-data";
    public static final String DebitCredit = "debitCredit";
    public static final String AvailableDebitCredit = "AvailableDebitCredit";

    public static final String AVAILABLE_AMOUNT = "available-amount";
    public static final String ACTUAL_AMOUNT = "actual-amount";
    public static final String CONNECTION_TEST = "PING_MESSAGE";

    //CRDB Sign Values
    public static final String CREDIT = "C";
    public static final String DEBIT = "D";
    public static final String CREDITDEBIT = "CD";

    //ft sgb-sgb
    public static final String NOT_REVERSED = "0";
    public static final String REVERSED = "1";
    public static final String NOT_CUTOVERED = "0";

    // Core constants declaration
    public static final String CFS_HOSTID = "1";
    public static final String SGB_HOSTID = "2";
    public static final String FAR_HOSTID = "3";

    public static final int ACTIVE_BATCH = 0;
    public static final String CURRENT_BATCH = "current batch";

    public static final int BACTH_TYPE_CR2_TRANS = 1;

    public class TransType {
        public static final int BAL = 0;
        public static final int FT = 2;
    }

    public static final String TRUE = "1";
    public static final String FALSE = "0";
    public static final String RESULT_SIZE = "resultSize";

    public static final String FLW_CORE_SEQ = "FLW_CORE_SEQ";
    public static final String DEST_CORE = "DEST_CORE";

    public static final String FT = "FT";

    public static final String OPERATOR_AND = " and ";
    public static final String OPERATOR_OR = " or ";

    //--XMLDataProcedure:ParseData --
    public static final String REQUEST_ACTION_CODE = "0001";

    //--GetCurrencyRate --
    public static final String CURRENCY_CODE = "CURRENCYCODE";
    public static final String CURRENCY_UNIT = "CURRENCYUNIT";
    public static final String CURRENCY_DESC = "CURRENCYDESC";
    public static final String SELL_RATE = "SELLRATE";
    public static final String BUY_RATE = "BUYRATE";
    public static final String DAY_DATE = "DAYDATE";
    public static final String DAY_TIME = "DAYTIME";
    public static final String CURRENCY_RATE_SHORT_NAME = "CRRT";
    public static final String CHEQUE_STATUS_SHORT_NAME = "CQST";

    //--XMLEbankMessageCQST --
    public static final String FAULT = "00";

    //--GetChequeStatus --
    public static final String ACCOUNT_NO = "ACCOUNTNO";
    public static final String ACTION_CODE = "ACTIONCODE";

    public static final String GENERAL_DATA_ERROR = "General data error";

    //WithdrawLimitControl
    public static final String INVALID_AMOUNT_REQ = "Requested amount is more than remaind permitted amount";

    public static final String INPUT_PARAMS = "inputParams";
    public static final String SRC_ACCOUNT = "srcAccount";
    public static final String DST_ACCOUNT = "dstAccount";
    public static final String REF_NO = "refNo";
    public static final String AMOUNT = "amount";
    public static final String DATE = "date";
    public static final String TIME = "time";
    public static final String BRANCH_CODE = "branchCode";
    public static final String BATCH_PK = "batchPK";
    public static final String BILL_ID = "billId";
    public static final String PAYMENT_ID = "paymentId";
    public static final String MSG_SEQ = "messageSeq";
    public static final String OP_CODE = "opCode";
    public static final String DOC_DESC = "desc";
    public static final String PAN = "pan";

    //Genarl Input params for BAL, STM & etc
    public static final String SESSION_ID = "sessionId";
    public static final String ACC_NO = "accNo";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String TRANS_COUNT = "transCount";
    public static final String BRANCH_ID = "brchId";
    public static final String DOC_SERIAL = "docSerial";
    public static final String SERVICE = "service";
    public static final String CLIENT_ID = "clientId";
    public static final String CUSTOMER_ID = "customerId";
    public static final String CUSTOMER_PASS = "customerPass";
    public static final String SIGN = "sign";
    public static final String ACC_TYPE = "accType";
    public static final String MIN_AMOUNT = "MinAmount";
    public static final String MAX_AMOUNT = "MaxAmount";
    public static final String CB_PAY_ID = "cbPayId";

    //getStatement class
    public static final String COMMAND = "COMMAND";
    public static final String CR_DB = "CRDB";
    public static final String CREDIT_DEBIT = "Credit/Debit";
    public static final String DESC = "DESC";
    public static final String BRANCH_NO = "BRANCHNO";
    public static final String BALANCE = "BALANCE";
    public static final String TRANS_DATE = "TRANSDATE";
    public static final String TRANS_TIME = "transTime";
    public static final String DEBIT_CREDIT = "DEBITCREDIT";

    public static final String ISREVERSED = "isReversed";
    public static final String FTREVERSED = "FTReversed";
    //
    public static final String HOST = "hostId";
    public static final String TERMINAL_ID = "terminalId";
    public static final String COMMAND_NAME = "commandName";

    public static final String FAILE = "2";
    public static final String SPARROW_ID = "2";

    public static final String IS_LOCAL_CURRENCY = "IsLocalCurrency";

    //command names
    public static final String FUND_TRANSFER_CMD = "FT";
    public static final String FUND_TRANSFER_REVERS_CMD = "FTR";
    public static final String SPW_FUND_TRANSFER = "ST";
    public static final String SPW_FUND_TRANSFER_REVERS = "STR";

    //getStatement && getBalance
    public static final String RIALI_ACCOUNT = "riali";
    public static final String ARZI_ACCOUNT = "arzi";

    // =============== Just For Test// ===============
    public static final String MESSAGE_SEQUENCE_FOR_tblog = "MESSAGE_SEQUENCE_FOR_tblog";

    public static final String INACTIVE_ACC_ST = "0";

    public static final String BLOCK_ACCOUNT = "BLCK";
    public static final int ACCOUNT_NATURE_NO_DEPOSIT = 1;
    public static final String DEST_ACCOUNT_NATURE = "destAccountNature";
    public static final String SRC_ACCOUNT_NATURE = "srcAccountNature";

    //customerFinderByAccountNoNew
    public static final String SRC_ACCOUNT_GROUP = "srcAccountGroup";
    public static final String DEST_ACCOUNT_GROUP = "destAccountGroup";

    //ProcessCardAccountStatus
    public static final String ACCOUNT_STATUS = "accStatus";
    public static final String ACCOUNT_BALANCE = "accBal";
    public static final String ACCOUNT_BRANCH_ID = "accBranchId";

    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";

    public static final String CREATE_DATE = "createDate";
    public static final String CREATE_TIME = "createTime";

    public static final String TRANS_DESC = "transDesc";

    //---------cfs and cm--------------------------
    //transactionType
    public static final String TRANSACTION_TYPE_PG = "PG";
    public static final String TRANSACTION_TYPE_KIOSK = "KS";
    public static final String TRANSACTION_TYPE_MP = "MP";
    public static final String TRANSACTION_TYPE_POS = "PS";
    public static final String TRANSACTION_TYPE_POS_BRANCH = "PB";

    public static final String ACCOUNT_FIELD = "account-field";
    public static final String PAYMENT_CODE = "payCode";
    public static final String CARD = "Card";

    public static final String GIFT_CARD_007 = "007";

    public static final String Host_Type_C2C = "C2C";
    public static final String Host_Type_C2F = "C2F";
    public static final String Host_Type_F2C = "F2C";
    public static final String Host_Type_F2F = "F2F";
    public static final String Host_Type_C2S = "C2S";
    public static final String Host_Type_F2S = "F2S";
    public static final String Host_Type_S2C = "S2C";
    public static final String Host_Type_S2F = "=S2F";

    public static final String DEFAULT_CHANNEL_CODE = "98";

    //=============extension phase in fundTransfer
    public static final String MERCHANT_TERMINAL_ID = "MerchantTermianlId";

    public static final String MERCHANT_RRN = "MerchantRRN";
    public static final String SPW_PAYMENT_ID = "SpwPaymentId";

    public static final String MAX_TRANS_NO_IN_LIST = "150";
    public static final String BLOCK_AMOUNT = "blockAmount";
    public static final String IS_FORCE = "isForce";
    public static final String BALANCE_MINIMUM = "balanceMinimum";
    public static final String SUBSID_AMOUNT = "subsidAmnt";

    public static final String AVAILBLE_BAL_SIGN = "avaibleBalSign";

    //============Represntative accounts =============
    public static final String BLOCK_NO = "BLCKNO";
    public static final String BROKER_NO = "BROKERNO";
    public static final String PROVIDER_ID = "providerId";
    public static final String BLOCK_DESC = "BLCKDESC";
    public static final String BLOCK_DATE = "BLCKDT";
    public static final String BLOCK_STATUS = "BLCKST";
    public static final String OPCODE = "OPCODE";

    //==share stock
    public static final String BLCK_DATE = "BlockDate";
    public static final String BLCK_TIME = "BlockTime";

    public static final String TRANS_AMOUNT = "TRANSAMOUNT";

    public static final String FROM_DATE_TIME = "fromDateTime";
    public static final String FROM_DATE = "fromDate";
    public static final String FROM_TIME = "fromTime";
    public static final String TO_DATE_TIME = "toDateTime";
    public static final String TO_DATE = "toDate";
    public static final String TO_TIME = "toTime";

    //table names
    public static final String TB_BLOCK_AMOUNT = "TBBLCKSTACC";
    public static final String TB_CUSTOMER_ACCOUNTS = "tbcustomeraccounts";
    public static final String TB_UNBLOCK_STOCK_ACCOUNTS = "TBUNBLCKSTACC";
    public static final String ACC_BROKER_REL_ST = "accBrkRelSt";
    public static final String TB_CHECK_DIGITS_ALG = "tbchkdigitalg";

    public static final String SRC_SMS_NOTIFICATION = "srcSmsNotification";
    public static final String DEST_SMS_NOTIFICATION = "destSmsNotification";
    public static final String BRANCH_CHANNEL_CODE = "01";
    public static final String WITHDRAW = "0";
    public static final String DEPOSIT = "1";

    public static final String PAYA_REF_ID = "payaRefId";
    public static final String REQUEST_DATE = "REQUESTDATE";

    public static final String ID = "id";

    public static final String INVALID_NUMBER_OF_PAYCODES = "invalidNoPC";
    public static final String PAYMENT_CODE_IS_INVALID = "InvalidPayCode";
    public static final String NO_RELATED_ALG_EXIST = "NoAlgExist";

    public static final byte MELLICODE_VALID = 1;
    public static final byte MELLICODE_INVALID = 2;

    //Municipality bill payment followup
    public static final String MNCPLTY_BILL_ID = "mncBillId";
    public static final String MNCPLTY_PAYMENT_ID = "mncPayId";
    public static final String MNCPLTY_BILL_AMNT = "mncAmnt";
    public static final String MNCPLTY_PAY_BRANCH = "mncBrnch";
    public static final String MNCPLTY_PAY_DATE = "mncDate";
    public static final String MNCPLTY_PAY_TIME = "mncTime";
    public static final String MNCPLTY_IS_FAR_CHECK_NEEDED = "isFaraCheckNeeded";

    //PPRS
    public static final String SERVICE_TYPE = "serviceType";

    public static final String ENEX_BLOCK_AMOUNT = "blckAmnt";

    //CFD Constants
    public static final long IGNORE_MAX_TRANS_LIMIT = -1000000000000000000L;
    public static final int NORMAL_CARD = 1;
    public static final int GIFT_CARD = 2;
    public static final int CARD_STATUS_DISABLE_FLAG = 0;
    public static final int CARD_STATUS_NOT_ASSIGNED_FLAG = -1;
    public static final String CARD_STATUS_ACTIVE_FLAG = "1";
    public static final int HOST_CFS = 1;
    public static final int HOST_FARAGIR = 3;
    public static final String CARD_DEFINITION = "cardDefinition";
    public static final String CARD_DEPOSIT = "cardDeposit";
    public static final String CARD_REVOKE = "cardRevoke";
    public static final String CARD_FOLLOWUP = "cardFollowup";
    public static final String CARD_DEFAULT_RECORDFLAG = "99";
    public static final String CARD_FINANCIAL_RECORDFLAG = "88";
    public static final String CARD_BALANCE = "cardBalance";

    public static final String E_STATUS_CLOSE = "0";

    public static final String UNBLOCKED_E_STATUS = "1";
    public static final String DEFAULT_CURRENCY = "364";
    //Ach  file params
    public static final String ACH_SESSION_ID = "pSession";
    public static final String ACH_SRC_IBAN = "pSrcAcc";
    public static final String ACH_DST_IBAN = "pDstAcc";
    public static final String ACH_AMOUNT = "pAmnt";
    public static final String IS_ACH_REQUEST = "isPayaReq";
    public static final String ACH_REF_NO = "pRefNo";
    public static final String ACH_FT_REQ_MSG = "pFTReqMsg";
    public static final String IS_TJ_ACH_FILE = "isTjPayaFile";

    //GROUP CARD Constants
    public static final String GROUP_CARD_ACCOUNT_TYPE1 = "511";
    public static final String GROUP_CARD_ACCOUNT_TYPE2 = "561";
    public static final String GROUP_CARD_ACCOUNT_TYPE3 = "512";
    public static final String GROUP_CARD_TYPE = "1";
    public static final String NORMAL_CARD_TYPE="0";
    public static final String GROUP_CARD_TYPE_STR = "G";
    public static final String NORMAL_CARD_TYPE_STR="N";
    public static final String GROUP_CARD_PARENT = "1";
    public static final String GROUP_CARD_CHILD = "2";
    public static final String NORMAL_CARD_SERIES = "0";
    public static final String GROUP_CARD = "GRPCD";
    public static final short BLOCK_GROUP_CARD = 0;
    public static final short UNBLOCK_GROUP_CARD = 1;
    public static final String DEACTIVE_CHILD_CARD_CFD = "DACTCHCFD";
    public static final String DEACTIVE_PARENT_CARD_CFD = "DACTPRCFD";

    public static final String QUEUE_MANAGER_HOST_IP = "queue-manager-hostname";
    public static final String QUEUE_MANAGER_HOST_PORT = "queue-manager-port";
    public static final String QUEUE_MANAGER_NAME= "queue-manager";
    public static final String SEND_QUEUE= "send-queue";
    public static final String RECIEVE_QUEUE= "receive-queue";
    public static final String QUEUE_MANAGER_TRANS_TYPE = "queueManagerTransportType";
    public static final String QUEUE_LISTENER_CLASS_NAME= "qlistener-class-name";

    public static final String THREAD_POOL_INIT_SIZE= "threadPoolInitSize";
    public static final String THREAD_POOL_MAX_SIZE= "threadPoolMaxSize";

    public static final String DB_POOL_INIT_SIZE= "dbConnectionPoolInitSize";
    public static final String DB_POOL_MAX_SIZE= "dbConnectionPoolMaxSize";
    public static final String DB_POOL_OPTIMAL_SIZE= "dbConnectionPoolOptimalSize";

   /*ISO*/
    public static final String ISO_SERVICE = "ISO";
    public static final String MERCHANT_TERMINAL_TYPE = "MerchantTermianlType";

    //  Branch
    public static final int BALANCE_REQUEST_LENGTH = 76;
    public static final int ACCOUNT_TYPE_INQUIRY_REQUEST_LENGTH = 60;
    public static final int NAC_REQUEST_LENGTH =627 ;
    public static final int UPDATE_ROW_REQUEST_LENGTH =652 ;
    public static final int BATCH_NAC_REQUEST_LENGTH =678 ;
    public static final int ACCOUNT_STATUS_REQUEST_LENGTH =88;
    public static final int REMOVE_ROW_REQUEST_LENGTH =86 ;
    public static final int UPDATE_ACCOUNT_REQUEST_LENGTH = 161;
    public static final int ONLINE_ACCOUNT_REQUEST_LENGTH =130 ;
    public static final int ACCOUNT_LIST_REQUEST_LENGTH = 71;
    public static final int DEPOSIT_REQUEST_LENGTH = 184;
    public static final int DEPOSIT_GIFTCARD_REQUEST_LENGTH = 159;
    public static final int DEPOSIT_REVERSE_REQUEST_LENGTH =180;
    public static final int DEPOSIT_GIFTCARD_REVERSE_REQUEST_LENGTH =185;
    public static final int WITHDRAW_REQUEST_LENGTH =155;
    public static final int WITHDRAW_WAGE_REQUEST_LENGTH =111;
    public static final int WITHDRAW_GIFTCARD_REQUEST_LENGTH =159 ;
    public static final int WITHDRAW_REVERSE_REQUEST_LENGTH =180;
    public static final int WITHDRAW_WAGE_REVERSE_REQUEST_LENGTH =126;
    public static final int FOLLOWUP_REQUEST_LENGTH = 88;
    public static final int CHANGE_ACCOUNT_STATUS_REQUEST_LENGTH = 158;
    public static final int RESET_ACCOUNT_REQUEST_LENGTH = 62;
    public static final int STATEMENT_REQUEST_LENGTH = 210;
    public static final int SERVICE_STATUS_REQUEST_LENGTH = 63;
    public static final int CHANGE_ACCOUNT_TYPE_REQUEST_LENGTH = 61;
    public static final int ACCOUNT_INQUIRY_REQUEST_LENGTH = 60;
    public static final int BLOCK_REPORT_REQUEST_LENGTH = 61;
    public static final int ACCOUNT_BLOCK_REPORT_REQUEST_LENGTH = 60;
    public static final int ACCOUNT_TO_CARD_REQUEST_LENGTH = 102;
    public static final int CANCELLATION_GIFTCARD_REQUEST_LENGTH = 91;
    public static final int DISCHARGE_GIFTCARD_REQUEST_LENGTH = 107;
    public static final int PINPAD_ACCOUNT_REQUEST_LENGTH = 65;
    public static final int VALID_VERHOEF=90;
    public static final int VALID_EXPENSE_VERHOEF=114;
    public static final int REMITTANCE_INQUIRY_REQUEST_LENGTH=104;
    public static final int WITHDRAW_REMITTANCE_REQUEST_LENGTH=172;
    public static final int WITHDRAW_ATM_REQUEST_LENGTH=143;
    public static final int DEPOSIT_ATM_REQUEST_LENGTH=143;
    public static final int REVERSE_WITHDRAW_ATM_REQUEST_LENGTH=169;
    public static final int REVERSE_DEPOSIT_ATM_REQUEST_LENGTH=169;
    public static final int INQUIRY_ATM_REQUEST_LENGTH=60;
    public static final int REGISTER_PAYA_REQUEST_LENGTH=556;
    public static final int SEND_PAYA_REQUEST_LENGTH=150;
    public static final int GET_PAYA_REQUEST_LENGTH=74;
    public static final int INACTIVE_PAYA_REQUEST_LENGTH=74;
    public static final int GET_IN_DUE_DATE_PAYA_REQUEST_LENGTH=68;
    public static final int DELETE_PAYA_REQUEST_LENGTH=74;
    public static final int RESEND_PAYA_REQUEST_LENGTH=60;
    public static final int REPORT_PAYA_REQUEST_LENGTH=76;
    public static final String ZERO_ACCOUNT_NO = "0000000000000";
    public static final String ZERO_CARD_NO = "0000000000000000";
    public static final String PIN_CREATE_NAC = "60100";
    public static final String PIN_UPDATE_ROW = "60102";
    public static final String PIN_GET_ACCOUNT_STATUS = "60105";
    public static final String PIN_UPDATE_ACCOUNT = "60110";
    public static final String PIN_ONLINE_ACCOUNT = "60115";
    public static final String PIN_CHANGE_ACCOUNT_STATUS = "60120";
    public static final String PIN_ACCOUNT_LIST = "60125";
    public static final String PIN_RESET_ACCOUNT = "60135";
    public static final String PIN_GET_MINI_ACCOUNT_STATUS = "60140";
    public static final String PIN_ACCOUNT_TYPE_INQUIRY = "60142";
    public static final String PIN_CREATE_NAC_BATCH = "60145";
    public static final String PIN_SET_SERVICE_STATUS = "60150";
    public static final String PIN_BLOCK_REPORT = "60155";
    public static final String PIN_ACCOUNT_TO_CARD = "60160";
    public static final String PIN_SIMIN_BLOCK_REPORT = "60165";
    public static final String PIN_ACCOUNT_BLOCK_REPORT = "60170";
    public static final String PIN_SIMIN_ACCOUNT_BLOCK_REPORT = "60175";
    public static final String PIN_REMOVE_ROW = "60180";
    public static final String PIN_PINPAD_ACCOUNT = "60185";
    public static final String PIN_CHANGE_ACCOUNT_TYPE = "60190";
    public static final String PIN_ACCOUNT_INQUIRY = "60195";
    public static final String PIN_BALANCE = "60200";
    public static final String PIN_STATEMENT = "60205";
    public static final String PIN_DEPOSIT = "60210";
    public static final String PIN_DEPOSIT_RESPONSE = "60211";
    public static final String PIN_WITHDRAW = "60215";
    public static final String PIN_WITHDRAW_RESPONSE = "60216";
    public static final String PIN_FOLLOWUP = "60220";
    public static final String PIN_COUNT_STATEMENT = "60225";
    public static final String PIN_DEPOSIT_GIFTCARD = "60230";
    public static final String PIN_DEPOSIT_GIFTCARD_RESPONSE = "60231";
    public static final String PIN_WITHDRAW_GIFTCARD = "60235";
    public static final String PIN_CANCELLATION_GIFTCARD = "60240";
    public static final String PIN_DISCHARGE_GIFTCARD = "60245";
    public static final String PIN_FULL_STATEMENT = "60250";
    public static final String PIN_DEPOSIT_REVERSE = "60410";
    public static final String PIN_WITHDRAW_REVERSE = "60415";
    public static final String PIN_DEPOSIT_GIFTCARD_REVERSE = "60420";
    public static final String PIN_IsValid_Verhoef = "60304";
    public static final String PIN_IsValid_ExpenseVerhoef = "60306";
    public static final String PIN_REMITTANCE_INQUIRY = "60300";
    public static final String PIN_WITHDRAW_REMITTANCE = "60302";
    public static final String PIN_WITHDRAW_ATM  = "60255";
    public static final String PIN_WITHDRAW_ATM_RESPONSE  = "60256";
    public static final String PIN_DEPOSIT_ATM  = "60260";
    public static final String PIN_DEPOSIT_ATM_RESPONSE = "60261";
    public static final String PIN_WITHDRAW_WAGE  = "60270";
    public static final String PIN_WITHDRAW_WAGE_RESPONSE = "60271";
    public static final String PIN_REVERSE_WITHDRAW_WAGE  = "60470";
    public static final String PIN_REVERSE_WITHDRAW_WAGE_RESPONSE  = "60471";
    public static final String PIN_REVERSE_WITHDRAW_ATM  = "60455";
    public static final String PIN_REVERSE_DEPOSIT_ATM  = "60460";
    public static final String PIN_INQUIRY_ATM  = "60308";
    public static final String PIN_REGISTER_PAYA_REQUEST="22000";
    public static final String PIN_SEND_PAYA="22010";
    public static final String PIN_GET_PAYA="22070";
    public static final String PIN_INACTIVE_PAYA="22090";
    public static final String PIN_GET_IN_DUE_DATE_PAYA="22020";
    public static final String PIN_DELETE_PAYA="22030";
    public static final String PIN_RESEND_PAYA="22080";
    public static final String PIN_REPORT_PAYA="75104";
    public static final int HOST_SGB = 2;
    public static final String BRANCH_SERVICE = "NASIM";
    public static final String BRANCH_WAGE_SERVICE = "BRANCH";
    public static final String DEFAULT_AMOUNT = "+00000000000000000";
    public static final String ACCOUNT_LIST = "account-list";
    public static final String PROVIDER_ID_BRANCH = "00";
    public static final String BROKER_ID_BRANCH = "998";
    public static final String CHN_USER_BRANCH = "BRANCH";
    public static final String JURIDICAL_BLOCK_REQUEST="6";
    public static final String TRAN_COUNT="tranCount";
    public static final String STATEMENT_COUNT="1";
    public static final String STATEMENT_ALL="0";
    public static final String FULL_STATEMENT="2";
    public static final String BLOCK_LIST = "block-list";
    public static final String CARD_ACCOUNT_LIST = "card-account-list";
    public static final String SERVICE_STATUS_MESSAGE = "SRVST";
    public static final String ACCOUNT_STATUS_MESSAGE = "ACCST";
    public static final String E_STATUS_MESSAGE = "EACST";
    public static final String REVOKE_ACCOUNT_MESSAGE = "RACST";
    public static final String ACCOUNT_TYPE_MESSAGE = "ACCTP";
    public static final String ACCOUNT_STATUS_ACTIVE = "1";
    public static final String ACCOUNT_STATUS_INACTIVE = "0";
    public static final String CREATE_NAC_REQUEST_TYPE = "1";
    public static final String UPDATE_NAC_REQUEST_TYPE = "2";
    public static final String GIFT_CARD_ACCOUNT_TITLE = "9";
    public static final String IS_BACKUP_ACCOUNT = "1";
    public static final String IS_NOT_BACKUP_ACCOUNT = "0";
    public static final String SINGLE_GIFT_CARD = "0";

    //  CMS
     public static final String POLICY_ELEMENT = "POLICY";
    public static final String POLICY_CREATE_ELEMENT = "POLICY_CREATE";
    public static final String POLICY_CREATE_BATCH_ELEMENT = "POLICY_CREATE_BATCH";
    public static final String POLICY_UPDATE_ELEMENT = "POLICY_UPDATE";
    public static final String POLICY_UPDATE_ALL_ELEMENT = "POLICY_UPDATEALL";
    public static final String POLICY_UPDATE_COLLECTION_ELEMENT = "POLICY_UPDATECOLLECTION";
    public static final String POLICY_PRESENTATION_ELEMENT = "POLICY_PRESENTATION";
    public static final String POLICY_ENDED_PRESENTATION_ELEMENT = "POLICY_ENDED_PRESENTATION";
    public static final String POLICY_HISTORY_ELEMENT = "POLICYHISTORY";
    public static final String POLICY_ENDED_ELEMENT = "POLICY_ENDED";
    public static final String CHARGE_PRESENTATION_ELEMENT = "CHARGE_PRESENTATION";
    public static final String CHARGE_REPORT_ELEMENT = "CHARGE_REPORT";
    public static final String CHILD_INFO_ELEMENT = "CHILDINFO";
    public static final String BALANCE_GROUPCARD_ELEMENT = "BALANCE_GROUPCARD";
    public static final String STATEMENT_GROUPCARD_ELEMENT = "STATEMENT_GROUPCARD";
     public static final String JOINTACCOUNTINFO_ELEMENT = "JOINTACCOUNTINFO";
    public static final String JOINTACCOUNT_ELEMENT = "JOINTACCOUNT";
     public static final String BALANCE_GIFTCARD_ELEMENT = "BALANCE_GIFTCARD";
    public static final String TOURISTCARD_ELEMENT = "TOURISTCARD";
    public static final String CUSTOMER_ELEMENT = "CUSTOMER";
    public static final String RE_CREATE_ELEMENT = "RE_CREATE";
    public static final String ACCOUNTINFO_ELEMENT = "ACCOUNTINFO";
    public static final String CARDINFO_ELEMENT = "CARDINFO";
    public static final String REVOKE_ELEMENT = "REVOKE";
    public static final String BALANCE_ELEMENT = "BALANCE";
    public static final String CHARGE_GIFTCARD_ELEMENT = "CHARGE_GIFTCARD";
    public static final String DEPOSIT_GIFTCARD_ELEMENT = "DEPOSIT_GIFTCARD";
    public static final String DISCHARGE_GIFTCARD_ELEMENT = "DISCHARGE_GIFTCARD";
    public static final String CANCELLATION_ELEMENT = "CANCELLATION";
    public static final String DCHARGE_ELEMENT = "DCHARGE";
    public static final String IMMEDIATE_CHARGE_ELEMENT = "IMMEDIATE_CHARGE";
    public static final String SMS_REGISTER_ELEMENT = "SMS_REGISTER";
    public static final String SMS_INQUIRY_ELEMENT = "SMS_INQUIRY";
    public static final String CHILDCARD_ELEMENT = "CHILDCARD";
    public static final String WFP_STATEMENT_ELEMENT = "WFP_STATEMENT";
    public static final String WFP_CHARGE_ELEMENT = "WFP_CHARGE";
    public static final String STATEMENT_ELEMENT = "STATEMENT";
    public static final String SUMMARY_ELEMENT = "SUMMARY";
    public static final String STOCK_DEPOSIT_ELEMENT = "STOCK_DEPOSIT";
    public static final String STOCK_FOLLOWUP_ELEMENT = "STOCK_FOLLOWUP";

    public static final String UPDATE_CHILD_MSG_TYPE = "48";
    public static final String CREATE_CHILD_MSG_TYPE = "49";
    public static final String UPDATE_MSG_TYPE = "59";
    public static final String CREATE_MSG_TYPE = "50";
    public static final String SEPARAT_MSG_TYPE = "51";
    public static final String RECREATE_MSG_TYPE = "52";
    public static final String ACCOUNTINFO_MSG_TYPE = "53";
    public static final String CARDINFO_MSG_TYPE = "54";
    public static final String REVOKE_MSG_TYPE = "55";
    public static final String JOINT_ACCOUNTINFO_MSG_TYPE = "56";
    public static final String JOINT_CREATE_MSG_TYPE = "57";
    public static final String JOINT_UPDATE_MSG_TYPE = "58";
    public static final String BALANCE_MSG_TYPE = "60";
    public static final String CHARGE_GIFTCARD_MSG_TYPE = "61";
    public static final String DISCHARGE_GIFTCARD_MSG_TYPE = "62";
    public static final String CANCELLATION_MSG_TYPE = "63";
    public static final String DEPOSIT_GIFTCARD_MSG_TYPE = "64";
    public static final String BALANCE_GIFTCARD_MSG_TYPE = "65";
    public static final String CREATE_CARD_RECORDFLAG = "11";
    public static final String CONNECTIVITY_RECORDFLAG = "22";
    public static final String SEPARAT_RECORDFLAG = "44";
    public static final String POLICY_CREATE_MSG_TYPE = "70";
    public static final String POLICY_UPDATE_MSG_TYPE = "71";
    public static final String POLICY_UPDATE_ALL_MSG_TYPE = "72";
    public static final String POLICY_UPDATE_COLLECTION_MSG_TYPE = "73";
    public static final String POLICY_PRESENTATION_MSG_TYPE = "75";
    public static final String BALANCE_GROUPCARD_MSG_TYPE = "76";
    public static final String STATEMENT_GROUPCARD_MSG_TYPE = "77";
    public static final String CHARGE_RECORDS_MSG_TYPE = "78";
    public static final String POLICY_CREATE_BATCH_MSG_TYPE = "79";
    public static final String POLICY_ENDED_PRESENTATION_MSG_TYPE = "80";
    public static final String POLICY_HISTORY_MSG_TYPE = "81";
    public static final String CHARGE_REPORT_MSG_TYPE = "82";
    public static final String DCHARGE_MSG_TYPE = "83";
    public static final String SMS_REGISTER_MSG_TYPE = "84";
    public static final String SMS_INQUIRY_MSG_TYPE = "85";
    public static final String CHILD_INFO_MSG_TYPE = "86";
    public static final String POLICY_ENDED_MSG_TYPE = "87";
    public static final String WFP_CHARGE_MSG_TYPE = "88";
    public static final String WFP_STATEMENT_MSG_TYPE = "89";
    public static final String IMMEDIATE_CHARGE_MSG_TYPE = "90";
    public static final String STATEMENT_ACCOUNT_MSG_TYPE = "91";
    public static final String SUMMARY_MSG_TYPE = "92";
    public static final String STOCK_DEPOSIT_MSG_TYPE = "93";
    public static final String STOCK_DEPOSIT_REVERSE_MSG_TYPE = "95";
    public static final String STOCK_FOLLOWUP_MSG_TYPE = "94";
    public static final String LEGAL_ACCOUNT_TYPE = "2";
    public static final int TRANSACTION_DATE = 8;


    public static final String POSHTIBAN_ACCOUNT_GROUP = "045";
    public static final String POLICY_STATUS_ENDED = "E";
    public static final String POLICY_STATUS_REVOKED = "R";

    public static final String BANKE_TEJARAT_BIN = "627353";
    public static final String BANKE_TEJARAT_BIN_NEW = "585983";

    public static final String CARD_SEQUENCE_NO_DEFAULT= "1";

    public static final String TABLE_NAME_CFS_CARD_ACCOUNT = "TBCFSCARDACCOUNT";
    public static final String TABLE_NAME_NON_CFS_CARD_ACCOUNT = "TBNONCFSCARDACC";
    //Hamreh-e-Avval payment
    public static final int ACCOUNT_NATURE_NO_DEPOSIT_BY_PATTERN = 2;
    public static final String DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT = "Destination Account No not Permitted to be Deposit";
    public static final String ACTIVE_PATTERN = "1";
    public static final String VALID_PATTERN = "1";

    // SIMIN
    public static final String SIMIN_SERVICE = "SIMIN";
    public static final String PIN_CHANGE_ACCOUNT_STATUS_SIMIN = "90120";
    public static final String PIN_DEPOSIT_BLOCK_SIMIN = "90122";
    public static final String PIN_REMOVE_DOC_SIMIN = "90100";
    public static final String PIN_ACCOUNT_INFO_SIMIN = "90105";
    public static final String PIN_PIN_SET_SERVICE_STATUS_SIMIN = "90150";
    public static final String PIN_PIN_GET_STATEMENT_SIMIN = "90110";
    public static final String PIN_CREATE_SIMIN_NAC = "90118";
    public static final String PIN_CREATE_NAC_ATM="90124";
    public static final String PIN_OFFLINE_NAC_ATM="90112";
    public static final String PIN_CHANGE_ATM_STATUS="90114";
    public static final String PIN_CHANGE_STATUS_CBI="90126";
    public static final String PIN_CHANGE_DEVICE_BRANCH="90128";
    public static final int SIMIN_CHANGE_STATUS_REQUEST_LENGTH = 336;
    public static final int SIMIN_DEPOSIT_BLOCK_REQUEST_LENGTH = 319;
    public static final int SIMIN_REMOVE_DOCUMENT_REQUEST_LENGTH = 121;
    public static final int SIMIN_ACCOUNT_INFO_REQUEST_LENGTH = 60;
    public static final int SIMIN_SERVICE_STATUS_REQUEST_LENGTH = 293;
    public static final int GET_STATEMENT_REQUEST_LENGTH = 76;
    public static final int SIMIN_NAC_REQUEST_LENGTH =139 ;
    public static final int CREATE_NAC_ATM_REQUEST_LENGTH=76;
    public static final int OFFLINE_ATM_REQUEST_LENGTH=75;
    public static final int CHANGE_ATM_STATUS_REQUEST_LENGTH=76;
    public static final int SIMIN_CHANGE_STATUS_AHKAM_REQUEST_LENGTH = 366;
    public static final int CHANGE_DEVICE_BRANCH_REQUEST_LENGTH=80;
    public static final String BROKER_ID_SIMIN = "997";
    public static final String PROVIDER_ID_SIMIN = "00";
    public static final String CHN_USER_SIMIN = "SIMIN";
    public static final String TERMINAL_ID_SIMIN = "SI";
    public static final String FIRST_NAME="firstName";
    public static final String LAST_NAME="lastName";
    public static final String  CM_BLOCK="0";
    public static final String  CM_UNBLOCK="1";
    public static final String  FARAGIR_BLOCK="1";
    public static final String  FARAGIR_UNBLOCK="0";
    public static final String  DEPOSIT_REQUEST="1";
    public static final String  WITHDRAW_REQUEST="2";
    public static final String  UNBLOCK_DEPOSIT="1";
    public static final String  BLOCK_DEPOSIT_NON_BRANCH="2";
    public static final String  BLOCK_DEPOSIT_BRANCH="3";
    public static final String  BLOCK_DEPOSIT_ALL="4";
    public static final String  UNBLOCK_PREFIX = "U";
    public static final String  BRANCH_POSTFIX = "B";
    public static final String  NON_BRANCH_POSTFIX = "E";
    public static final String  UNBLOCK_DEPOSIT_ALL_REQUEST="UA";
    public static final String  UNBLOCK_DEPOSIT_NON_BRANCH_REQUEST="UE";
    public static final String  UNBLOCK_DEPOSIT_BRANCH_REQUEST="UB";
    public static final String  BLOCK_DEPOSIT_NON_BRANCH_REQUEST="BE";
    public static final String  BLOCK_DEPOSIT_BRANCH_REQUEST="BB";
    public static final String  BLOCK_DEPOSIT_ALL_REQUEST="BA";
    public static final String  BLOCK_STATUS_FLAG="0";
    public static final String  UNBLOCK_STATUS_FLAG="1";
    public static final String  FARAGIR_DEPOSIT_BLOCK="2";
    public static final String  FARAGIR_DEPOSIT_UNBLOCK="3";
    public static final String  DEPOSIT_UBLOCK="11";
    public static final String NATIONAL_STATUS="1";    // Valid:1   invalid:0
    public static final String ATM_ACCOUNT_GROUP ="007";
    public static final String OFFLINESTATUS_DEVICE="0";
    public static final String ACTIVE_ISMODIFIED="0";    // Active:0  notActive:1
    public static final String NOTACTIVE_ISMODIFIED="1";
    public static final String ATM_ACTIVE_STATUS="1";
    public static final String ATM_IN_ACTIVE_STATUS="0";
    public static final String ONLINE_ATM_STATUS="1";    // Online:1   offline:0
    public static final String OFFLINE_ATM_STATUS="0";
    public static final String NO_LOCKSTATUS_FLAG="-1";
    public static final String BROKER_ID_SIMIN_CBI = "997";
    public static final String PROVIDER_ID_SIMIN_CBI = "01";
    public static final String BROKER = "BROKER";
    public static final String PROVIDER = "PROVIDER";
    public static final String BROKER_ID_SIMIN_FOR_FARAGIR = "00000997";
    public static final String PINPAD_ACCOUNT_GROUP ="001";
    public static final String CREATE_DEVICE_ONLINE ="CRTO";
    public static final String CREATE_DEVICE_OFFLINE ="CRTF";
    public static final String OFFLINE_DEVICE ="OFLN";
    public static final String ACTIVE_DEVICE ="ACTV";
    public static final String INACTIVE_DEVICE ="IACTV";
    public static final String CHANGE_DEVICE_CODE ="CHNDC";
    public static final String DEVICE_ONLINE_ACC_REQUEST ="O";
    public static final String DEVICE_OFFLINE_ACC_REQUEST ="F";

    //Tourist
    public static final String TERMINAL_ID_NASIM = "NS";
    public static final String TERMINAL_ID_TOURIST = "TR";
    public static final String TOURIST_SERVICE = "TOURIST";
    public static final int TOURIST_BALANCE_REQUEST_LENGTH = 71;
    public static final int TOURIST_STATEMENT_REQUEST_LENGTH = 116;
    public static final int TOURIST_SUMMARY_REPORT_REQUEST_LENGTH = 118;
    public static final int TOURIST_FUND_TRANSFER_REQUEST_LENGTH = 122;
    public static final int TOURIST_CHARGE_REQUEST_LENGTH = 109;
    public static final int TOURIST_FOLLOWUP_REQUEST_LENGTH = 97;
    public static final int TOURIST_CHARGE_STATEMENT_REQUEST_LENGTH = 103;
    public static final int TOURIST_REVOKE_REQUEST_LENGTH = 91;
    public static final String PIN_TOURIST_BAL = "90200";
    public static final String PIN_TOURIST_CARD_STATEMENT = "90205";
    public static final String PIN_TOURIST_CHARGE = "90210";
    public static final String PIN_TOURIST_DISCHARGE = "90215";
    public static final String PIN_TOURIST_FOLLOWUP = "90220";
    public static final String PIN_TOURIST_CHARGE_STATEMENT = "90225";
    public static final String PIN_TOURIST_REVOKE = "90230";
    public static final String PIN_TOURIST_SUMMARY_REPORT = "90255";
    public static final String PIN_TOURIST_FUND_TRANSFER = "90260";
    public static final String E_STATUS_ACTIVE = "1";
    public static final String SERVICE_STATUS_ACTIVE = "1";
    public static final String DEPOSIT_TRANSACTION = "D";
    public static final String WITHDRAW_TRANSACTION = "W";

    //Moshtarak
    public static final String SHARED_ACCOUNT_TYPE = "3";
    public static final int ACTIVE_ROW_STATUS = 1;
    public static final int REMOVE_ROW_STATUS = 2;
    public static final int REVOKE_ROW_STATUS = 3;
    public static final String FOREIGNERS_NATIONAL_CODE = "999999999";

    //Charge Managment
    public static final String CONNECTOR= "connector";
    public static final String CHARGE = "1";
    public static final String DCHARGE = "0";
    public static final int DCHARGE_BATCHPK =999999999 ;
    public static final String NON_AGGREGATE_TYPE = "1";
    public static final String DCHARGE_LAST_CHARGE = "L";
    public static final String DCHARGE_AMOUNT = "A";
    public static final String DCHARGE_BALANCE = "T";

    // Credits
    public static final String TERMINAL_ID_CREDITS = "CD";
    public static final String NC_SERVICE = "GC";
    public static final String CREDITS_SERVICE = "CREDITS";
    public static final int CREDITS_BALANCE_REQUEST_LENGTH = 86;
    public static final int CREDITS_STATEMENT_REQUEST_LENGTH = 218;
    public static final int CREDITS_DEPOSIT_REQUEST_LENGTH = 192;
    public static final int CREDITS_WITHDRAW_REQUEST_LENGTH = 163;
    public static final int CREDITS_FOLLOWUP_REQUEST_LENGTH = 96;
    public static final int CREDITS_DEPOSIT_REVERSE_REQUEST_LENGTH= 188;
    public static final int CREDITS_WITHDRAW_REVERSE_REQUEST_LENGTH= 188;
    public static final int CREDITS_PICHACK_CHECK_STATUS_REQUEST_LENGTH = 76;
    public static final String PIN_CREDITS_BAL = "90300";
    public static final String PIN_CREDITS_STATEMENT = "90305";
    public static final String PIN_CREDITS_DEPOSIT = "90310";
    public static final String PIN_CREDITS_WITHDRAW = "90315";
    public static final String PIN_CREDITS_DEPOSIT_REVERSE = "90410";
    public static final String PIN_CREDITS_WITHDRAW_REVERSE = "90415";
    public static final String PIN_CREDITS_FOLLOWUP = "90320";
    public static final String PIN_CREDITS_DEPOSIT_RESPONSE = "90311";
    public static final String PIN_CREDITS_WITHDRAW_RESPONSE = "90316";
    public static final String PIN_CREDITS_PICHACK_CHECK_STATUS = "90325";
    public static final String CREDIT_VALUE = "0";
    public static final String DEBIT_VALUE = "1";
    public static final String EFECTIVE_DATE = "EfectiveDate";
    public static final String CHEQUE_NUMBER = "chequeNumber";

    //Amx service
    public static final int AMX_NAC_REQUEST_LENGTH =595;
    public static final int CHANGE_ACCOUNT_STATUS_AMX_REQUEST_LENGTH = 79;
    public static final String AMX= "AMX";
    public static final String UNCONDITIONAL_REVOKE_STATUS= "U";
    public static final String REVOKE_ACCOUNT_STATUS= "0";
    public static final String REVIVAL_ACCOUNT_STATUS= "1";
    public static final String PIN_CREATE_NAC_AMX = "90500";
    public static final String PIN_CHANGE_ACCOUNT_STATUS_AMX = "90520";
    public static final String TERMINAL_ID_AMX = "AX";
    public static final String AMX_SERVICE = "AMX";

    //Account Inquiry
    public static final String ACCOUNT_NOT_FOUND = "5";
    public static final String ACCOUNT_IS_IN_TWO_CORE = "4";

    //GroupCard for all
    public static final String NORMAL_ACCOUNT = "N";
    public static final String GROUP_CARD_ACCOUNT = "G";

    //Remittance
    public static final String REGISTERED_STATUS = "REGISTERED";
    public static final String PAYED_STATUS = "PAYED";

    //Safe Deposit Box
    public static final String TERMINAL_ID_SAFE_BOX = "SA";
    public static final String SAFE_BOX_SERVICE = "SAFEBOX";
    public static final int FUND_TRANSFER_SAFE_BOX_REQUEST_LENGTH = 130;
    public static final int FUND_TRANSFER_REVERSE_SAFE_BOX_REQUEST_LENGTH = 156;
    public static final int FOLLOW_UP_SAFE_BOX_REQUEST_LENGTH = 77;
    public static final int BALANCE_SAFE_BOX_REQUEST_LENGTH = 51;
    public static final String PIN_FUND_TRANSFER_SAFE_BOX = "90502";
    public static final String PIN_FUND_TRANSFER_SAFE_BOX_RESPONSE = "90503";
    public static final String PIN_FUND_TRANSFER_REVERSE_SAFE_BOX = "90602";
    public static final String PIN_FUND_TRANSFER_REVERSE_SAFE_BOX_RESPONSE = "90603";
    public static final String PIN_FOLLOW_UP_SAFE_BOX = "90504";
    public static final String PIN_BALANCE_SAFE_BOX = "90506";
    public static final String Host_Type = "HostType";
    public static final String WAGE_DOCUMENT = "01";
    public static final String DEPOSIT_DOCUMENT = "02";
    public static final String STAMP_DOCUMENT = "03";
    public static final String DISCHARGE_DOCUMENT = "04";
    public static final String KEY_DOCUMENT = "05";
    public static final String EXTRA_INFO = "extraInfo";
    public static final String FOLLOW_TYPE = "FollowType";
    public static final String FT_SAF_NAME = "TJ_BR_FT";
    public static final String FTR_SAF_NAME = "TJ_BR_FTR";
    public static final String REVERSE_SAF_NAME = "TJ_BR_REV";
    public static final String PENDING_HANDLED = "0";
    public static final String SUCCESS_HANDLED = "1";
    public static final String ERROR_HANDLED = "2";
    public static final String FTR_SUCCEEDED = "FTRSucceeded";
    public static final String F2C = "31";
    public static final String C2F = "13";
    public static final String TERMINAL_TYPE_SAFE_BOX = "63";

    //ATM
    public static final String ATM_TYPE="2";
    public static final String PINPAD_TYPE="3";
    public static final String SEPARATOR_BRANCH = "#";
    public static final String BRANCH_MANAGER_CREATE_PIN="995";
    public static final String BRANCH_MANAGER_REVOKE_PIN="997";
    public static final String ATM_DEVICE_TYPE="02";
    public static final String PINPAD_DEVICE_TYPE="03";

    //SMS
    public static final String SMS_SERVICE = "REGSMS";
    public static final String PIN_GET_BALANCE_SMS="90700";
    public static final String PIN_WAGE_SMS="90702";
    public static final String PIN_ACTIVE_SMS="90704";
    public static final String PIN_DISABLE_SMS="90706";
    public static final String PIN_FOLLOWUP_SMS="90708";
    public static final int SMS_BALANCE_REQUEST_LENGTH= 50;
    public static final int SMS_WAGE_REQUEST_LENGTH= 64;
    public static final int SMS_ACTIVE_REQUEST_LENGTH=50;
    public static final int SMS_DISABLE_REQUEST_LENGTH=50;
    public static final int SMS_FOLLOWUP_REQUEST_LENGTH= 76;
    public static final String TERMINAL_ID_SMS = "SM";
    public static final String SMS_NOTIFICATION = "1";
    public static final String DISABLE_SMS_NOTIFICATION = "0";
    public static final String MESSAGE_TYPE = "messageType";
    public static final String WAGE_SMS_TYPE = "W";
    public static final String ACTIVE_SMS_TYPE = "A";
    public static final String DISABLE_SMS_TYPE = "D";

    //Stock Deposit
    public static final String WAGE_OPCODE = "wageOpCode";
    public static final String WAGE_ACCOUNT_NO = "wageAccountNo";
    public static final String WAGE_AMOUNT = "wageAmount";
    public static final String WAGE_DISCRIPTION = "wageDescription";
    public static final String WAGE_EXTRA_INFO = "wageExtraInfo";
    public static final String DEST_MIN_BALANCE = "destMinBalance";
    public static final String DEST_ACCOUNT_BALANCE = "destAccountBalance";
    public static final String WAGE_TX = "wageTx";
    public static final String PG_CHANNEL_CODE = "59";
    public static final String FEE_ACCOUNT= "FEEACCOUNT";
    public static final String FEE_REF_NO= "feeRefNo";
    public static final String FEE_HOST_ID= "feeHostId";
    public static final String FEE_EXTRA_INFO= "feeExtraInfo";
    public static final String FEE_DESC = "feeDesc";
    public static final String FEE_AMOUNT = "feeAmount";
    public static final String FEE_FUND_TRANSFER = "SK";
    public static final String FEE_OP_CODE= "feeOpCode";
    public static final String STK_FT_SAF_NAME = "TJ_STK_FT";
    public static final String STK_FTR_SAF_NAME = "TJ_STK_FTR";
    public static final String STK_REVERSE_SAF_NAME = "TJ_STK_REV";

    //impersonal Accounts job
    public static final String EXIST = "E";
    public static final String DELETE = "D";
    public static final String INSERT = "I";
    public static final int DISTINCT_VIOLATION_ERROR = -803;

    //SMS for Group Card
    public static final String OTHER_CHANNEL_CODE = "99";
    public static final String GROUP_CARD_CHARGE = "2";
    public static final String GROUP_CARD_DE_CHARGE = "3";

    //GruopWeb
    public static final String GROUP_WEB_MEG_SEPARATOR="#";

    //Hoghooghi
    public static final String HOGHOOGHI_ACCOUNT_TYPE="2";

    //PAYA
    public static final String ACTIVE_PAYA_REQUEST = "1";
    public static final String INACTIVE_PAYA_REQUEST = "100";
    public static final String DELETE_PAYA_REQUEST = "200";
    public static final String PAYA_EXCEPTION = "300";
    public static final String TRACK_ID = "trackId";
    public static final int REF_ID_LEN = 28;

    //1400
    public static final String CENTURY="13";


    // one time id
    public static final int ACCOUNT_NATURE_DEPOSIT_ON_TIME = 9;
    public static final String ID_PAID = "1";
    public static final String ID_NOT_PAID = "0";
    //NacLog
    public static final String CREATE_NAC_STATUS="I";
    public static final String UPDATE_NAC_STATUS="U";
    public static final String REVOKE_STATUS="D";
    public static final String REVIVAL_STATUS="V";
    public static final String REMOVE_ROW="R";

    //CBI SIMIN ChangeBlockStatus
    public static final String CBI_UNBLOCK_STATUS="4";
    public static final String CBI_BLOCK_STATUS="6";
    public static final String CBI_TEST_BLOCK_STATUS="2";
    public static final String CBI_TEST_UBLOCK_STATUS="3";
    public static final String DUPLICATE_BLOCK_STATUS="1";
    public static final String NOT_DUPLICATE_BLOCK_STATUS="0";
    public static final String DUPLICATE_UNBLOCK_STATUS="2";
    public static final String CBI_FLAG_ORG="1";    // test=0 orginal=1
    public static final String CBI_FLAG_TEST="0";
    public static final String IS_REVOKED="1";
    public static final String NOT_REVOKED="0";
    //Fee sharing
    public static final String CBI="CENTRALBNK";
    public static final String BRANCH="BRANCH";
    public static final String ACH="ACH";
    public static final String RTGS="RTGS";
    public static final String EBNK_ACH_OPERATION_CODE="EBACHSHR";
    public static final String EBNK_RTGS_OPERATION_CODE="EBRTGSSHR";
    public static final String EBNK_ACH_REVERSE_OPERATION_CODE="EBACHSHRR";
    public static final String EBNK_RTGS_REVERSE_OPERATION_CODE="EBRTGSSHRR";
    public static final String TXN_SRC_FEE = "FEESHARING";
    public static final String FEE_SHARING_SESSION_ID = "FeeSharingSessionId";
    public static final String TX_IS_REVERSAL = "T";
    public static final String TX_IS_NOT_REVERSAL = "F";
    public static final String TX_IS_REVERSED = "Y";
    public static final String TX_IS_NOT_REVERSED = "N";
    public static final String BRANCH_CHANNEL = "branch";
    public static final String EBNK_CHANNEL = "ebnk";

    //SMS for SIMIN BlockAccount
    public static final String SMS_TRANSACTION_TYPE_BLOCK_ACCOUNT="ACCBLOCK";

    //Block Wage
    public static final String BLOCK_WAGE_INFO="BLOCKWAGE";
    public static final String USER_LIST="USERLIST";
    public static final String SIMIN_CHANNEL_CODE = "00";

    //Financial GroupBlock-UnBlock
    public static final String BLOCK_SERVICE = "BLOCK";
    public static final String FINANCIAL_FILE_TYPE = "F";
    public static final String PROVIDER_ID_BLOCK = "00";
    public static final String BROKER_ID_BLOCK = "994";
    public static final String CHN_USER_BLOCK = "BLOCK";
    public static final String BROKER_ID_BLOCK_FOR_FARAGIR = "00000994";
    public static final String BLOCK_SERVICE_TYPE = "block";
    public static final String UNBLOCK_SERVICE_TYPE = "unblock";
    public static final String BLOCK_SERVICE_STATUS = "2"; //tbkashefBatch :: NotProccessed:2 ,BlockEditedRow:3 ,BlockFinishedRow:4, UnBlockEditedRow:5 ,FinishedRow(block&unblock):6 ,FinishedUnBlockRow:7
    public static final String BLOCK_SERVICE_EDIT_STATUS = "3";
    public static final String BLOCK_SERVICE_FINISH_STATUS = "4";
    public static final String UNBLOCK_SERVICE_EDIT_STATUS = "5";
    public static final String UNBLOCK_SERVICE_FINISH_STATUS = "6";
    public static final String BLOCK_PROCESS_STATUS = "0"; // tbkashef  ::NotProcessed:0, blockedEditedRow:5 ,SuccessBlockedFinishedRow:1 ,FailedBlockedFinishRow:2 ,SuccessUnBlockedFinishedRow:3 ,FailedUnBlockedFinishRow:4
    public static final String SUCCESS_BLOCK_PROCESS_STATUS = "1";
    public static final String FAILED_BLOCK_PROCESS_STATUS = "2";
    public static final String SUCCESS_UNBLOCK_PROCESS_STATUS = "3";
    public static final String FAILED_UNBLOCK_PROCESS_STATUS="4";
    public static final String EDIT_BLOCK_PROCESS_STATUS = "5";
    public static final String EDIT_UNBLOCK_PROCESS_STATUS = "6";
    public static final String UNBLOCK_ACCOUNT_STATUS="7";
    public static final String BLOCK_ACCOUNT_STATUS="8";
    public static final String BEFORE_UNBLOCK_JOB="B";
    public static final String AFTER_UNBLOCK_JOB="A";

    //Marhoonat insurance
    public static final String TERMINAL_ID_MARHONAT_INSURANCE = "MA";
    public static final String MARHONAT_SERVICE = "MARIN";
    public static final String TERMINAL_TYPE_MARHOONAT_INSURANCE = "64";

    //Mahcheck
    public static final String MAHCHECK_STATUS = "01";
    public static final String MAHCHECK_BLOCK_SMS_TYPE="BLCKMA";
    public static final String MAHCHECK_UNBLOCK_SMS_TYPE="UNBLCKMA";
    public static final String DEVICE_CODE = "deviceCode";
    public static final String MAHCHECK_BLOCK_STATUS = "4";
    public static final String MAHCHECK_UNBLOCK_STATUS = "5";

    //SMS for SET SERVICE_STATUS_SIMIN
    public static final String SMS_TRANSACTION_TYPE_SET_SERVICE_STATUS="SETSTATUS";

    //Omid Range
    public static final String START_OMID_RANGE = "0003551010006";
    public static final String END_OMID_RANGE = "0003589999990";

    //Manzume
    public static final String MANZUME_SERVICE = "BRANCH";
    public static final String PIN_ACCOUNT_INFO_MANZUME="80105";
    public static final String PIN_DEPOSIT_MANZUME = "80210";
    public static final String PIN_DEPOSIT_REVERSE_MANZUME = "80410";
    public static final String PIN_FOLLOWUP_MANZUME = "80220";
    public static final String PIN_DEPOSIT_MANZUME_RESPONSE = "80211";
    public static final String PIN_DEPOSIT_REVERSE_MANZUME_RESPONSE = "80411";


    public static final int ACCOUNT_INFO_REQUEST_LENGTH =55;
    public static final int MANZUME_FOLLOWUP_REQUEST_LENGTH = 81;
    public static final int MANZUME_DEPOSIT_REVERSE_REQUEST_LENGTH =159;
    public static final int MANZUME_DEPOSIT_REQUEST_LENGTH = 193;
    public static final String HAS_ID = "1";
    public static final String HAS_NOT_ID = "0";
    public static final String TERMINAL_ID_MANZUME = "MN";

    public static final String VIRTUAL_ACCOUNT ="VirtualAccount";
    public static final String MANZUME_DEPOSIT_OP_CODE ="541";   ///bank elam mikone
    public static final String MANZUME_REVERSAL_DEPOSIT_OP_CODE ="899";

    public static final String MANZUME_CMPARAM_MODIFIRE ="ManzumeType";
//    public static final String M_FLAG ="-1";

    public static final String MANZUME_LONGTERM_CMPARAM_MODIFIRE ="ManzumeLongTermType";

    public static final String INVALID_PAYMENT_CODE = "Invalid payment code";

    public static final String IRAN_INSURANCE_ACCOUNT = "IranInsuranceAccount";
    public static final String ID1 = "id1";
    public static final String ID2 = "id2";


}
