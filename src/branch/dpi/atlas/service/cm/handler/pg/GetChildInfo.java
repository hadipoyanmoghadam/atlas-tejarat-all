package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.Charge;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Customer;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by sh.Behnaz on 11/18/18.
 */
public class GetChildInfo extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {

            String cardNo = msg.getAttributeAsString(Fields.PAN);

            String customerId = ChannelFacadeNew.getCustomerIdByCardNo(cardNo);
            Customer customer = ChannelFacadeNew.getCustomerInfo(customerId);
            msg.setAttribute(Constants.CUSTOMER, customer);
            Charge lastcharge= ChannelFacadeNew.getLastCharge(cardNo);
            msg.setAttribute(Fields.CHARGE_LIST, lastcharge);
            String accountNo = ChannelFacadeNew.getAccountNoByCustomerId(customerId);
            msg.setAttribute(Fields.ACCOUNT_NO, accountNo.trim());

        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.INVALID_CARD_NUMBER);
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside GetChildInfo.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

