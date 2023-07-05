package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: F.Heydari
 * Date: Nov 20, 2019
 * Time: 12:05:10 PM
 */
public abstract class SMSMessage {

    private static Log log = LogFactory.getLog(CreditsMessage.class);
    public static final int headerLength = 50;
    protected String txString;

    //Message Header Fields
    //---------------------
    private String pin;                   //N5
    private String messageSequence;      //C12
    private String userId;               //C6
    private String requestDate;           //C6
    private String requestTime;           //C6
    protected String accountNo;             //C13

    //Message Body Fields
    //---------------------
    protected String requestId;              //Char(12)
    private String availableBalance;      //Char(18)
    private String actualBalance;         //Char(18)
    private String actionCode;            //Char(4)
    protected String resultDescription;
    protected String transactionDate;       //Char(8)
    protected String transactionTime;    //Char(6)
    protected String origMessageData;       //Num(24)
    protected String terminalId;                //C2
    protected String amount;                // Num(18)  unsigned
    protected String documentNo;            // Num(6)
    protected String documentDescription;   // Char(40)


    //Start ==> set field length
    public static final int PIN = 5;
    public static final int MESSAGE_SEQUENCE = 12;
    public static final int USER_ID = 6;
    public static final int REQUEST_DATE = 8;
    public static final int REQUEST_TIME = 6;
    public static final int ACCOUNT_NO = 13;

    public static final int REQUESTID = 12;
    public static final int AVAILABLEBALANCE = 18;
    public static final int ACTUALBALANCE = 18;
    public static final int ACTIONCODE = 4;
    public static final int TRANSACTIONDATE = 8;
    public static final int TRANSACTIONTIME = 6;
    public static final int ORIGMASSAGEDATE = 26;
    public static final int TERMINAL_ID = 2;
    public static final int AMOUNT = 18;
    public static final int DOCUMENT_NO = 6;
    public static final int DOCUMENT_DESCRIPTION = 40;

    //END ==> set field length


    public SMSMessage(String msgStr) {
        txString = msgStr;
        this.unpackHeader(msgStr.substring(0, headerLength));
    }

    protected void unpackHeader(String header) {
        int range = 0;
        pin = header.substring(0, range += PIN);
        userId = header.substring(range, range += USER_ID);
        messageSequence = header.substring(range, range += MESSAGE_SEQUENCE);
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
        sb.append(pin).append(userId).append(messageSequence).append(requestDate).append(requestTime).append(accountNo).append(actionCode);
        return sb.toString();
    }

    public static SMSMessage parseMessage(String msgStr) throws FormatException {

        SMSMessage msg = null;
        try {
            switch (Integer.parseInt(msgStr.substring(0, 5))) {
                case 90700:   //Balance
                    msg = new ParserSMSBalance(msgStr);
                    break;
                case 90702:   //Active sms with Wage
                    msg = new ParserSMSWage(msgStr);
                    break;

                case 90704: //Active sms with out wage
                    msg = new ParserActiveSMS(msgStr);
                    break;
                case 90706:   //Disable sms
                    msg = new ParserSMSDisable(msgStr);
                    break;
                case 90708:   //FollowUp sms
                    msg = new ParserSMSFollowUp(msgStr);
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


    public static int getHeaderLength() {
        return headerLength;
    }

    public String getTxString() {
        return txString;
    }

    public void setTxString(String txString) {
        this.txString = txString;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getMessageSequence() {
        return messageSequence;
    }

    public void setMessageSequence(String messageSequence) {
        this.messageSequence = messageSequence;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getActualBalance() {
        return actualBalance;
    }

    public void setActualBalance(String actualBalance) {
        this.actualBalance = actualBalance;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getOrigMessageData() {
        return origMessageData;
    }

    public void setOrigMessageData(String origMessageData) {
        this.origMessageData = origMessageData;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }
}

