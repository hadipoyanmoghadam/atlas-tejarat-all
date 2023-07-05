package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Dec 23, 2019
 * Time: 02:02 PM
 */
public class ParserSiminDepositBlock extends BranchMessage {
    public ParserSiminDepositBlock(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SIMIN_DEPOSIT_BLOCK_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        accountStatus = txString.substring(headerLength, range =headerLength+ DEPOSIT_STATUS);
        blockRow = txString.substring(range, range += BLOCK_ROW);
        documentDescription = txString.substring(range, range += SIMIN_DOCUMENT_DESCRIPTION);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
    }
}
