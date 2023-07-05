package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.entity.Customer;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Sep 15, 2013
 * Time: 12:45:27 PM
 */
public class FindCustomerID extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String accountNo = branchMsg.getAccountNo();
        Account account = null;

        String customerId = "";
        String accountTitle = "";
        String subTitle = "";
        try {

            if (branchMsg.getPin().equalsIgnoreCase(Constants.PIN_CREATE_NAC) && branchMsg.getRequestType().equalsIgnoreCase(Constants.UPDATE_NAC_REQUEST_TYPE)) {
                String[] accountInfo = ChannelFacadeNew.findCustomerID(accountNo, "", "");
                customerId = accountInfo[0];
                accountTitle = accountInfo[1];
            } else if (branchMsg.getPin().equalsIgnoreCase(Constants.PIN_UPDATE_ROW)) {
                String[] accountInfo = ChannelFacadeNew.findCustomerID(accountNo, branchMsg.getOldNationalCode(), branchMsg.getOld_ext_IdNumber());
                customerId = accountInfo[0];
                accountTitle = accountInfo[1];
            } else if (branchMsg.getPin().equalsIgnoreCase(Constants.PIN_GET_ACCOUNT_STATUS)) {
                String[] accountInfo = ChannelFacadeNew.findCustomerID(accountNo, branchMsg.getNationalCode(), branchMsg.getExt_IdNumber());
                customerId = accountInfo[0];
                accountTitle = accountInfo[1];
                subTitle = accountInfo[4];


                if ((accountTitle!=null && subTitle!=null)
                        &&(accountTitle.equals("3") || accountTitle.equals("2")) && !subTitle.equals("0")) {
                    if (holder.containsKey(Constants.CUSTOMER_ACCOUNT)) {
                        account = (Account) holder.get(Constants.CUSTOMER_ACCOUNT);
                    } else {
                        throw new NotFoundException("Customer does not exist");
                    }
                    account.setAccountTitle(subTitle);
                    holder.put(Constants.CUSTOMER_ACCOUNT,account);
                }
            } else {
                String[] accountInfo = ChannelFacadeNew.findCustomerID(accountNo, branchMsg.getNationalCode(), branchMsg.getExt_IdNumber());
                customerId = accountInfo[0];
                accountTitle = accountInfo[1];
            }
            if (accountTitle != null && !accountTitle.equals("")) {
            msg.setAttribute(Fields.ORIG_ACCOUNT_TYPE, accountTitle);
            }
            if (customerId != null && !customerId.equals("")) {
                holder.put(Fields.BRANCH_CUSTOMER_ID, customerId);
            } else {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
            }
        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside FindCustomerID.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}