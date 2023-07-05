package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: Apr 10, 2013
 * Time: 12:05:10 PM
 */
public abstract class BranchMessage {

    private static Log log = LogFactory.getLog(BranchMessage.class);
    public static final int headerLength = 60;
    protected String txString;

    //Message Header Fields
    //---------------------
    private String pin;                   //N5
    private String messageSequence;       //C12
    private String branchCode;            //C6
    private String userId;                //C6
    private String messageVersion;        //C5
    private String requestDate;           //C6
    private String requestTime;           //C6
    protected String accountNo;             //C13
    //Message Body Fields
    //---------------------
    protected String cardNo;                //C13
    protected String txDate;                //C6
    protected String txTime;                //C6
    protected String origMessageData;       //Num(24)
    private String actionCode;            //Char(4)
    private String availableBalance;      //Num(18)
    private String actualBalance;         //Num(18)
    protected String blockedAmount;         // Num(18), Unsigned
    protected String accountStatus;         //Num(1)      0:Blocked   1:Normal     2:revoke    3:resuscitation     4:Amount Blocked    5:Amount UnBlock

    protected String requestType;           // Num(1)       1: Create   2: Update
    protected String accountType;           // Num(1)       1: Haghighi   2: Hoghooghi    3: Moshtarak
    protected String ownerIndex;            // Num(2)
    protected String accountGroup;          // Num(3)
    protected String statementType;         // Num(1)
    protected String createDate;            // Num(6)
    protected String changeDate;            // Num(6)
    protected String firstName;             // Char(15)
    protected String lastName;              // Char(20)
    protected String fatherName;            // Char(15)

    protected String gender;                // Num(1)
    protected String nationalCode;          // Num(11)
    protected String oldNationalCode;       // Num(11)
    protected String birthDate;             // Num(8)
    protected String ID_Number;             // Num(10)
    protected String ID_SerialNumber;       // Num(6)
    protected String ID_Series;             // Char(3)
    protected String ID_IssueDate;          // Char(8)
    protected String ID_IssueCode;          // Num(4)
    protected String ID_IssuePlace;         // Char(30)
    protected String en_FirstName;          // Num(20)
    protected String en_LastName;           // Num(30)
    protected String ext_IdNumber;          // Num(10)
    protected String old_ext_IdNumber;      // Num(15)
    protected String foreignCountryCode;    // Num(3)
    protected String tel_Number1;           // Num(20)
    protected String tel_Number2;           // Num(20)
    protected String mob_Number;            // Num(20)
    protected String fax_Number;            // Num(20)
    protected String address1;              // Char(50)
    protected String address2;              // Char(50)
    protected String postalCode;            // Num(10)
    protected String currencyCode;          // Num(3)
    protected String nationalCodeValid;     // Num(1)        1: valid,  0: invalid

    protected String withdrawType;          // Num(1)        0: Normal, 1:Alone    2: Together
    protected String accountOpenerName;     // Char(40)
    protected String balance;               // Num(18), Signed

    protected String terminalId;            // Num(2)
    protected String terminalCode;          // Num(2)
    protected String amount;                // Num(18)  unsigned
    protected String transDate;             // Num(6)
    protected String transTime;             // Num(6)
    protected String documentNo;            // Num(6)
    protected String documentDescription;   // Char(30)
    protected String operationCode;         // Num(3)

    protected String accountCount;         // Num(2)
    protected String hostID;                // Num(1)

    protected String blockRow;                // Num(13)

