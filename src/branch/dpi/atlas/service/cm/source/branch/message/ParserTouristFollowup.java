package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 08:40 AM
 */
public class ParserTouristFollowup extends TouristMessage {
    public ParserTouristFollowup(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.TOURIST_FOLLOWUP_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    @Override
    protected void unpackBody() {
        int range = 0;
        origMessageData = txString.substring(headerLength, range = headerLength + ORIG_MESSAGE_DATA);
        if (cardNo.equals(Constants.ZERO_CARD_NO))
            accountNo = txString.substring(range, range += ACCOUNT_NO);
    }
}
