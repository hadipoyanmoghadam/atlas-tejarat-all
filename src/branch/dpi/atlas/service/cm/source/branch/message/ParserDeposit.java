package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: May 26, 2013
 * Time: 5:51:39 PM
 */
public class ParserDeposit extends BranchMessage {
    public ParserDeposit(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.DEPOSIT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

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
        id1 = txString.substring(range, range += ID1);
    }
}
