package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: April 23, 2017
 * Time: 04:40 PM
 */
public class ParserTouristBAL extends TouristMessage {

    public ParserTouristBAL(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.TOURIST_BALANCE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        if (cardNo.equals(Constants.ZERO_CARD_NO))
            accountNo = txString.substring(headerLength, headerLength + ACCOUNT_NO);
    }
}
