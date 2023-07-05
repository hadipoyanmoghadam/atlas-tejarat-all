package branch.dpi.atlas.service.cm.source.manzume.message;

import branch.dpi.atlas.service.cm.source.branch.message.FormatException;
import dpi.atlas.util.Constants;

/**
 * User: F.Heydari
 * Date: NOV 3, 2022
 * Time: 15:17:05 PM
 */
public class ParserFollowup extends ManzumeMessage {
    public ParserFollowup(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.MANZUME_FOLLOWUP_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    @Override
    protected void unpackBody() {
        int range = headerLength;
        origMessageData = txString.substring(range, range = range +ORIG_MESSAGE_DATA);
    }
}
