package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: May 4, 2019
 * Time: 4:42 PM
 */
public class ParserAccountInquiry extends BranchMessage {

    public ParserAccountInquiry(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.ACCOUNT_INQUIRY_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }


    protected void unpackBody() {
    }
}
