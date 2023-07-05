
package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * user:F.Heydari
 * Date:Nov,12, 2019
 * Time: 10:05 AM
 */

public class ParserSMSWage extends SMSMessage {

    public ParserSMSWage(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SMS_WAGE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {

        int range = headerLength;
        transactionDate = txString.substring(range, range = range + TRANSACTIONDATE);
        transactionTime = txString.substring(range, range = range + TRANSACTIONTIME);

    }
}