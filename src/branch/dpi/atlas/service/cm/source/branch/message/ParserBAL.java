package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: Apr 16, 2013
 * Time: 11:35:24 AM
 */
public class ParserBAL extends BranchMessage {

    public ParserBAL(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.BALANCE_REQUEST_LENGTH)
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
