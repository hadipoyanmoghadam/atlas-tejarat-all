package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Dec 29, 2013
 * Time: 10:22:20 AM
 */
public class ParserResetAccount extends BranchMessage {
    public ParserResetAccount(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.RESET_ACCOUNT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        terminalId = txString.substring(headerLength, range = headerLength + TERMINAL_ID);
    }
}
