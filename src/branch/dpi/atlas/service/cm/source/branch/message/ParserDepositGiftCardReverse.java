package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Jun 20, 2015
 * Time: 10:27 AM
 */
public class ParserDepositGiftCardReverse extends BranchMessage {
    public ParserDepositGiftCardReverse(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.DEPOSIT_GIFTCARD_REVERSE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    @Override
    protected void unpackBody() {
        int range = 0;
        terminalId = txString.substring(headerLength, range = headerLength + TERMINAL_ID);
        amount = txString.substring(range, range += AMOUNT);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        documentDescription = txString.substring(range, range += DOCUMENT_DESCRIPTION);
        operationCode = txString.substring(range, range += OPERATION_CODE);
        nationalCode = txString.substring(range, range += IDENTITY_CODE).trim();
        origMessageData = txString.substring(range, range += ORIG_MESSAGE_DATA);
        requestType = txString.substring(range, range += REQUEST_TYPE);  //0= single card 1=batch card
    }
}
