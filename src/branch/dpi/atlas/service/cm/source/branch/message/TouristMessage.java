package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 12:05:10 PM
 */
public abstract class TouristMessage {

    private static Log log = LogFactory.getLog(TouristMessage.class);
    public static final int headerLength = 58;
    protected String txString;

    //Message Header Fields
    //---------------------
    private String pin;                   //N5
    private String messageSequence;       //C12
    private String branchCode;            //C6
    private String userId;                //C6
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
    protected String requestType;           // Num(1)       1: Create   2: Update
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
    protected String fromSequence;             //Num(12)
    protected String response;
    protected String id1;              //Char(30)
    protected String transactionType;       // Char(1)
    protected String srcAccountNo;       // Char(13)
    protected String destAccountNo;       // Char(13)

    //Start ==> set field length
    public static final int PIN=5;
    public static final int MESSAGE_SEQUENCE=12;
    public static final int BRANCH_CODE=5;
    public static final int USER_ID=6;
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
    public static final int ACCOUNT_STATUS=1;
    public static final int REQUEST_TYPE=1;
    public static final int ACCOUNT_TYPE=1;
    public static final int OWNER_INDEX=2;
    public static final int ACCOUNT_GROUP=3;
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
    public static final int TRANSACTION_DOCUMENT_NUMBER=6;
    public static final int FROM_SEQUENCE=12;
    public static final int ISSUER_BRANCH_CODE=5;
    public static final int Effective_Date=8;
    public static final int Effective_Time=6;
    public static final int ID1=30;
    public static final int TRANSACTION_TYPE=1;

    //END ==> set field length


        public TouristMessage(String msgStr) {
        txString = msgStr;
        this.unpackHeader(msgStr.substring(0, headerLength));
    }

    protected void unpackHeader(String header) {
        int range = 0;
        pin = header.substring(0, range += PIN);
        messageSequence = header.substring(range, range += MESSAGE_SEQUENCE);
        branchCode = header.substring(range, range += BRANCH_CODE);
        userId = header.substring(range, range += USER_ID);
        requestDate = header.substring(range, range += REQUEST_DATE);
        requestTime = header.substring(range, range += REQUEST_TIME);
        cardNo = header.substring(range, range += CARD_NO);
    }

    protected abstract void unpackBody();

    public String createResponseHeader() {
        StringBuffer sb = new StringBuffer();
        String pin = String.valueOf(Integer.parseInt(this.pin) + 1);
        this.requestDate = DateUtil.getSystemDate();
        this.requestTime = DateUtil.getSystemTime();
        sb.append(pin).append(messageSequence).append(branchCode).append(userId).append
                (requestDate).append(requestTime).append(cardNo).append(actionCode);
        return sb.toString();
    }

    public static TouristMessage parseMessage(String msgStr) throws FormatException {

        TouristMessage msg = null;
        try {
            switch (Integer.parseInt(msgStr.substring(0, 5))) {
                case 90200:   //Balance_TouristCard
                    msg = new ParserTouristBAL(msgStr);
                    break;
                case 90205:   //cardStatement_TouristCard
                    msg = new ParserTouristCardStatement(msgStr);
                    break;
                case 90210:   //Charge_TouristCard
                    msg = new ParserTouristCardCharge(msgStr);
                    break;
                case 90215:   //DisCharge_TouristCard
                    msg = new ParserTouristCardDisCharge(msgStr);
                    break;
                case 90220:   //FollowUp_TouristCard
                    msg = new ParserTouristFollowup(msgStr);
                    break;
                case 90225:   //ChargeStatement_TouristCard
                    msg = new ParserTouristChargeStatement(msgStr);
                    break;
                case 90230:   //Revoke_TouristCard
                    msg = new ParserTouristCardRevoke(msgStr);
                    break;
                case 90255:   //Summery_report_TouristCard
                    msg = new ParserTouristSummaryReport(msgStr);
                    break;
                case 90260:   //Fund_Transfer_TouristCard
                    msg = new ParserTouristFundTransfer(msgStr);
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

    public void setPin(String pin) {
        this.pin = pin;
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

    public String getFromSequence() {
        return fromSequence;
    }

    public void setFromSequence(String fromSequence) {
        this.fromSequence = fromSequence;
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

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getSrcAccountNo() {
        return srcAccountNo;
    }

    public void setSrcAccountNo(String srcAccountNo) {
        this.srcAccountNo = srcAccountNo;
    }

    public String getDestAccountNo() {
        return destAccountNo;
    }

    public void setDestAccountNo(String destAccountNo) {
        this.destAccountNo = destAccountNo;
    }
}

