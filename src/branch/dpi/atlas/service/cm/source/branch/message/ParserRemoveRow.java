package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: July 24, 2017
 * Time: 12:30 PM
 */
public class ParserRemoveRow extends BranchMessage {

    public ParserRemoveRow(String msgStr) throws FormatException {
        super(msgStr);
        if(msgStr.length()== Constants.REMOVE_ROW_REQUEST_LENGTH)
        unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }



    protected  void unpackBody()
    {
        int range = 0;
        nationalCode = txString.substring(headerLength, range = headerLength + NATIONAL_CODE);
        ext_IdNumber = txString.substring(range, range += EXT_ID_NUMBER);
        if (Long.parseLong(ext_IdNumber) == 0)
            ext_IdNumber = "";
    }
}
