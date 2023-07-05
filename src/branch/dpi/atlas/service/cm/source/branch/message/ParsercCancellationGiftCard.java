package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Jun 20, 2015
 * Time: 9:33 AM
 */
public class ParsercCancellationGiftCard extends BranchMessage {
    public ParsercCancellationGiftCard(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.CANCELLATION_GIFTCARD_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        terminalId = txString.substring(headerLength, range = headerLength + TERMINAL_ID);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        transDate = txString.substring(range, range += TRANS_DATE);
        nationalCode = txString.substring(range, range += IDENTITY_CODE).trim();
    }
}
