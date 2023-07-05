package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Jun 20, 2015
 * Time: 12:17 PM
 */
public class ParserDischargeGiftCard extends BranchMessage {
    public ParserDischargeGiftCard(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.DISCHARGE_GIFTCARD_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        terminalId = txString.substring(headerLength, range = headerLength + TERMINAL_ID);
        cardNo = txString.substring(range, range += CARD_NO);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        nationalCode = txString.substring(range, range += IDENTITY_CODE).trim();
    }
}
