package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: F.Heydari
 * Date: February 08, 2020
 * Time: 16:16:48 PM
 */
public class ParserNACSimin extends BranchMessage {

    public ParserNACSimin(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SIMIN_NAC_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }


    protected void unpackBody() {

        int range = headerLength;
        branchCodeBody=txString.substring(range, range +=BRANCHCODEBODY );
        accountGroup = txString.substring(range, range += ACCOUNT_GROUP);
        firstName = txString.substring(range, range += FIRST_NAME);
        lastName = txString.substring(range, range += LAST_NAME);
        accountType = txString.substring(range, range += ACCOUNT_TYPE);
        ownerIndex="00";



    }
}
