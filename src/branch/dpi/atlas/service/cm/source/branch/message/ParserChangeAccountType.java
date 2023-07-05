package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: May 4, 2019
 * Time: 4:38 PM
 */
public class ParserChangeAccountType extends BranchMessage {

    public ParserChangeAccountType(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.CHANGE_ACCOUNT_TYPE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }


    protected void unpackBody() {
        int range = 0;
        requestType = txString.substring(headerLength, range = headerLength + REQUEST_TYPE);
    }
}
