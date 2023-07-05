package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: F.Heydari
 * Date: sep 2 2019
 * Time: 8:28 AM
 */
public class ParserOfflineATM extends BranchMessage {
    public ParserOfflineATM(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.OFFLINE_ATM_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = headerLength;
        branchCodeBody = txString.substring(range, range += BRANCHCODEBODY);
        deviceCode = txString.substring(range, range += DEVICECODE);
        deviceType=txString.substring(range,range +=DEVICE_TYPE);



    }
}
