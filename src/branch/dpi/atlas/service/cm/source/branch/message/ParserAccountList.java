package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: July 13, 2013
 * Time: 9:46 AM
 */
public class ParserAccountList extends BranchMessage {

    public ParserAccountList(String msgStr) throws FormatException {
        super(msgStr);
        if(msgStr.length()== Constants.ACCOUNT_LIST_REQUEST_LENGTH)
        unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }



    protected  void unpackBody()
    {
        int range = 0;
        nationalCode = txString.substring(headerLength, range = headerLength + NATIONAL_CODE);
    }
}
