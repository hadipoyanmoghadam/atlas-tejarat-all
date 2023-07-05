package branch.dpi.atlas.service.cm.handler.AMX;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Dec 10, 2018
 * Time: 04:41 PM
 */
public class CheckCustomerID extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String accountNo = amxMessage.getAccountNo();
        String customerId = "";
        try {
            if (amxMessage.getOwnerIndex().equalsIgnoreCase("00")) {
                String[] accountInfo = ChannelFacadeNew.findCustomerID(accountNo, "", "");
                customerId = accountInfo[0];
                if (customerId != null && !customerId.equals("")) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_ALREADY_EXISTS);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_ALREADY_EXISTS));
                }
            } else {
                String[] zeroCustomer = ChannelFacadeNew.findCustomerID(accountNo, "", "");
                String customer = zeroCustomer[0];
                String accountTitle = zeroCustomer[1];
                if (customer == null || customer.equals("")) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.ZERO_ROW_NOT_FOUND);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ZERO_ROW_NOT_FOUND));
                }
                if (!accountTitle.equalsIgnoreCase(Constants.SHARED_ACCOUNT_TYPE) && !accountTitle.equalsIgnoreCase(Constants.HOGHOOGHI_ACCOUNT_TYPE)) {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                }
                msg.setAttribute(Fields.ORIG_ACCOUNT_TYPE,accountTitle);
                String[] accountInfo = ChannelFacadeNew.findCustomerID(accountNo, amxMessage.getNationalCode(), amxMessage.getExt_IdNumber());
                customerId = accountInfo[0];
                if (customerId != null && !customerId.equals("")) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_ALREADY_EXISTS);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_ALREADY_EXISTS));
                }
            }
        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CheckCustomerID.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}
