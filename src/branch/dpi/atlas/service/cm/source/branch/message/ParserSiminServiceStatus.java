package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: Sep 17, 2014
 * Time: 10:40 AM
 */
public class ParserSiminServiceStatus extends BranchMessage {

    public ParserSiminServiceStatus(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SIMIN_SERVICE_STATUS_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }


    protected void unpackBody() {
        int range = 0;
        requestType = txString.substring(headerLength, range = headerLength + REQUEST_TYPE);
        terminalId = txString.substring(range, range += TERMINAL_ID);
        documentDescription = txString.substring(range, range += SIMIN_DOCUMENT_DESCRIPTION);
    }
}
