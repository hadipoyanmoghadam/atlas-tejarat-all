package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User:F.Heydari
 * Date:Nov ,12, 2019
 * Time: 10:05 AM
 */

public class ParserActiveSMS extends SMSMessage {

    public ParserActiveSMS(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SMS_ACTIVE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {


    }
}
