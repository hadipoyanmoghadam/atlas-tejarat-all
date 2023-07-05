package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: July 10, 2019
 * Time: 1:37 PM
 */
public class ParserFTRSafeBox extends AmxMessage {
    public ParserFTRSafeBox(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.FUND_TRANSFER_REVERSE_SAFE_BOX_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        srcAccount = txString.substring(headerLength, range = headerLength + ACCOUNT_NO);
        destAccount = txString.substring(range, range +=  ACCOUNT_NO);
        amount = txString.substring(range, range += AMOUNT);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        documentDescription = txString.substring(range, range += DOCUMENT_DESCRIPTION);
        requestType=txString.substring(range, range += TRANSACTION_TYPE);
        origMessageData = txString.substring(range, range += ORIG_MESSAGE_DATA);


    }
}
