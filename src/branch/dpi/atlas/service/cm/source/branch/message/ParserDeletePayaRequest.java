package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: July 28, 2020
 * Time: 3:06 PM
 */
public class ParserDeletePayaRequest extends BranchMessage {

    public ParserDeletePayaRequest(String msgStr) throws FormatException {
        super(msgStr);
        if(msgStr.length()== Constants.DELETE_PAYA_REQUEST_LENGTH)
        unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }



    protected  void unpackBody()
    {
        int range = 0;
        documentNo = txString.substring(headerLength, range = headerLength + DOCUMENT_NO);
        dueDate = txString.substring(range, range += DATE);
    }
}
