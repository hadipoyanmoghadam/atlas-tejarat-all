package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: July 06, 2013
 * Time: 3:30:10 PM
 */
public class ParserAccountStatus extends BranchMessage {

    public ParserAccountStatus(String msgStr) throws FormatException {
        super(msgStr);
        if(msgStr.length()== Constants.ACCOUNT_STATUS_REQUEST_LENGTH)
        unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }



    protected  void unpackBody()
    {
        int range = 0;
        ownerIndex = txString.substring(headerLength, range = headerLength + OWNER_INDEX);
        nationalCode = txString.substring(range, range += NATIONAL_CODE);
        ext_IdNumber = txString.substring(range, range += EXT_ID_NUMBER);
        if (Long.parseLong(ext_IdNumber) == 0)
            ext_IdNumber = "";
    }
}
