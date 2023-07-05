package branch.dpi.atlas.service.cm.source.branch.message;


import dpi.atlas.util.Constants;

/**
 * User: F.Heydari
 * Date: Dec 23,2020
 * Time: 8:58:20 AM
 */
public class ParserSiminChangeStatusCBI extends BranchMessage {
    public ParserSiminChangeStatusCBI(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.SIMIN_CHANGE_STATUS_AHKAM_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = 0;
        accountStatus = txString.substring(headerLength, range =headerLength+ ACCOUNT_STATUS);
        amount = txString.substring(range, range += AMOUNT);
        blockRow = txString.substring(range, range += BLOCK_ROW);
        documentDescription = txString.substring(range, range += SIMIN_DOCUMENT_DESCRIPTION);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        nationalCode = txString.substring(range, range += NATIONAL_CODE);
        range=range+1;//for convert ext_IdNo(16) to ext_IdNo(15)
        ext_IdNumber = txString.substring(range, range += EXT_ID_NUMBER);
        cbiFlag=txString.substring(range,range+=CBI_FLAG);
        organization=txString.substring(range,range+=ORGANIZATION);
    }
}
