package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Feb 1, 2020
 * Time: 04:54 PM
 */
public class ParserTouristFundTransfer extends TouristMessage {

    public ParserTouristFundTransfer(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.TOURIST_FUND_TRANSFER_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        srcAccountNo = txString.substring(headerLength, range = headerLength + ACCOUNT_NO);
        accountNo=srcAccountNo;
        destAccountNo = txString.substring(range, range += ACCOUNT_NO);
        amount = txString.substring(range, range += AMOUNT);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        documentNo = txString.substring(range, range += DOCUMENT_NO);

    }
}

