package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Dec 28, 2020
 * Time: 2:44 PM
 */
public class ParserWithdrawWageReverse extends BranchMessage {
    public ParserWithdrawWageReverse(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.WITHDRAW_WAGE_REVERSE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    @Override
    protected void unpackBody() {
        int range = 0;
        terminalId = txString.substring(headerLength, range = headerLength + TERMINAL_ID);
        amount = txString.substring(range, range += AMOUNT);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        origMessageData = txString.substring(range, range += ORIG_MESSAGE_DATA);


    }
}
