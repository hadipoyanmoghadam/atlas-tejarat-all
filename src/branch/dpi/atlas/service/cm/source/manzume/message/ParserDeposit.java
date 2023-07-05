package branch.dpi.atlas.service.cm.source.manzume.message;


import branch.dpi.atlas.service.cm.source.branch.message.FormatException;
import dpi.atlas.util.Constants;

/**
 * User: F.Heydari
 * Date: NOV 1, 2022
 * Time: 2:23:23 PM
 */
public class ParserDeposit extends ManzumeMessage {
    public ParserDeposit(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.MANZUME_DEPOSIT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        transDate = txString.substring(headerLength , range = headerLength + TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        amount = txString.substring(range, range += AMOUNT);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        documentDescription = txString.substring(range, range += DOCUMENT_DESCRIPTION);
        operationCode = Constants.MANZUME_DEPOSIT_OP_CODE;
        id1 = txString.substring(range, range += ID1);
        id2 = txString.substring(range, range += ID2);
    }
}
