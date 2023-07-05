package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: May 26, 2013
 * Time: 2:01:09 PM
 */
public class ParserUpdateAccount  extends BranchMessage {

    public ParserUpdateAccount(String msgStr) throws FormatException {
        super(msgStr.substring(0, BranchMessage.headerLength));
        txString = msgStr;
        if(msgStr.length()== Constants.UPDATE_ACCOUNT_REQUEST_LENGTH)
        unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected  void unpackBody()
    {
        int range = 0;
        withdrawType = txString.substring(headerLength, range = headerLength + WITHDRAW_TYPE);
        accountOpenerName = txString.substring(range, range +=ACCOUNT_OPENER_NAME);
        address2 = txString.substring(range, range +=ADDRESS2);
    }
}
