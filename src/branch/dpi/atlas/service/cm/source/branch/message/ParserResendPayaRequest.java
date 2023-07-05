package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: July 28, 2020
 * Time: 3:07 PM
 */
public class ParserResendPayaRequest extends BranchMessage {

    public ParserResendPayaRequest(String msgStr) throws FormatException {
        super(msgStr);
        if(msgStr.length()== Constants.RESEND_PAYA_REQUEST_LENGTH)
        unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }



    protected  void unpackBody()
    {
    }
}
