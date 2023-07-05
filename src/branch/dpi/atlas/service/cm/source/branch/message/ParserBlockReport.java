package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: April 5, 2015
 * Time: 09:33 AM
 */
public class ParserBlockReport extends BranchMessage {

    public ParserBlockReport(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.BLOCK_REPORT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }


    protected void unpackBody() {
        int range = 0;
        requestType = txString.substring(headerLength, range = headerLength + REQUEST_TYPE);
    }
}
