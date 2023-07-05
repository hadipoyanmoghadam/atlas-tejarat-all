package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * User: R.Nasiri
 * Date: July 28, 2020
 * Time: 2:32 PM
 */
public class ParserRegisterPayaRequest extends BranchMessage {

    public ParserRegisterPayaRequest(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.REGISTER_PAYA_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }


    protected void unpackBody() {
        int range = 0;
        documentNo = txString.substring(headerLength, range = headerLength + DOCUMENT_NO);
        dueDate = txString.substring(range, range += DATE);
        bankSend = txString.substring(range, range += BANK_CODE);
        bankRecv = txString.substring(range, range += BANK_CODE);
        amount = txString.substring(range, range += AMOUNT);
        sourceIban = txString.substring(range, range += IBAN);
        paymentCode = txString.substring(range, range += PAYMENT_CODE);
        destIban = txString.substring(range, range += IBAN);
        senderName = txString.substring(range, range += PAYA_NAME);
        meliCode = txString.substring(range, range += MELI_CODE);
        postalCode = txString.substring(range, range += POSTAL_CODE);
        address1 = txString.substring(range, range += ADDRESS1);
        tel_Number1 = txString.substring(range, range += SENDER_TEL);
        reciverName = txString.substring(range, range += PAYA_NAME);
        payaDescription = txString.substring(range, range += PAYA_DESCRIPTION);
        senderShahab = txString.substring(range, range += SHAHAB);
        reciverShahab = txString.substring(range, range += SHAHAB);
        reciverMeliCode = txString.substring(range, range += MELI_CODE);
        reciverPostalCode = txString.substring(range, range += POSTAL_CODE);
        reason = txString.substring(range, range += REASON);

    }
}
