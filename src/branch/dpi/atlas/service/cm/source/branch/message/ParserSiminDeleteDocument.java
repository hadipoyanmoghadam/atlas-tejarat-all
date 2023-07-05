package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Feb 13, 2019
 * Time: 2:30 PM
 */
public class ParserSiminDeleteDocument extends BranchMessage {
    public ParserSiminDeleteDocument(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SIMIN_REMOVE_DOCUMENT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        amount = txString.substring(headerLength, range =headerLength+ AMOUNT);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        requestType = txString.substring(range, range += REQUEST_TYPE);
        transDate= txString.substring(range, range += TRANS_DATE);
        transTime= txString.substring(range, range += TRANS_TIME);
        operationCode= txString.substring(range, range += OPERATION_CODE);
        terminalCode= txString.substring(range, range += TERMINAL_ID);
        issuerBranchCode= txString.substring(range, range += BRANCH_CODE);
        fromSequence = txString.substring(range, range += FROM_SEQUENCE);
    }
}
