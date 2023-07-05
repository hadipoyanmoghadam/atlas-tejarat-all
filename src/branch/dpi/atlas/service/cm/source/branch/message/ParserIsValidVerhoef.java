package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: F.Heidari
 * Date: July 15 2019
 * Time: 10:41 AM
 */
public class ParserIsValidVerhoef extends BranchMessage {
    public ParserIsValidVerhoef(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.VALID_VERHOEF)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = headerLength;
        id_num = txString.substring(headerLength, range += ID_Num);
        amount_verhoef = txString.substring(range, range += Amount_Verhoef);
    }
}
