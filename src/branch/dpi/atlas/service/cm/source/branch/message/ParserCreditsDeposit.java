package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Sep 04, 2017
 * Time: 10:46 AM
 */
public class ParserCreditsDeposit extends CreditsMessage {
    public ParserCreditsDeposit(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.CREDITS_DEPOSIT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        amount = txString.substring(headerLength, range = headerLength + AMOUNT);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        documentDescription = txString.substring(range, range += DOCUMENT_DESCRIPTION);
        operationCode = txString.substring(range, range += OPERATION_CODE);
        nationalCode = txString.substring(range, range += NATIONAL_CODE);
        id1 = txString.substring(range, range += ID1);
    }
}
