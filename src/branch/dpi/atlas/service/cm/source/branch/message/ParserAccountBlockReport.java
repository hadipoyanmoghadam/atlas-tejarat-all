package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: Dec 5, 2015
 * Time: 12:23 PM
 */
public class ParserAccountBlockReport extends BranchMessage {

    public ParserAccountBlockReport(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.ACCOUNT_BLOCK_REPORT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }


    protected void unpackBody() {
    }
}
