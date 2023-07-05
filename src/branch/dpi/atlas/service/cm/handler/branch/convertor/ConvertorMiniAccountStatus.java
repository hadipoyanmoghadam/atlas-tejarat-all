package branch.dpi.atlas.service.cm.handler.branch.convertor;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.entity.Customer;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

import java.util.Map;


/**
 * User: R.Nasiri
 * Date: Feb 22, 2014
 * Time: 11:33 AM
 */
public class ConvertorMiniAccountStatus extends BranchBaseConvertor implements BranchToIMFFormater {

    public CMCommand format(BranchMessage branchMsg) {
        CMCommand command = super.format(branchMsg);
        return command;
    }

    public String createResponse(CMMessage msg, Map map) throws CMFault {
        Account account = null;
        Customer customer = null;
        try {
            if (map.containsKey(Constants.CUSTOMER_ACCOUNT)) {
                account = (Account) map.get(Constants.CUSTOMER_ACCOUNT);
            }
            if (map.containsKey(Constants.CUSTOMER)) {
                customer = (Customer) map.get(Constants.CUSTOMER);
            } else
                throw new NotFoundException("Customer does not exist");

            StringBuilder responseStr = new StringBuilder();
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            responseStr.append(branchMsg.createResponseHeader())
                    .append(ISOUtil.padleft(customer.getFirstName().trim(), branchMsg.FIRST_NAME, ' '))
                    .append(ISOUtil.padleft(customer.getLastName().trim(), branchMsg.LAST_NAME, ' '))
                    .append(ISOUtil.padleft(customer.getNationalCode().trim(), branchMsg.NATIONAL_CODE, ' '))
                    .append(ISOUtil.padleft(customer.getBirthDate().trim(), branchMsg.BIRTH_DATE, '0'))
                    .append(ISOUtil.padleft(customer.getIdNumber().trim(), branchMsg.ID_NUMBER, '0'))
                    .append(ISOUtil.padleft(customer.getTelNumber1().trim(), branchMsg.TEL_NO1, '0'))
                    .append(ISOUtil.padleft(customer.getAddress1().trim(), branchMsg.ADDRESS1, ' '))
                    .append(ISOUtil.padleft(String.valueOf(account.getAccountStatus()).trim(), branchMsg.ACCOUNT_STATUS, '0'))
                    .append(ISOUtil.padleft(account.getAccountOpenerName().trim(), branchMsg.ACCOUNT_OPENER_NAME, ' '))
                    .append(ISOUtil.padleft(String.valueOf(account.getWithdrawType()).trim(), branchMsg.WITHDRAW_TYPE, '0'));
            if (customer.getAddress2() == null || "".equals(customer.getAddress2().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ADDRESS2, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getAddress2().trim(), branchMsg.ADDRESS2, ' '));
            return responseStr.toString();

        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (Exception e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }

    }
}



