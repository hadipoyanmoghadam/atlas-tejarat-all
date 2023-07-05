package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: Sep 25, 2017
 * Time: 10:25 AM
 */
public class ParserPinpadAccountInquiry extends BranchMessage {

    public ParserPinpadAccountInquiry(String msgStr) throws FormatException {
        super(msgStr);
        if(msgStr.length()== Constants.PINPAD_ACCOUNT_REQUEST_LENGTH)
        unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }


    protected  void unpackBody()
    {
        int range = 0;
        pinpadBranch = txString.substring(headerLength, range = headerLength + BRANCH_CODE);
    }
}
