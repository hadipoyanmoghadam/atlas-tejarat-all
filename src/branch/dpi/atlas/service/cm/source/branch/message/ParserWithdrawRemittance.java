package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: F.Heidari
 * Date: July 28 2019
 * Time: 9:26 AM
 */

public class ParserWithdrawRemittance extends BranchMessage {
    public ParserWithdrawRemittance(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.WITHDRAW_REMITTANCE_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range = headerLength;
        terminalId = txString.substring(range, range += TERMINAL_ID);
        transDate = txString.substring(range, range += TRANS_DATE);
        transTime = txString.substring(range, range += TRANS_TIME);
        documentNo = txString.substring(range, range += DOCUMENT_NO);
        documentDescription = txString.substring(range, range += DOCUMENT_DESCRIPTION);
        operationCode = txString.substring(range, range += OPERATION_CODE);
        operationCodeFeeAmount = txString.substring(range, range += OPERATION_CODE);
        remittance_request_no = txString.substring(range, range += REMITTANCE_REQUEST_NO);
        remittanceDate = txString.substring(range, range += REMITTANCE_DATE);
        nationalCode = txString.substring(range, range += NATIONAL_CODE);
        ext_IdNumber = txString.substring(range, range += EXT_ID_NUMBER);
        if (Long.parseLong(ext_IdNumber) == 0)
            ext_IdNumber = "";


    }

}
