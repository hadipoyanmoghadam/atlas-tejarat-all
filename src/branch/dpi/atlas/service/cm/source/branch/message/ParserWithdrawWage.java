package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Dec 28, 2020
 * Time: 2:27 PM
 */
public class ParserWithdrawWage extends BranchMessage {
    public ParserWithdrawWage(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.WITHDRAW_WAGE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    @Override
    protected void unpackBody() {
        int range = 0;
        terminalId = txString.substring(headerLength, range = headerLength + TERMINAL_ID);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        amount = txString.substring(range, range += AMOUNT);
        opType = txString.substring(range, range += OPERATION_TYPE);
        groupType = txString.substring(range, range += GROUP_TYPE);
        groupNo = txString.substring(range, range += GROUP_NUMBER);
        filler = txString.substring(range, range += FILER);
    }
}
