package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: April 23, 2017
 * Time: 04:45 PM
 */
public class ParserTouristCardDisCharge extends TouristMessage {
    public ParserTouristCardDisCharge(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.TOURIST_CHARGE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        accountNo = txString.substring(headerLength, range = headerLength + ACCOUNT_NO);
        amount = txString.substring(range, range += AMOUNT);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
    }
}
