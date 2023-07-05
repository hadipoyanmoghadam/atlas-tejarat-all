package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Apr 11, 2015
 * Time: 10:05 AM
 */
public class ParserAccountToCard extends BranchMessage {

    public ParserAccountToCard(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.ACCOUNT_TO_CARD_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        if (accountNo.equals(Constants.ZERO_ACCOUNT_NO)){
            cardNo = txString.substring(headerLength, range = headerLength + CARD_NO2);
            if(cardNo.startsWith(Constants.BANKE_TEJARAT_BIN))
                cardNo=cardNo+"000";
            nationalCode = txString.substring(range, range += NATIONAL_CODE);
            ext_IdNumber = txString.substring(range, range += EXT_ID_NUMBER);
            if (Long.parseLong(ext_IdNumber) == 0)
                ext_IdNumber = "";
        }else{
            range=headerLength + CARD_NO2;
            nationalCode = txString.substring(range, range += NATIONAL_CODE);
            ext_IdNumber = txString.substring(range, range += EXT_ID_NUMBER);
            if (Long.parseLong(ext_IdNumber) == 0)
                ext_IdNumber = "";
        }
    }
}
