package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

/**
 * User: R.Nasiri
 * Date: Sep 04, 2017
 * Time: 10:42 AM
 */
public class ParserCreditsStatement extends CreditsMessage {

    public ParserCreditsStatement(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.CREDITS_STATEMENT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        String fromDate="";
        String toDate="";
        String fromTime="";
        String toTime="";
        String transactionMinimumAmount="";
        String transactionMaximumAmount="";
        String transactionDocumentNumber="";
        String transactionOperationCode="";
        String fromSequence="";
        int range = 0;
        if (accountNo.equals(Constants.ZERO_ACCOUNT_NO))
            cardNo = txString.substring(headerLength, range = headerLength + CARD_NO);
        else
            range = headerLength + CARD_NO;
        statementType = txString.substring(range, range += STATEMENT_TYPE);
        fromDate = txString.substring(range, range += FROM_DATE);
        this.fromDate= ISOUtil.isZero(fromDate) ? "" : fromDate;
        toDate = txString.substring(range, range += TO_DATE);
        this.toDate= ISOUtil.isZero(toDate) ? "" : toDate;
        fromTime = txString.substring(range, range += FROM_TIME);
        this.fromTime= ISOUtil.isZero(fromTime) ? "" : fromTime;
        toTime = txString.substring(range, range += TO_TIME);
        this.toTime= ISOUtil.isZero(toTime)? "":toTime;
        transactionCount = txString.substring(range, range += TRANSACTION_COUNT);
        creditDebit = txString.substring(range, range += CREDIT_DEBIT).trim();
        transactionMinimumAmount = txString.substring(range, range += TRANSACTION_MIN_AMOUNT);
        this.transactionMinimumAmount= ISOUtil.isZero(transactionMinimumAmount)?"":transactionMinimumAmount;
        transactionMaximumAmount = txString.substring(range, range += TRANSACTION_MAX_AMOUNT);
        this.transactionMaximumAmount= ISOUtil.isZero(transactionMaximumAmount)?"":transactionMaximumAmount;
        transactionDocumentNumber = txString.substring(range, range += TRANSACTION_DOCUMENT_NUMBER);
        this.transactionDocumentNumber= ISOUtil.isZero(transactionDocumentNumber)?"":transactionDocumentNumber;
        transactionDescription = txString.substring(range, range += TRANSACTION_DESCRIPTION).trim();
        transactionOperationCode = txString.substring(range, range += OPERATION_CODE);
        this.operationCode= ISOUtil.isZero(transactionOperationCode)?"":transactionOperationCode;
        fromSequence = txString.substring(range, range += FROM_SEQUENCE);
        this.fromSequence= ISOUtil.isZero(fromSequence)?"":fromSequence;
    }
}

