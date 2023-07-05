package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;


/**
 * User:F.Heydari
 * Date:Dec 29 2019
 * time:9:10 AM
 */
public class ParserSiminStatement extends BranchMessage{
    public ParserSiminStatement(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.GET_STATEMENT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {

        int range = headerLength;
        fromDate = txString.substring(range, range += FROM_DATE);
        toDate = txString.substring(range, range += TO_DATE);




    }
}
