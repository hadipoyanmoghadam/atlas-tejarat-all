package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Sep 04, 2017
 * Time: 10:57 AM
 */
public class ParserCreditsDepositReverse extends CreditsMessage {
    public ParserCreditsDepositReverse(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.CREDITS_DEPOSIT_REVERSE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    @Override
    protected void unpackBody() {
        int range = 0;
        amount = txString.substring(headerLength, range = headerLength + AMOUNT);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        documentDescription = txString.substring(range, range += DOCUMENT_DESCRIPTION);
        operationCode = txString.substring(range, range += OPERATION_CODE);
        nationalCode = txString.substring(range, range += NATIONAL_CODE);
        origMessageData = txString.substring(range, range += ORIG_MESSAGE_DATA);
    }
}
