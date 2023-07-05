package branch.dpi.atlas.service.cm.source.branch.message;

import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

/**
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 08:43 AM
 */
public class ParserTouristChargeStatement extends TouristMessage {

    public ParserTouristChargeStatement(String msgStr) throws FormatException {
        super(msgStr);
        if (msgStr.length() == Constants.TOURIST_CHARGE_STATEMENT_REQUEST_LENGTH)
            unpackBody();
        else
            throw new FormatException("length of request is incorrect");
    }

    protected void unpackBody() {
        String fromDate="";
        String toDate="";
        String fromTime="";
        String toTime="";
        String fromSequence="";
        int range = 0;
        fromDate = txString.substring(headerLength, range = headerLength + FROM_DATE);
        this.fromDate= ISOUtil.isZero(fromDate)? "":fromDate;
        toDate = txString.substring(range, range += TO_DATE);
        this.toDate= ISOUtil.isZero(toDate)? "":toDate;
        fromTime = txString.substring(range, range += FROM_TIME);
        this.fromTime= ISOUtil.isZero(fromTime) ? "" : fromTime;
        toTime = txString.substring(range, range += TO_TIME);
        this.toTime= ISOUtil.isZero(toTime)? "":toTime;
        transactionCount = txString.substring(range, range += TRANSACTION_COUNT);
        fromSequence = txString.substring(range, range += FROM_SEQUENCE);
        this.fromSequence= ISOUtil.isZero(fromSequence)?"":fromSequence;
    }
}

