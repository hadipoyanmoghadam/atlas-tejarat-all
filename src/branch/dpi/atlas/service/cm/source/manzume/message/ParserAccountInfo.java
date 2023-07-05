package branch.dpi.atlas.service.cm.source.manzume.message;


import branch.dpi.atlas.service.cm.source.branch.message.FormatException;
import dpi.atlas.util.Constants;

public class ParserAccountInfo extends ManzumeMessage {

    public ParserAccountInfo(String msgStr) throws FormatException {
        super(msgStr);
        if(msgStr.length()== Constants.ACCOUNT_INFO_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }


    protected  void unpackBody()
    {

    }
}
