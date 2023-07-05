package branch.dpi.atlas.service.cm.source.manzume.message;

import branch.dpi.atlas.service.cm.source.branch.message.*;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class ManzumeMessage {

    private static Log log = LogFactory.getLog(ManzumeMessage.class);
    public static final int headerLength = 55;
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
//    protected String cardNo;                //C16

    //Message Body Fields
    //---------------------
    private String actionCode;            //Char(4)
    protected String address;            //Char(200)
    protected String postalCode;            //Char(10)
    protected String customerId;            //Char(10)
    protected String customerName;            //Char(200)
    protected String identificationCode;       //Char(15)
    protected String mobileNo;       //Char(11)
    protected String accountType;       //Char(1)
    protected String terminalId;            // Num(2)
    protected String origMessageData;       //Num(26)
    protected String txDate;                //C6
    protected String txTime;                //C6
    protected String amount;                // Num(18)  unsigned
    protected String transDate;             // Num(6)
    protected String transTime;             // Num(6)
    protected String documentNo;            // Num(6)
    protected String documentDescription;   // Char(30)
    protected String operationCode;         // Num(3)
    protected String id1;              //Char(30)
    protected String id2;              //Char(30)
    private String actualBalance;         //Num(18)

    //......................
    //Start ==> set field length
    public static final int PIN=5;
    public static final int MESSAGE_SEQUENCE=12;
    public static final int BRANCH_CODE=5;
    public static final int USER_ID=6;
    public static final int REQUEST_DATE=8;
    public static final int REQUEST_TIME=6;
    public static final int ACCOUNT_NO=13;
    public static final int ADDRESS=200;
    public static final int POSTAL_CODE=10;
    public static final int CUSTOMER_ID=10;
    public static final int CUSTOMER_NAME=200;
    public static final int IDENTIFICATION_CODE=15;
    public static final int MOBILE_NO=11;
    public static final int ACCOUNT_TYPE=1;
    public static final int TERMINAL_ID=2;
    public static final int ORIG_MESSAGE_DATA=26;
    public static final int TX_DATE=8;
    public static final int TX_TIME=6;
    public static final int CARD_NO=16;
    public static final int ID1=30;
    public static final int ID2=30;
    public static final int AMOUNT=18;
    public static final int TRANS_DATE=8;
    public static final int TRANS_TIME=6;
    public static final int DOCUMENT_NO=6;
    public static final int DOCUMENT_DESCRIPTION=40;
    public static final int ACTUAL_BALANCE=18;


    //END ==> set field length


    public ManzumeMessage(String msgStr) {
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
        accountNo = header.substring(range, range += ACCOUNT_NO);
    }

    protected abstract void unpackBody();

    public String createResponseHeader() {
        StringBuffer sb = new StringBuffer();
        String pin = String.valueOf(Integer.parseInt(this.pin) + 1);
        this.requestDate = DateUtil.getSystemDate();
        this.requestTime = DateUtil.getSystemTime();
        sb.append(pin).append(messageSequence).append(branchCode).append(userId)
                .append(requestDate).append(requestTime).append(accountNo).append(actionCode);

        return sb.toString();
    }

    public static ManzumeMessage parseMessage(String msgStr) throws FormatException {

        ManzumeMessage msg = null;
        try {
            switch (Integer.parseInt(msgStr.substring(0, 5))) {
                case 80105:   //ACCOUNT INFO
                    msg = new ParserAccountInfo(msgStr);
                    break;
                case 80210:   //Deposit
                    msg = new ParserDeposit(msgStr);
                    break;
                case 80410:   //reverseDeposit
                    msg = new ParserDepositReverse(msgStr);
                    break;
                case 80220:   //Followup Message
                    msg = new ParserFollowup(msgStr);
                    break;
                default:
                    log.error("ERROR:: in ManzumeMessage : invalid pin");
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

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getMessageSequence() {
        return messageSequence;
    }

    public void setMessageSequence(String messageSequence) {
        this.messageSequence = messageSequence;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getTxString() {
        return txString;
    }

    public void setTxString(String txString) {
        this.txString = txString;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getOrigMessageData() {
        return origMessageData;
    }

    public void setOrigMessageData(String origMessageData) {
        this.origMessageData = origMessageData;
    }

    public String getTxDate() {
        return txDate;
    }

    public void setTxDate(String txDate) {
        this.txDate = txDate;
    }

    public String getTxTime() {
        return txTime;
    }

    public void setTxTime(String txTime) {
        this.txTime = txTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
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

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public String getId1() {
        return id1;
    }

    public void setId1(String id1) {
        this.id1 = id1;
    }

    public String getId2() {
        return id2;
    }

    public void setId2(String id2) {
        this.id2 = id2;
    }

    public String getActualBalance() {
        return actualBalance;
    }

    public void setActualBalance(String actualBalance) {
        this.actualBalance = actualBalance;
    }
}

