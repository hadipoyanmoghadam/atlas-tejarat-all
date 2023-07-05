package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: July 10, 2019
 * Time: 1:37 PM
 */
public class ParserFollowUpSafeBox extends AmxMessage {
    public ParserFollowUpSafeBox(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.FOLLOW_UP_SAFE_BOX_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        srcAccount = txString.substring(headerLength, range = headerLength + ACCOUNT_NO);
        origMessageData = txString.substring(range, range += ORIG_MESSAGE_DATA);


    }
}
