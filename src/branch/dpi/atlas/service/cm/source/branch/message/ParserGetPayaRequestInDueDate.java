package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: July 28, 2020
 * Time: 3:05 PM
 */
public class ParserGetPayaRequestInDueDate extends BranchMessage {

    public ParserGetPayaRequestInDueDate(String msgStr) throws FormatException {
        super(msgStr);
        if(msgStr.length()== Constants.GET_IN_DUE_DATE_PAYA_REQUEST_LENGTH)
        unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }



    protected  void unpackBody()
    {
        int range = 0;
        dueDate = txString.substring(headerLength, range = headerLength + DUE_DATE);
    }
}
