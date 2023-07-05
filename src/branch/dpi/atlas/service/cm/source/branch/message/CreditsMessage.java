package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 12:05:10 PM
 */
public abstract class CreditsMessage {

    private static Log log = LogFactory.getLog(CreditsMessage.class);
    public static final int headerLength = 70;
    protected String txString;

    //Message Header Fields
    //---------------------
    private String pin;                   //N5
    private String messageSequence;      //C12
    private String branchCode;           //C6
    private String userId;               //C6
    private String clientIpAddress;      //C15
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
    protected String accountGroup;          // Num(3)
    protected String statementType;         // Num(1)
    protected String nationalCode;          // Num(11)
    protected String ext_IdNumber;          // Num(10)
    protected String balance;               // Num(18), Signed
    protected String terminalId;            // Num(2)
    protected String amount;                // Num(18)  unsigned
    protected String transDate;             // Num(6)
    protected String transTime;             // Num(6)
    protected String documentNo;            // Num(6)
    protected String documentDescription;   // Char(30)
    protected String operationCode;         // Num(3)
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
    protected String id1;              //Char(30)
    protected String tx_pk;              //Char(14)
    protected String checkNumber;        //Char(6)
    protected String checkStatus;        //Char(1)
    protected String checkDescription;        //Char(70)

    //Start ==> set field length
    public static final int PIN=5;
    public static final int MESSAGE_SEQUENCE=12;
    public static final int BRANCH_CODE=5;
    public static final int USER_ID=6;
    public static final int CLIENT_IP_ADDRESS=15;
    public static final int REQUEST_DATE=8;
    public static final int REQUEST_TIME=6;
    public static final int ACCOUNT_NO=13;
    public static final int CARD_NO=16;
    public static final int TX_DATE=8;
    public static final int TX_TIME=6;
    public static final int ORIG_MESSAGE_DATA=26;
    public static final int ACTION_CODE=4;
    public static final int AVAILABLE_BALANCE=18;
    public static final int ACTUAL_BALANCE=18;
    public static final int REQUEST_TYPE=1;
    public static final int STATEMENT_TYPE=1;
    public static final int NATIONAL_CODE=11;
    public static final int EXT_ID_NUMBER=15;
    public static final int BALANCE=18;
    public static final int TERMINAL_ID=2;
    public static final int AMOUNT=18;
    public static final int TRANS_DATE=8;
    public static final int TRANS_TIME=6;
    public static final int DOCUMENT_NO=6;
    public static final int DOCUMENT_DESCRIPTION=40;
    public static final int EXTRA_INFO=200;
    public static final int OPERATION_CODE=3;
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
    public static final int ID1=30;
    public static final int TX_PK=14;
    public static final int CHECK_NUMBER=6;
    public static final int CHECK_STATUS=1;
    public static final int CHECK_DESCRIPTION=70;

    //END ==> set field length


        public CreditsMessage(String msgStr) {
        txString = msgStr;
        this.unpackHeader(msgStr.substring(0, headerLength));
    }

    protected void unpackHeader(String header) {
        int range = 0;
        pin = header.substring(0, range += PIN);
        messageSequence = header.substring(range, range += MESSAGE_SEQUENCE);
        branchCode = header.substring(range, range += BRANCH_CODE);
        userId = header.substring(range, range += USER_ID);
        clientIpAddress = header.substring(range, range += CLIENT_IP_ADDRESS);
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
        sb.append(pin).append(messageSequence).append(branchCode).append(userId).append
                (clientIpAddress).append(requestDate).append(requestTime).append(accountNo).append(actionCode);
        return sb.toString();
    }

    public static CreditsMessage parseMessage(String msgStr) throws FormatException {

        CreditsMessage msg = null;
        try {
            switch (Integer.parseInt(msgStr.substring(0, 5))) {
                case 90300:   //Balance
                    msg = new ParserCreditsBAL(msgStr);
                    break;
                case 90305:   //Statement
                    msg = new ParserCreditsStatement(msgStr);
                    break;
                case 90310:   //Deposit
                    msg = new ParserCreditsDeposit(msgStr);
                    break;
                case 90315:   //Withdrawal
                    msg = new ParserCreditsWithdraw(msgStr);
                    break;
                case 90320:   //FollowUp
                    msg = new ParserCreditsFollowup(msgStr);
                    break;
                case 90325:   //FollowUp
                    msg = new ParserCreditsPichackCheckStatus(msgStr);
                    break;
                case 90410:   //Deposit Reverse
                    msg = new ParserCreditsDepositReverse(msgStr);
                    break;
                case 90415:   //withdrawal Reverse
                    msg = new ParserCreditsWithdrawReverse(msgStr);
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

    public String getRequestDate() {
        return requestDate;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public String getCardNo() {
        return cardNo;
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

    public String getRequestType() {
        return requestType;
    }

   public String getNationalCode() {
        return nationalCode;
    }

    public String getExt_IdNumber() {
        return ext_IdNumber;
    }

    public String getStatementType() {
        return statementType;
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

    public String getTransactionMaximumAmount() {
        return transactionMaximumAmount;
    }

    public String getTransactionDocumentNumber() {
        return transactionDocumentNumber;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public String getFromSequence() {
        return fromSequence;
    }

    public void setFromSequence(String fromSequence) {
        this.fromSequence = fromSequence;
    }

    public void setIssuerBranchCode(String issuerBranchCode) {
        this.issuerBranchCode = issuerBranchCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
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

    public void setAccountGroup(String accountGroup) {
        this.accountGroup = accountGroup;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getTx_pk() {
        return tx_pk;
    }

    public void setTx_pk(String tx_pk) {
        this.tx_pk = tx_pk;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(String checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getCheckDescription() {
        return checkDescription;
    }

    public void setCheckDescription(String checkDescription) {
        this.checkDescription = checkDescription;
    }
}

