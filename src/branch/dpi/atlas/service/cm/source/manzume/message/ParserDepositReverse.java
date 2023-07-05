package branch.dpi.atlas.service.cm.source.manzume.message;

import branch.dpi.atlas.service.cm.source.branch.message.FormatException;
import dpi.atlas.util.Constants;

/**
 * User: F. Heydari
 * Date: NOV 3, 2022
 * Time: 15:06:36 PM
 */
public class ParserDepositReverse extends ManzumeMessage {
    public ParserDepositReverse(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.MANZUME_DEPOSIT_REVERSE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    @Override
    protected void unpackBody() {
        int range = headerLength;
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        amount = txString.substring(range,  range +=   AMOUNT);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        documentDescription = txString.substring(range, range += DOCUMENT_DESCRIPTION);
        operationCode = Constants.MANZUME_REVERSAL_DEPOSIT_OP_CODE;
        origMessageData = txString.substring(range, range += ORIG_MESSAGE_DATA);
    }
}
