package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;


/**
 * User: R.Nasiri
 * Date: July 28, 2020
 * Time: 2:35 PM
 */
public class ParserSendPayaRequest extends BranchMessage {

    public ParserSendPayaRequest(String msgStr) throws FormatException {
        super(msgStr);
        if(msgStr.length()== Constants.SEND_PAYA_REQUEST_LENGTH)
        unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }



    protected  void unpackBody()
    {
        int range = 0;
        refNo = txString.substring(headerLength, range = headerLength + REFNO);
        terminalId = txString.substring(range, range += TERMINAL_ID);
        operator = txString.substring(range, range += OPERATOR);
        operationCode = txString.substring(range, range += OPERATION_CODE);
        transDate= txString.substring(range, range += DATE);
        dueDate= txString.substring(range, range += DUE_DATE);
        serial= txString.substring(range, range += SERIAL);
        transTime= txString.substring(range, range += TRANS_TIME);
        payaDescription= txString.substring(range, range += PAYA_DESCRIPTION);
    }
}
