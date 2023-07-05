package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Sep 04, 2017
 * Time: 10:37 AM
 */
public class ParserCreditsBAL extends CreditsMessage {

    public ParserCreditsBAL(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.CREDITS_BALANCE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        // Body of Balance Request may include card number
        if (accountNo.equals(Constants.ZERO_ACCOUNT_NO))
            cardNo = txString.substring(headerLength, headerLength + CARD_NO);
    }
}
