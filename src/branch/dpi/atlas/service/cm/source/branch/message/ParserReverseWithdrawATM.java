package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: F.Heydari
 * Date: August 24 2019
 * Time: 10:20 AM
 */
public class ParserReverseWithdrawATM extends BranchMessage {
    public ParserReverseWithdrawATM(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.REVERSE_WITHDRAW_ATM_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = headerLength;
        terminalId = txString.substring(headerLength, range += TERMINAL_ID);
        amount = txString.substring(range, range += AMOUNT);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        documentDescription = txString.substring(range, range += DOCUMENT_DESCRIPTION);
        operationCode = txString.substring(range, range += OPERATION_CODE);
        origMessageData = txString.substring(range, range += ORIG_MESSAGE_DATA);
    }
}