    protected String fromDate;                // Num(6)
    protected String toDate;                // Num(6)
    protected String fromTime;                // Num(6)
    protected String toTime;                // Num(6)
    protected String transactionCount;       // Num(5)
    protected String creditDebit;            //Char(1)
    protected String transactionMinimumAmount;  //Num(18)
    protected String transactionMaximumAmount;  //Num(18)
    protected String transactionDocumentNumber;  //Num(6)
    protected String transactionDescription;  //Chare(30)
    protected String fromSequence;             //Num(12)
    protected String issuerBranchCode;         //Num(5)
    protected String effactiveDate;         //Num(6)
    protected String response;
    protected String hostCode;              //Char(2)
    protected String emailAddress;              //Char(50)
    protected String id1;              //Char(30)
    protected String pinpadBranch;    //Char(5)
    protected String isPoshtiban;    //Num(1)
    protected String id_num;        //Num(15)
    protected String amount_verhoef;    //Num(15)
    protected String cheque_number;    //Num(16)
    protected String date_verhoef;    //Num(8)
    protected String isvalid_verhoef;    //Num(1)   1: valid,  0: invalid
    protected String remittance_request_no;       //Num(10)
    protected String remittanceDate;     //Num(8)
    protected String birthDate_remittance;   //Num(8)
    protected String telNumber_remittance;   //Num(12)
    protected String expirDate_remittance;   //Num(8)
    protected String remittanceAmount;   //Num(18)
    protected String remittanceFeeAmount;   //Num(18)
    protected String remittanceStatus;   //Char(10)
    protected String remittanceMessage_Sequence;   //Num(12)
    protected String operationCodeFeeAmount; //Num(3)
    protected String branchCodeBody; //Num(5)
    protected String maxATM_Balance; //Num(18)
    protected String avgDeficit;   //Num(18)
    protected String deviceCode;   //Char(8)
    protected String bankSend;
    protected String bankRecv;
    protected String sourceIban;
    protected String paymentCode;
    protected String destIban;
    protected String senderName;
    protected String meliCode;
    protected String reciverName;
    protected String payaDescription;
    protected String TrackId;
    protected String refNo;
    protected String operator;
    protected String dueDate;
    protected String serial;
    protected String senderShahab;
    protected String reciverShahab;
    protected String reciverMeliCode;
    protected String reciverPostalCode;
    protected String reason;
    protected String opType;
    protected String groupType;
    protected String groupNo;
    protected String filler;
    protected String cbiFlag;  // test:0  orginal:1
    protected String deviceType;  // ATM:2  PINPAD:3
    protected String atmAccountType; // O:online  F:offline
    protected String atmBranchCode; // char(5)
    protected String existDeviceBranchCode; // char(5)
    protected String organization; // char(2)
    //Start ==> set field length
    public static final int PIN=5;
    public static final int MESSAGE_SEQUENCE=12;
    public static final int BRANCH_CODE=5;
    public static final int USER_ID=6;
    public static final int MESSAGE_VERSION=5;
    public static final int REQUEST_DATE=8;
    public static final int REQUEST_TIME=6;
    public static final int ACCOUNT_NO=13;
    public static final int CARD_NO=16;
    public static final int CARD_NO2=16;
    public static final int CARD_NO3=19;
    public static final int TX_DATE=8;
    public static final int TX_TIME=6;
    public static final int ORIG_MESSAGE_DATA=26;
    public static final int ACTION_CODE=4;
    public static final int AVAILABLE_BALANCE=18;
    public static final int ACTUAL_BALANCE=18;
    public static final int BLOCKED_AMOUNT=18;
    public static final int ACCOUNT_STATUS=1;
    public static final int REQUEST_TYPE=1;
    public static final int ACCOUNT_TYPE=1;
    public static final int OWNER_INDEX=2;
    public static final int ACCOUNT_GROUP=3;
    public static final int STATEMENT_TYPE=1;
    public static final int CREATE_DATE=8;
    public static final int CHANGE_DATE=8;
    public static final int FIRST_NAME=30;
    public static final int LAST_NAME=40;
    public static final int FATHER_NAME=30;
    public static final int GENDER=1;
    public static final int NATIONAL_CODE=11;
    public static final int BIRTH_DATE=8;
    public static final int ID_NUMBER=10;
    public static final int ID_SERIAL_NUMBER=6;
    public static final int ID_SERIES=3;
    public static final int ID_ISSUE_DATE=8;
    public static final int ID_ISSUE_CODE=4;
    public static final int ID_ISSUE_PLACE=30;
    public static final int EN_FIRST_NAME=50;
    public static final int EN_LAST_NAME=50;
    public static final int EXT_ID_NUMBER=15;
    public static final int FOREIGN_COUNTRY_CODE=3;
    public static final int TEL_NO1=20;
    public static final int TEL_NO2=20;
    public static final int MOBILE_NO=20;
    public static final int FAX_NO=20;
    public static final int ADDRESS1=50;
    public static final int ADDRESS2=50;
    public static final int POSTAL_CODE=10;
    public static final int CURRENCY_CODE=3;
    public static final int NATIONAL_CODE_VALID=1;
    public static final int WITHDRAW_TYPE=1;
    public static final int ACCOUNT_OPENER_NAME=50;
    public static final int BALANCE=18;
    public static final int TERMINAL_ID=2;
    public static final int AMOUNT=18;
    public static final int TRANS_DATE=8;
    public static final int TRANS_TIME=6;
    public static final int DOCUMENT_NO=6;
    public static final int DOCUMENT_DESCRIPTION=40;
    public static final int EXTRA_INFO=200;
    public static final int SIMIN_DOCUMENT_DESCRIPTION=230;
    public static final int SIMIN_BLOCK_DESCRIPTION=250;
    public static final int OPERATION_CODE=3;
    public static final int ACCOUNT_COUNT=2;
    public static final int HOST_ID=1;
    public static final int BLOCK_ROW=13;
    public static final int FROM_DATE=8;
    public static final int TO_DATE=8;
    public static final int FROM_TIME=6;
    public static final int TO_TIME=6;
    public static final int TRANSACTION_COUNT=5;
    public static final int CREDIT_DEBIT=1;
    public static final int TRANSACTION_MIN_AMOUNT=18;
    public static final int TRANSACTION_MAX_AMOUNT=18;
    public static final int TRANSACTION_DOCUMENT_NUMBER=6;
    public static final int TRANSACTION_DESCRIPTION=40;
    public static final int FROM_SEQUENCE=12;
    public static final int ISSUER_BRANCH_CODE=5;
    public static final int Effective_Date=8;
    public static final int Effective_Time=6;
    public static final int HOST_CODE=2;
    public static final int E_MAIL=50;
    public static final int ID1=30;
    public static final int ID2=30;
    public static final int FOLLOW_NO=14;
    public static final int BLOCK_COUNT=3;
    public static final int BLOCK_DESCRIPTION=50;
    public static final int CHN_USER=30;
    public static final int ACCOUNT_CARD_COUNT=1;
    public static final int IS_POSHTIBAN=1;
    public static final int ID_Num = 15;
    public static final int Amount_Verhoef = 15;
    public static final int Cheque_Number = 16;
    public static final int Date_Verhoef = 8;
    public static final int Isvalid_Verhoef = 1;
    public static final int REMITTANCE_REQUEST_NO=10;
    public static final int REMITTANCE_DATE=8;
    public static final int REMITTANCE_TELE=12;
    public static final int REMITTANCE_EXPIRE_DATE=8;
    public static final int REMITTANCE_STATUS=10;
    public static final int STATUS_D=1;
    public static final int DEPOSIT_STATUS=2;
    public static final int BRANCHCODEBODY=5;
    public static final int MAXATM_BALANCE=18;
    public static final int AVGDEFICIT=18;
    public static final int DEVICECODE=8;
    public static final int DATE=8;
    public static final int BANK_CODE=2;
    public static final int PRIOD_TYPE=1;
    public static final int IBAN=30;
    public static final int PAYMENT_CODE=35;
    public static final int PAYA_NAME=70;
    public static final int MELI_CODE=15;
    public static final int PAYA_DESCRIPTION=50;
    public static final int TRACK_ID=18;
    public static final int REFNO=4;
    public static final int OPERATOR=3;
    public static final int DUE_DATE=8;
    public static final int SERIAL=6;
    public static final int PAYA_COUNT=4;
    public static final int COUNT=3;
    public static final int PAYA_BRANCH_CODE=6;
    public static final int SHAHAB=16;
    public static final int REASON=10;
    public static final int SENDER_TEL=33;
    public static final int OPERATION_TYPE=1;
    public static final int GROUP_TYPE=1;
    public static final int GROUP_NUMBER=6;
    public static final int FILER=3;
    public static final int CBI_FLAG=1;
    public static final int DEVICE_TYPE=2;
    public static final int ATM_ACCOUNT_TYPE=1;
    public static final int ATM_BRANCH_CODE=5;
    public static final int EXIST_DEVICE_BRANCH_CODE=5;
    public static final int IDENTITY_CODE=15;
    public static final int ORGANIZATION=2;
    //END ==> set field length


