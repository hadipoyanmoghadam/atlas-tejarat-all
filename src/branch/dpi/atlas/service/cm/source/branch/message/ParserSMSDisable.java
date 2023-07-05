package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User:F.Heydari
 * Date: Nov ,12, 2019
 * Time: 10:05 AM
 */

public class ParserSMSDisable extends SMSMessage {

    public ParserSMSDisable(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SMS_DISABLE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {


    }
}
