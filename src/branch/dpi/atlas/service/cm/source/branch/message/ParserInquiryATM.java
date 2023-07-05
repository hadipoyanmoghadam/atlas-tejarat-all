package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: F.Heydari
 * Date: August 24 2019
 * Time: 10:25 AM
 */
public class ParserInquiryATM extends BranchMessage {
    public ParserInquiryATM(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.INQUIRY_ATM_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {

    }
}