        public BranchMessage(String msgStr) {
        txString = msgStr;
        this.unpackHeader(msgStr.substring(0, headerLength));
    }

    protected void unpackHeader(String header) {
        int range = 0;
        pin = header.substring(0, range += PIN);
        messageSequence = header.substring(range, range += MESSAGE_SEQUENCE);
        branchCode = header.substring(range, range += BRANCH_CODE);
        userId = header.substring(range, range += USER_ID);
        messageVersion = header.substring(range, range += MESSAGE_VERSION);
        requestDate = header.substring(range, range += REQUEST_DATE);
        requestTime = header.substring(range, range += REQUEST_TIME);
        accountNo = header.substring(range, range += ACCOUNT_NO);
    }

    protected abstract void unpackBody();

    public String createResponseHeader() {
        StringBuffer sb = new StringBuffer();
        String pin = String.valueOf(Integer.parseInt(this.pin) + 1);
        this.requestDate = DateUtil.getSystemDate();
        this.requestTime = DateUtil.getSystemTime();
        sb.append(pin).append(messageSequence).append(branchCode).append(userId).append(messageVersion).append
                (requestDate).append(requestTime).append(accountNo).append(actionCode);
        return sb.toString();
    }

    public static BranchMessage parseMessage(String msgStr) throws FormatException {

        BranchMessage msg = null;
        try {
            switch (Integer.parseInt(msgStr.substring(0, 5))) {
                case 60200:   //BAL
                    msg = new ParserBAL(msgStr);
                    break;
                case 60100:   //Create NAC
                    msg = new ParserNAC(msgStr);
                    break;
                case 60102:   //update Row
                    msg = new ParserUpdateRow(msgStr);
                    break;
                case 60105:   //Get Account Status
                    msg = new ParserAccountStatus(msgStr);
                    break;
                case 60110:   //Update Account
                    msg = new ParserUpdateAccount(msgStr);
                    break;
                case 60115:   //Online Account
                    msg = new ParserOnlineAccount(msgStr);
                    break;
                case 60120:   //change Account status
                    msg = new ParserChangeAccountStatus(msgStr);
                    break;
                case 60125:   //Account List
                    msg = new ParserAccountList(msgStr);
                    break;
                case 60135:   //Reset Account
                    msg = new ParserResetAccount(msgStr);
                    break;
                case 60140:   //Get MINI Account Status
                    msg = new ParserAccountStatus(msgStr);
                    break;
                case 60142:   //Get MINI Account Status
                    msg = new ParserAccountTypeInquiry(msgStr);
                    break;
                case 60145:   //Create NAC Batch
                    msg = new ParserBatchNAC(msgStr);
                    break;
                case 60150:   //Service Status
                    msg = new ParserServiceStatus(msgStr);
                    break;
                case 60155:   //Block or UnBlock Report
                    msg = new ParserBlockReport(msgStr);
                    break;
                case 60160:   //ACCOUNT TO CARD
                    msg = new ParserAccountToCard(msgStr);
                    break;
                case 60165:   //Block or UnBlock Report(Simin)
                    msg = new ParserBlockReport(msgStr);
                    break;
                case 60170:   //ACCOUNT Block Report
                    msg = new ParserAccountBlockReport(msgStr);
                    break;
                case 60175:   //ACCOUNT Block Report (SIMIN)
                    msg = new ParserAccountBlockReport(msgStr);
                    break;
                case 60180:   //ACCOUNT Block Report (SIMIN)
                    msg = new ParserRemoveRow(msgStr);
                    break;
                case 60185:   //pinpad Account inquiry
                    msg = new ParserPinpadAccountInquiry(msgStr);
                    break;
                case 60190:   //change Account type
                    msg = new ParserChangeAccountType(msgStr);
                    break;
                case 60195:   //Account type inquiry
                    msg = new ParserAccountInquiry(msgStr);
                    break;
                case 60205:   //Statement
                    msg = new ParserStatement(msgStr);
                    break;
                case 60210:   //Deposit
                    msg = new ParserDeposit(msgStr);
                    break;
                case 60410:   //Deposit Reverse
                    msg = new ParserDepositReverse(msgStr);
                    break;
                case 60215:   //Withdrawal
                    msg = new ParserWithdraw(msgStr);
                    break;
                case 60415:   //Withdrawal Reverse
                    msg = new ParserWithdrawReverse(msgStr);
                    break;
                case 60220:   //Followup Message
                    msg = new ParserFollowup(msgStr);
                    break;
                case 60225:   //Transaction Count
                    msg = new ParserStatement(msgStr);
                    break;
                case 60230:   //Deposit_GIFTCARD
                    msg = new ParserDeposit4GiftCard(msgStr);
                    break;
                case 60420:   //Deposit giftCard Reverse
                    msg = new ParserDepositGiftCardReverse(msgStr);
                    break;
                case 60235:   //Withdrawal_GIFTCARD
                    msg = new ParserWithdrawGiftCard(msgStr);
                    break;
                case 60240:   //cancellation_GIFTCARD
                    msg = new ParsercCancellationGiftCard(msgStr);
                    break;
                case 60245:   //Discharge GIFTCARD
                    msg = new ParserDischargeGiftCard(msgStr);
                    break;
                case 60250:   //New Statement
                    msg = new ParserStatement(msgStr);
                    break;
                case 90120:   //SIMIN change account status
                    msg = new ParserSiminChangeStatus(msgStr);
                    break;
                case 90122:   //SIMIN change account status
                    msg = new ParserSiminDepositBlock(msgStr);
                    break;
                case 90105:   //SIMIN account Information
                    msg = new ParserAccountInfo(msgStr);
                    break;
                case 90150:   //SIMIN set service Status
                    msg = new ParserSiminServiceStatus(msgStr);
                    break;
                case 90100:   //SIMIN Remove Doc
                    msg = new ParserSiminDeleteDocument(msgStr);
                    break;
                case 60304:   //Verhoef
                    msg = new ParserIsValidVerhoef(msgStr);
                    break;
                case 60306:   //ExpenseVerhoef
                    msg = new ParserIsValidExpenseVerhoef(msgStr);
                    break;
                case 60300:   //remittance inquiry
                    msg = new ParserRemittanceInquiry(msgStr);
                    break;
                case 60302:   //withdraw remittance
                    msg = new ParserWithdrawRemittance(msgStr);
                    break;
                case 90110:   //Simin Statement
                    msg = new ParserSiminStatement(msgStr);
                    break;
                case 90118:   //SIMIN Create NAC
                    msg = new ParserNACSimin(msgStr);
                    break;
                case 60255:   //Withdraw ATM
                    msg = new ParserWithdrawATM(msgStr);
                    break;
                case 60455:   // withdraw ATM Reverse
                    msg = new ParserReverseWithdrawATM(msgStr);
                    break;
                case 60260:   //Deposit ATM
                    msg = new ParserDepositATM(msgStr);
                    break;
                case 60460:   //Deposit ATM Reverse
                    msg = new ParserReverseDepositATM(msgStr);
                    break;
                case 60270:   //Withdraw Wage
                    msg = new ParserWithdrawWage(msgStr);
                    break;
                case 60470:   //Withdraw Wage Reverse
                    msg = new ParserWithdrawWageReverse(msgStr);
                    break;
                case 60308:   // Inquiry ATM
                    msg = new ParserInquiryATM(msgStr);
                    break;
                case 22000:   //Register paya request
                    msg = new ParserRegisterPayaRequest(msgStr);
                    break;
                case 22010:   //Send paya request
                    msg = new ParserSendPayaRequest(msgStr);
                    break;
                case 22070:   //Get paya request
                    msg = new ParserGetPayaRequest(msgStr);
                    break;
                case 22090:   //inactive paya request
                    msg = new ParserInactivePayaRequest(msgStr);
                    break;
                case 22020:   //get paya request in due date
                    msg = new ParserGetPayaRequestInDueDate(msgStr);
                    break;
                case 22030:   //Delete paya request
                    msg = new ParserDeletePayaRequest(msgStr);
                    break;
                case 22080:   //Register paya request
                    msg = new ParserResendPayaRequest(msgStr);
                    break;
                case 75104:   //paya request report
                    msg = new ParserPayaRequestReport(msgStr);
                    break;
                case 90124:   //SIMIN Create ATM Nac
                    msg = new ParserCreateNacATM(msgStr);
                    break;
                case 90112:   //SIMIN Offline ATM
                    msg = new ParserOfflineATM(msgStr);
                    break;
                case 90114:   //SIMIN Change ATM Status
                    msg = new ParserChangeATMStatus(msgStr);
                    break;
                case 90126:   //SIMIN Change STATUS CBI
                    msg = new ParserSiminChangeStatusCBI(msgStr);
                    break;
                case 90128:   //SIMIN Change ATM branch
                    msg = new ParserChangeDeviceBranch(msgStr);
                    break;
                default:
                    log.error("ERROR:: in branchMessage : invalid pin");
                    throw new Exception();
            }
        } catch (NumberFormatException e) {
            throw new FormatException(e.getMessage());
        } catch (Exception e) {
            throw new FormatException(e.getMessage());
        }
        return msg;
    }


