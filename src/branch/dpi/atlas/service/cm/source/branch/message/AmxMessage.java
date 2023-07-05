package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: R.Nasiri
 * Date: Dec 5, 2018
 * Time: 11:34 AM
 */
public abstract class AmxMessage {

    private static Log log = LogFactory.getLog(AmxMessage.class);
    public static final int headerLength = 38;
    protected String txString;

    //Message Header Fields
    //---------------------
    private String pin;                   //N5
    private String messageSequence;       //C12
    private String branchCode;            //C6
    private String requestDate;           //C6
    private String requestTime;           //C6
    protected String accountNo;             //C13
    //Message Body Fields
    //---------------------
    protected String terminalId;                //C2
    protected String txDate;                //C6
    protected String txTime;                //C6
    private String actionCode;            //Char(4)
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
    protected String oldNationalCode;       // Num(11)
    protected String old_ext_IdNumber;      // Num(15)
    protected String emailAddress;              //Char(50)
    protected String origMessageData;              //Char(24)
    protected String srcAccount;              //Char(13)
    protected String destAccount;              //Char(13)
    protected String amount;              //nuber(18)
    protected String availableBalance;    //nuber(18)
    protected String actualBalance;              //nuber(18)

    //Start ==> set field length
    public static final int PIN=5;
    public static final int MESSAGE_SEQUENCE=12;
    public static final int BRANCH_CODE=5;
    public static final int REQUEST_DATE=8;
    public static final int REQUEST_TIME=6;
    public static final int ACCOUNT_NO=13;
    public static final int TX_DATE=6;
    public static final int TX_TIME=6;
    public static final int ACTION_CODE=4;
    public static final int ACCOUNT_STATUS=1;
    public static final int REQUEST_TYPE=1;
    public static final int TRANSACTION_TYPE=2;
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
    public static final int E_MAIL=50;
    public static final int STATUS_TYPE=2;
    public static final int TERMINAL_ID=2;
    public static final int AMOUNT=18;
    protected String documentNo;            // Num(6)
    protected String documentDescription;   // Char(40)
    public static final int DOCUMENT_NO=6;
    public static final int DOCUMENT_DESCRIPTION=40;
    public static final int ORIG_MESSAGE_DATA=26;

    //END ==> set field length


        public AmxMessage(String msgStr) {
        txString = msgStr;
        this.unpackHeader(msgStr.substring(0, headerLength));
    }

    protected void unpackHeader(String header) {
        int range = 0;
        pin = header.substring(0, range += PIN);
        messageSequence = header.substring(range, range += MESSAGE_SEQUENCE);
        branchCode = header.substring(range, range += BRANCH_CODE);
        requestDate = header.substring(range, range += REQUEST_DATE);
        requestTime = header.substring(range, range += REQUEST_TIME);
        terminalId = header.substring(range, range += TERMINAL_ID);
    }

    protected abstract void unpackBody();

    public String createResponseHeader() {
        StringBuffer sb = new StringBuffer();
        String pin = String.valueOf(Integer.parseInt(this.pin) + 1);
        this.requestDate = DateUtil.getSystemDate();
        this.requestTime = DateUtil.getSystemTime();
        sb.append(pin).append(messageSequence).append(branchCode).append
                (requestDate).append(requestTime).append(terminalId).append(actionCode);
        return sb.toString();
    }

    public static AmxMessage parseMessage(String msgStr) throws FormatException {

        AmxMessage msg = null;
        try {
            switch (Integer.parseInt(msgStr.substring(0, 5))) {
                case 90500:   //Create or update NAC
                    msg = new ParserAMXNAC(msgStr);
                    break;
                case 90502:   //Fund Transfer Safe Deposit Box
                    msg = new ParserFTSafeBox(msgStr);
                    break;
                case 90602:   //Fund Transfer Reverse Safe Deposit Box
                    msg = new ParserFTRSafeBox(msgStr);
                    break;
                case 90504:   //Follow Up Safe Deposit Box
                    msg = new ParserFollowUpSafeBox(msgStr);
                    break;
                case 90506:   //Balance Safe Deposit Box
                    msg = new ParserBalanceSafeBox(msgStr);
                    break;
                case 90520:   //conditional/unconditional revoke or revival or revoke inquiry
                    msg = new ParserChangeAccountStatusAMX(msgStr);
                    break;
                default:
                    log.error("ERROR:: in AmxMessage : invalid pin");
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

     public String getRequestDate() {
        return requestDate;
    }

    public String getRequestTime() {
        return requestTime;
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

    public String getBalance() {
        return balance;
    }

    public String getEmailAddress() {
        return emailAddress;
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

    public String getOldNationalCode() {
        return oldNationalCode;
    }

    public String getOld_ext_IdNumber() {
        return old_ext_IdNumber;
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

    public String getSrcAccount() {
        return srcAccount;
    }

    public void setSrcAccount(String srcAccount) {
        this.srcAccount = srcAccount;
    }

    public String getDestAccount() {
        return destAccount;
    }

    public void setDestAccount(String destAccount) {
        this.destAccount = destAccount;
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
}

