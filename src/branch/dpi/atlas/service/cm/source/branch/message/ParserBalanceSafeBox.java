package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Oct 19, 2021
 * Time: 1:37 PM
 */
public class ParserBalanceSafeBox extends AmxMessage {
    public ParserBalanceSafeBox(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.BALANCE_SAFE_BOX_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        srcAccount = txString.substring(headerLength, range = headerLength + ACCOUNT_NO);
    }
}
