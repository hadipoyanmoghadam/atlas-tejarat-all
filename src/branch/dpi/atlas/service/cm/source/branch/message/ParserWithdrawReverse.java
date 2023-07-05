package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: Jul 4, 2013
 * Time: 5:41:11 PM
 */
public class ParserWithdrawReverse extends BranchMessage {
    public ParserWithdrawReverse(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.WITHDRAW_REVERSE_REQUEST_LENGTH)
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
        documentDescription = txString.substring(range, range += DOCUMENT_DESCRIPTION);
        operationCode = txString.substring(range, range += OPERATION_CODE);
        nationalCode = txString.substring(range, range += NATIONAL_CODE);
        origMessageData = txString.substring(range, range += ORIG_MESSAGE_DATA);


    }
}