    public String getPin() {
        return pin;
    }

    public String getMessageSequence() {
        return messageSequence;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getDate_verhoef() {
        return date_verhoef;
    }

    public void setDate_verhoef(String date_verhoef) {
        this.date_verhoef = date_verhoef;
    }

    public String getCheque_number() {
        return cheque_number;
    }

    public void setCheque_number(String cheque_number) {
        this.cheque_number = cheque_number;
    }

    public String getAmount_verhoef() {
        return amount_verhoef;
    }

    public void setAmount_verhoef(String amount_verhoef) {
        this.amount_verhoef = amount_verhoef;
    }

    public String getId_num() {
        return id_num;
    }

    public void setId_num(String id_num) {
        this.id_num = id_num;
    }

    public String getIsvalid_verhoef() {
        return isvalid_verhoef;
    }

    public void setIsvalid_verhoef(String isvalid_verhoef) {
        this.isvalid_verhoef = isvalid_verhoef;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getTxDate() {
        return txDate;
    }

    public String getTxTime() {
        return txTime;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getActionCode() {
        return actionCode;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public void setActualBalance(String actualBalance) {
        this.actualBalance = actualBalance;
    }

    public String getActualBalance() {
        return actualBalance;
    }

    public void setBlockedAmount(String blockedAmount) {
        this.blockedAmount = blockedAmount;
    }

    public String getBlockedAmount() {
        return blockedAmount;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public String getTxString() {
        return txString;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getGender() {
        return gender;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getID_Number() {
        return ID_Number;
    }

    public String getID_SerialNumber() {
        return ID_SerialNumber;
    }

    public String getID_Series() {
        return ID_Series;
    }

    public String getID_IssueDate() {
        return ID_IssueDate;
    }

    public String getID_IssueCode() {
        return ID_IssueCode;
    }

    public String getID_IssuePlace() {
        return ID_IssuePlace;
    }

    public String getEn_FirstName() {
        return en_FirstName;
    }

    public String getEn_LastName() {
        return en_LastName;
    }

    public String getExt_IdNumber() {
        return ext_IdNumber;
    }

    public String getForeignCountryCode() {
        return foreignCountryCode;
    }

    public String getTel_Number1() {
        return tel_Number1;
    }

    public String getTel_Number2() {
        return tel_Number2;
    }

    public String getMob_Number() {
        return mob_Number;
    }

    public String getFax_Number() {
        return fax_Number;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getNationalCodeValid() {
        return nationalCodeValid;
    }

    public String getWithdrawType() {
        return withdrawType;
    }

    public String getAccountOpenerName() {
        return accountOpenerName;
    }

    public String getOwnerIndex() {
        return ownerIndex;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAccountGroup() {
        return accountGroup;
    }

    public String getStatementType() {
        return statementType;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public String getOrigMessageData() {
        return origMessageData;
    }

    public String getBalance() {
        return balance;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransDate() {
        return transDate;
    }

    public String getTransTime() {
        return transTime;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setOrigMessageData(String origMessageData) {
        this.origMessageData = origMessageData;
    }

    public String getAccountCount() {
        return accountCount;
    }

    public void setAccountCount(String accountCount) {
        this.accountCount = accountCount;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getBlockRow() {
        return blockRow;
    }

    public void setBlockRow(String blockRow) {
        this.blockRow = blockRow;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(String transactionCount) {
        this.transactionCount = transactionCount;
    }

    public String getCreditDebit() {
        return creditDebit;
    }

    public void setCreditDebit(String creditDebit) {
        this.creditDebit = creditDebit;
    }

    public String getTransactionMinimumAmount() {
        return transactionMinimumAmount;
    }

    public void setTransactionMinimumAmount(String transactionMinimumAmount) {
        transactionMinimumAmount = transactionMinimumAmount;
    }

    public String getTransactionMaximumAmount() {
        return transactionMaximumAmount;
    }

    public void setTransactionMaximumAmount(String transactionMaximumAmount) {
        transactionMaximumAmount = transactionMaximumAmount;
    }

    public String getTransactionDocumentNumber() {
        return transactionDocumentNumber;
    }

    public void setTransactionDocumentNumber(String transactionDocumentNumber) {
        transactionDocumentNumber = transactionDocumentNumber;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        transactionDescription = transactionDescription;
    }

    public String getFromSequence() {
        return fromSequence;
    }

    public void setFromSequence(String fromSequence) {
        this.fromSequence = fromSequence;
    }

    public String getIssuerBranchCode() {
        return issuerBranchCode;
    }

    public void setIssuerBranchCode(String issuerBranchCode) {
        this.issuerBranchCode = issuerBranchCode;
    }

    public String getEffactiveDate() {
        return effactiveDate;
    }

    public void setEffactiveDate(String effactiveDate) {
        this.effactiveDate = effactiveDate;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getHostCode() {
        return hostCode;
    }

    public void setHostCode(String hostCode) {
        this.hostCode = hostCode;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAccountGroup(String accountGroup) {
        this.accountGroup = accountGroup;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getOldNationalCode() {
        return oldNationalCode;
    }

    public void setOldNationalCode(String oldNationalCode) {
        this.oldNationalCode = oldNationalCode;
    }

    public String getOld_ext_IdNumber() {
        return old_ext_IdNumber;
    }

    public void setOld_ext_IdNumber(String old_ext_IdNumber) {
        this.old_ext_IdNumber = old_ext_IdNumber;
    }

    public String getPinpadBranch() {
        return pinpadBranch;
    }

    public void setPinpadBranch(String pinpadBranch) {
        this.pinpadBranch = pinpadBranch;
    }

    public String getIsPoshtiban() {
        return isPoshtiban;
    }

    public void setIsPoshtiban(String isPoshtiban) {
        this.isPoshtiban = isPoshtiban;
    }
    public String getTerminalCode() {
        return terminalCode;
    }

    public void setTerminalCode(String terminalCode) {
        this.terminalCode = terminalCode;
    }
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getOperationCodeFeeAmount() {
        return operationCodeFeeAmount;
    }

    public void setOperationCodeFeeAmount(String operationCodeFeeAmount) {
        this.operationCodeFeeAmount = operationCodeFeeAmount;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public void setExt_IdNumber(String ext_IdNumber) {
        this.ext_IdNumber = ext_IdNumber;
    }

    public static Log getLog() {
        return log;
    }

    public static void setLog(Log log) {
        BranchMessage.log = log;
    }

    public String getRemittanceMessage_Sequence() {
        return remittanceMessage_Sequence;
    }

    public void setRemittanceMessage_Sequence(String remittanceMessage_Sequence) {
        this.remittanceMessage_Sequence = remittanceMessage_Sequence;
    }

    public String getRemittanceStatus() {
        return remittanceStatus;
    }

    public void setRemittanceStatus(String remittanceStatus) {
        this.remittanceStatus = remittanceStatus;
    }

    public String getRemittanceFeeAmount() {
        return remittanceFeeAmount;
    }

    public void setRemittanceFeeAmount(String remittanceFeeAmount) {
        this.remittanceFeeAmount = remittanceFeeAmount;
    }

    public String getRemittanceAmount() {
        return remittanceAmount;
    }

    public void setRemittanceAmount(String remittanceAmount) {
        this.remittanceAmount = remittanceAmount;
    }

    public String getExpirDate_remittance() {
        return expirDate_remittance;
    }

    public void setExpirDate_remittance(String expirDate_remittance) {
        this.expirDate_remittance = expirDate_remittance;
    }

    public String getTelNumber_remittance() {
        return telNumber_remittance;
    }

    public void setTelNumber_remittance(String telNumber_remittance) {
        this.telNumber_remittance = telNumber_remittance;
    }

    public String getBirthDate_remittance() {
        return birthDate_remittance;
    }

    public void setBirthDate_remittance(String birthDate_remittance) {
        this.birthDate_remittance = birthDate_remittance;
    }

    public String getRemittanceDate() {
        return remittanceDate;
    }

    public void setRemittanceDate(String remittanceDate) {
        this.remittanceDate = remittanceDate;
    }

    public String getRemittance_request_no() {
        return remittance_request_no;
    }

    public void setRemittance_request_no(String remittance_request_no) {
        this.remittance_request_no = remittance_request_no;
    }

    public String getBranchCodeBody() {
        return branchCodeBody;
    }

    public void setBranchCodeBody(String branchCodeBody) {
        this.branchCodeBody = branchCodeBody;
    }

    public String getMaxATM_Balance() {
        return maxATM_Balance;
    }

    public void setMaxATM_Balance(String maxATM_Balance) {
        this.maxATM_Balance = maxATM_Balance;
    }

    public String getAvgDeficit() {
        return avgDeficit;
    }

    public void setAvgDeficit(String avgDeficit) {
        this.avgDeficit = avgDeficit;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBankSend() {
        return bankSend;
    }

    public void setBankSend(String bankSend) {
        this.bankSend = bankSend;
    }

    public String getBankRecv() {
        return bankRecv;
    }

    public void setBankRecv(String bankRecv) {
        this.bankRecv = bankRecv;
    }

    public String getSourceIban() {
        return sourceIban;
    }

    public void setSourceIban(String sourceIban) {
        this.sourceIban = sourceIban;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getDestIban() {
        return destIban;
    }

    public void setDestIban(String destIban) {
        this.destIban = destIban;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMeliCode() {
        return meliCode;
    }

    public void setMeliCode(String meliCode) {
        this.meliCode = meliCode;
    }

    public String getReciverName() {
        return reciverName;
    }

    public void setReciverName(String reciverName) {
        this.reciverName = reciverName;
    }

    public String getPayaDescription() {
        return payaDescription;
    }

    public void setPayaDescription(String payaDescription) {
        this.payaDescription = payaDescription;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSenderShahab() {
        return senderShahab;
    }

    public void setSenderShahab(String senderShahab) {
        this.senderShahab = senderShahab;
    }

    public String getReciverShahab() {
        return reciverShahab;
    }

    public void setReciverShahab(String reciverShahab) {
        this.reciverShahab = reciverShahab;
    }

    public String getReciverMeliCode() {
        return reciverMeliCode;
    }

    public void setReciverMeliCode(String reciverMeliCode) {
        this.reciverMeliCode = reciverMeliCode;
    }

    public String getReciverPostalCode() {
        return reciverPostalCode;
    }

    public void setReciverPostalCode(String reciverPostalCode) {
        this.reciverPostalCode = reciverPostalCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTrackId() {
        return TrackId;
    }

    public void setTrackId(String trackId) {
        TrackId = trackId;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getOpType() {
        return opType;
    }

    public void setOpType(String opType) {
        this.opType = opType;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getFiller() {
        return filler;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }

    public String getCbiFlag() {
        return cbiFlag;
    }

    public void setCbiFlag(String cbiFlag) {
        this.cbiFlag = cbiFlag;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAtmAccountType() {
        return atmAccountType;
    }

    public void setAtmAccountType(String atmAccountType) {
        this.atmAccountType = atmAccountType;
    }

    public String getAtmBranchCode() {
        return atmBranchCode;
    }

    public void setAtmBranchCode(String atmBranchCode) {
        this.atmBranchCode = atmBranchCode;
    }

    public String getExistDeviceBranchCode() {
        return existDeviceBranchCode;
    }

    public void setExistDeviceBranchCode(String existDeviceBranchCode) {
        this.existDeviceBranchCode = existDeviceBranchCode;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}

