package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.pg.wfp.WFPCharge;
import branch.dpi.atlas.service.cm.handler.pg.wfp.WFPChargeList;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by sh.Behnaz on 8/6/19.
 */
public class GetWFPCharges extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {

            String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);
            String fromDate = msg.getAttributeAsString(Fields.FROM_DATE);
            String toDate = msg.getAttributeAsString(Fields.TO_DATE);

            List<WFPCharge> charges = ChannelFacadeNew.getWFPCharges(accountNo, fromDate, toDate);
            if (charges == null || charges.equals("") || charges.size()==0)
                throw new NotFoundException(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHARGE_RECORDS_NOT_FOUND));

            WFPChargeList list = new WFPChargeList();
            list.setCharge(charges);
            msg.setAttribute(Fields.CHARGE_LIST, list);


          String  TAmount= ChannelFacadeNew.getWFPRevokedCards(accountNo, fromDate, toDate);
          msg.setAttribute(Fields.TOTAL_CHARGE_AMOUNT, TAmount);

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.CHARGE_RECORDS_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.CHARGE_RECORDS_NOT_FOUND);
        } catch (Exception e) {
            log.error("ERROR :::Inside GetWFPCharges.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

