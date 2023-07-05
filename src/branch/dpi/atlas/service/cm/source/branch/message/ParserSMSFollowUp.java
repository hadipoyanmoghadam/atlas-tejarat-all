
package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User:F.Heydari
 * Date:Nov,12, 2019
 * Time: 10:05 AM
 */

public class ParserSMSFollowUp extends SMSMessage {

    public ParserSMSFollowUp(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SMS_FOLLOWUP_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {

        int range = headerLength;

        origMessageData = txString.substring(range, range = range + ORIGMASSAGEDATE);


    }
}

