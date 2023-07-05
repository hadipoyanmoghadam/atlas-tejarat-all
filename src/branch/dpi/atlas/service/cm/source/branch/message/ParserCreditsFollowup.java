package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Sep 04, 2017
 * Time: 10:54 AM
 */
public class ParserCreditsFollowup extends CreditsMessage {
    public ParserCreditsFollowup(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.CREDITS_FOLLOWUP_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    @Override
    protected void unpackBody() {
        int range = 0;
        origMessageData = txString.substring(headerLength, range = headerLength + ORIG_MESSAGE_DATA);
    }
}
