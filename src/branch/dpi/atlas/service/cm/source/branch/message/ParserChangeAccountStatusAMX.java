package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Oct 20, 2018
 * Time: 1:53 PM
 */
public class ParserChangeAccountStatusAMX extends AmxMessage {
    public ParserChangeAccountStatusAMX(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.CHANGE_ACCOUNT_STATUS_AMX_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        accountNo = txString.substring(headerLength, range = headerLength + ACCOUNT_NO);
        requestType = txString.substring(range, range += STATUS_TYPE);
        requestType=requestType.trim();
        nationalCode = txString.substring(range, range += NATIONAL_CODE);
        ext_IdNumber = txString.substring(range, range += EXT_ID_NUMBER);
        if (Long.parseLong(ext_IdNumber) == 0)
            ext_IdNumber = "";
    }
}
