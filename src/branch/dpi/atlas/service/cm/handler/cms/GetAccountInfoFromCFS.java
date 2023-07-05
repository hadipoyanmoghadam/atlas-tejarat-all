package branch.dpi.atlas.service.cm.handler.cms;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: Feb 21, 2015
 * Time: 15:04:03 PM
 */
public class GetAccountInfoFromCFS extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);
        Account account;

        try {

            account = ChannelFacadeNew.getAccount(accountNo);
            if (account.getAccountStatus() != CFSConstants.ACC_STATUS_ACTIVE) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_BAD_STATUS);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_BAD_STATUS));
            }

            holder.put(Constants.CUSTOMER_ACCOUNT, account);
            holder.put(Fields.BRANCH_CUSTOMER_ID, account.getCustomerId());
            holder.put(Fields.WITHDRAW_TYPE, account.getWithdrawType());

        } catch (CMFault e) {
            throw e;
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside GetAccountInfoFromCFS.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
