package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Nov 09, 2013
 * Time: 11:00:20 AM
 */
public class ParserChangeAccountStatus extends BranchMessage {
    public ParserChangeAccountStatus(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.CHANGE_ACCOUNT_STATUS_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        terminalId = txString.substring(headerLength, range = headerLength + TERMINAL_ID);
        accountStatus = txString.substring(range, range += ACCOUNT_STATUS);
        amount = txString.substring(range, range += AMOUNT);
        blockRow = txString.substring(range, range += BLOCK_ROW);
        documentDescription = txString.substring(range, range += BLOCK_DESCRIPTION);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
    }
}
