package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.Charge;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.ChargeList;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.ChargeRecordsReq;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.DateUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by sh.Behnaz on 11/25/17.
 */
public class GetChargeRecords extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {

            ChargeRecordsReq chargeMsg = (ChargeRecordsReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
            String accountNo = chargeMsg.getAccountNo();
            String cardNo = chargeMsg.getCardno();
            String fromDate = chargeMsg.getFromDate();
            String toDate = chargeMsg.getToDate();



            List<Charge> charges = ChannelFacadeNew.getChargeRecords(accountNo, cardNo, fromDate, toDate);
            if (charges == null || charges.equals("") || charges.size()==0)
                throw new NotFoundException(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHARGE_RECORDS_NOT_FOUND));


            ChargeList list = new ChargeList();
            list.setCharge(charges);
            msg.setAttribute(Fields.CHARGE_LIST, list);


        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.CHARGE_RECORDS_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.CHARGE_RECORDS_NOT_FOUND);
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside ChangePolicy4GroupCard.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

