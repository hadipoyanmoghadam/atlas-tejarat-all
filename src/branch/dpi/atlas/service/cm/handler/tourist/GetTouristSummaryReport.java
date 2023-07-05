package branch.dpi.atlas.service.cm.handler.tourist;

import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Jan 15, 2020
 * Time: 3:52 PM
 */
public class GetTouristSummaryReport extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {
            TouristMessage touristMessage;
            touristMessage = (TouristMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            String cardNo = touristMessage.getCardNo();
            String accountNo = touristMessage.getAccountNo();
            String fromDate = touristMessage.getFromDate();
            String toDate = touristMessage.getToDate();
            String transactionType = touristMessage.getTransactionType();
            String paymentId = touristMessage.getId1();
            boolean isAccountBase = msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equalsIgnoreCase("1") ? true : false;


            String[] result = ChannelFacadeNew.getTouristSummaryReport(accountNo, cardNo, fromDate, toDate,paymentId,transactionType,isAccountBase);

            if(result[0]==null || result[0].trim().equalsIgnoreCase(""))
                result[0]="0";

            if(result[1]==null || result[1].trim().equalsIgnoreCase(""))
                result[1]="0";

            msg.setAttribute(Fields.TOTAL_TRANSACTION_AMOUNT, result[0]);
            msg.setAttribute(Fields.TOTAL_TRANSACTION_COUNT, result[1]);

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside GetTouristSummaryReport.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}
