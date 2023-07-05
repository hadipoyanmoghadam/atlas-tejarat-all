package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: May 30, 2015
 * Time: 11:00:20 AM
 */
public class ParserSiminChangeStatus extends BranchMessage {
    public ParserSiminChangeStatus(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SIMIN_CHANGE_STATUS_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        accountStatus = txString.substring(headerLength, range =headerLength+ ACCOUNT_STATUS);
        amount = txString.substring(range, range += AMOUNT);
        blockRow = txString.substring(range, range += BLOCK_ROW);
        documentDescription = txString.substring(range, range += SIMIN_DOCUMENT_DESCRIPTION);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
    }
}
