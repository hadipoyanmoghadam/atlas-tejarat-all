package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;

/**
 * Created by dpi on 7/9/19.
 */
public class ParserIsValidExpenseVerhoef extends BranchMessage {

    public ParserIsValidExpenseVerhoef(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.VALID_EXPENSE_VERHOEF)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        int range =headerLength;
        id_num = txString.substring(headerLength, range += ID_Num);
        amount_verhoef = txString.substring(range, range += Amount_Verhoef);
        cheque_number = txString.substring(range, range += Cheque_Number);
        date_verhoef = txString.substring(range, range += Date_Verhoef);
    }

}
