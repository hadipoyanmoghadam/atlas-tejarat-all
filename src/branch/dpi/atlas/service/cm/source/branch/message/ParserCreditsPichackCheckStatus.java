package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: App 29, 2022
 * Time: 12:12 PM
 */
public class ParserCreditsPichackCheckStatus extends CreditsMessage {

    public ParserCreditsPichackCheckStatus(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.CREDITS_PICHACK_CHECK_STATUS_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
            checkNumber = txString.substring(headerLength, headerLength + CHECK_NUMBER);
    }
}
