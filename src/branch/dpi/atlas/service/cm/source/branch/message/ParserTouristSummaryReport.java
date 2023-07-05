package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

/**
 * User: R.Nasiri
 * Date: Jan 15, 2020
 * Time: 04:43 PM
 */
public class ParserTouristSummaryReport extends TouristMessage {

    public ParserTouristSummaryReport(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.TOURIST_SUMMARY_REPORT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        String paymentId = "";
        int range = 0;
        accountNo = txString.substring(headerLength, range = headerLength + ACCOUNT_NO);
        fromDate = txString.substring(range, range += FROM_DATE);
        toDate = txString.substring(range, range += TO_DATE);
        transactionType = txString.substring(range, range += TRANSACTION_TYPE);
        paymentId = txString.substring(range, range += ID1);
        id1 = paymentId.trim().equalsIgnoreCase("") ? "" : paymentId.trim();

    }
}

