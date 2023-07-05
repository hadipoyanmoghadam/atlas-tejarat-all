package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: Apr 28, 2019
 * Time: 02:25 PM
 */
public class ParserAccountTypeInquiry extends BranchMessage {

    public ParserAccountTypeInquiry(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.ACCOUNT_TYPE_INQUIRY_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {

    }
}
