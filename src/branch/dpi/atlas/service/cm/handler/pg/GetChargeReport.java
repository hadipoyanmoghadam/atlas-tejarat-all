package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReport;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.ChargeReportList;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by sh.Behnaz on 9/1/18.
 */
public class GetChargeReport extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {

            String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);
            String fromDate = msg.getAttributeAsString(Fields.FROM_DATE);
            String toDate = msg.getAttributeAsString(Fields.TO_DATE);

            List<ChargeReport> charges = ChannelFacadeNew.getChargeRecords(accountNo, fromDate, toDate);
            ChargeReportList list = new ChargeReportList();
            list.setCharge(charges);
            msg.setAttribute(Fields.CHARGE_LIST, list);

          String TAmount= ChannelFacadeNew.getTotalChargeAmount(accountNo, fromDate, toDate);
            if (TAmount==null || TAmount.equalsIgnoreCase(""))
                TAmount="0";
          msg.setAttribute(Fields.TOTAL_CHARGE_AMOUNT, TAmount);


        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside GetChargeReport.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

