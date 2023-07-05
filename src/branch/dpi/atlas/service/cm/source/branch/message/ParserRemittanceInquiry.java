package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: F.Heidari
 * Date: July 27 2019
 * Time: 9:54 AM
 */
public class ParserRemittanceInquiry extends BranchMessage {
    public ParserRemittanceInquiry(String msgStr) throws FormatException {
        super(msgStr);

        if (msgStr.length() == Constants.REMITTANCE_INQUIRY_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = headerLength;
        remittance_request_no = txString.substring(headerLength, range += REMITTANCE_REQUEST_NO);
        remittanceDate = txString.substring(range, range += REMITTANCE_DATE);
        nationalCode = txString.substring(range, range += NATIONAL_CODE);
        ext_IdNumber = txString.substring(range, range += EXT_ID_NUMBER);
        if (Long.parseLong(ext_IdNumber) == 0)
            ext_IdNumber = "";


    }

}
