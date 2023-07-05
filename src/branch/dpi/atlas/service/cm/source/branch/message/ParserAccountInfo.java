package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: July 7, 2015
 * Time: 11:06 PM
 */
public class ParserAccountInfo extends BranchMessage {

    public ParserAccountInfo(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SIMIN_ACCOUNT_INFO_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
    }
}
