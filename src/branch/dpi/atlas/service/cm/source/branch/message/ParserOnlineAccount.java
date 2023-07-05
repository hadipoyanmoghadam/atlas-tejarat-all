package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: May 26, 2013
 * Time: 2:11:26 PM
 */
public class ParserOnlineAccount extends BranchMessage {
    public ParserOnlineAccount(String msgStr) throws FormatException {
        super(msgStr);
        if(msgStr.length()== Constants.ONLINE_ACCOUNT_REQUEST_LENGTH)
        unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody()
    {
        int range = 0;
        withdrawType = txString.substring(headerLength, range = headerLength + WITHDRAW_TYPE);
        accountOpenerName = txString.substring(range,range += ACCOUNT_OPENER_NAME);
        balance = txString.substring(range,range += BALANCE);
        accountStatus = txString.substring(range,range += ACCOUNT_STATUS);
    }
}
