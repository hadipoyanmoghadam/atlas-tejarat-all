package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: May 27, 2013
 * Time: 4:39:05 PM
 */
public class ParserFollowup extends BranchMessage {
    public ParserFollowup(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.FOLLOWUP_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    @Override
    protected void unpackBody() {
        int range = 0;
        terminalId = txString.substring(headerLength, range = headerLength + TERMINAL_ID);
        origMessageData = txString.substring(range, range += ORIG_MESSAGE_DATA);
    }
}
