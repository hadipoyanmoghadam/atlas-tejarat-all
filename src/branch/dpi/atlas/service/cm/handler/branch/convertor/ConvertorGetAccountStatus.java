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
 * Date: July 09, 2013
 * Time: 10:19 AM
 */
public class ConvertorGetAccountStatus extends BranchBaseConvertor implements BranchToIMFFormater {

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
            responseStr.append(branchMsg.createResponseHeader());

            if (account.getAccountTitle() == null || "".equals(account.getAccountTitle().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ACCOUNT_TYPE, '0'));
            else
                responseStr.append(ISOUtil.padleft(account.getAccountTitle().trim(), branchMsg.ACCOUNT_TYPE, '0'));

            if (customer.getOwnerIndex() == null || "".equals(customer.getOwnerIndex().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.OWNER_INDEX, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getOwnerIndex().trim(), branchMsg.OWNER_INDEX, '0'));

            if (account.getAccountType() == null || "".equals(account.getAccountType().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ACCOUNT_GROUP, '0'));
            else
                responseStr.append(ISOUtil.padleft(account.getAccountType().trim(), branchMsg.ACCOUNT_GROUP, '0'));

            responseStr.append(ISOUtil.padleft(String.valueOf(customer.getStatementType()).trim(), branchMsg.STATEMENT_TYPE, '0'));

            if (customer.getCreationDate() == null || "".equals(customer.getCreationDate().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.CREATE_DATE, '0'));
            else{
                if(customer.getCreationDate().trim().length()==6)
                    customer.setCreationDate(Constants.CENTURY+customer.getCreationDate().trim());

                responseStr.append(ISOUtil.padleft(customer.getCreationDate().trim(), branchMsg.CREATE_DATE, '0'));
            }
            if (customer.getOrigEditDate() == null || "".equals(customer.getOrigEditDate().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.CHANGE_DATE, '0'));
            else{
                if(customer.getOrigEditDate().trim().length()==6)
                    customer.setOrigEditDate(Constants.CENTURY + customer.getOrigEditDate().trim());

                responseStr.append(ISOUtil.padleft(customer.getOrigEditDate().trim(), branchMsg.CHANGE_DATE, '0'));
            }

            if (customer.getFirstName() == null || "".equals(customer.getFirstName().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.FIRST_NAME, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getFirstName().trim(), branchMsg.FIRST_NAME, ' '));

            if (customer.getLastName() == null || "".equals(customer.getLastName().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.LAST_NAME, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getLastName().trim(), branchMsg.LAST_NAME, ' '));

            if (customer.getFatherName() == null || "".equals(customer.getFatherName().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.FATHER_NAME, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getFatherName().trim(), branchMsg.FATHER_NAME, ' '));

            responseStr.append(ISOUtil.padleft(String.valueOf(customer.getGender()).trim(), branchMsg.GENDER, '0'));

            if (customer.getNationalCode() == null || "".equals(customer.getNationalCode().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.NATIONAL_CODE, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getNationalCode().trim(), branchMsg.NATIONAL_CODE, ' '));

            if (customer.getBirthDate() == null || "".equals(customer.getBirthDate().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.BIRTH_DATE, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getBirthDate().trim(), branchMsg.BIRTH_DATE, '0'));

            if (customer.getIdNumber() == null || "".equals(customer.getIdNumber().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ID_NUMBER, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getIdNumber().trim(), branchMsg.ID_NUMBER, '0'));


            if (customer.getIdSerialNumber() == null || "".equals(customer.getIdSerialNumber().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ID_SERIAL_NUMBER, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getIdSerialNumber().trim(), branchMsg.ID_SERIAL_NUMBER, '0'));

            if (customer.getIdSeries() == null || "".equals(customer.getIdSeries().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ID_SERIES, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getIdSeries().trim(), branchMsg.ID_SERIES, ' '));

            if (customer.getIdIssueDate() == null || "".equals(customer.getIdIssueDate().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ID_ISSUE_DATE, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getIdIssueDate().trim(), branchMsg.ID_ISSUE_DATE, '0'));

            if (customer.getIdIssueCode() == null || "".equals(customer.getIdIssueCode().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ID_ISSUE_CODE, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getIdIssueCode().trim(), branchMsg.ID_ISSUE_CODE, '0'));

            if (customer.getIdIssuePlace() == null || "".equals(customer.getIdIssuePlace().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ID_ISSUE_PLACE, ' '));
            else
            responseStr.append(ISOUtil.padleft(customer.getIdIssuePlace().trim(), branchMsg.ID_ISSUE_PLACE, ' '));

            if (customer.getEnglishFirstName() == null || "".equals(customer.getEnglishFirstName().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.EN_FIRST_NAME, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getEnglishFirstName().trim(), branchMsg.EN_FIRST_NAME, ' '));

            if (customer.getEnglishLastName() == null || "".equals(customer.getEnglishLastName().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.EN_LAST_NAME, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getEnglishLastName().trim(), branchMsg.EN_LAST_NAME, ' '));

            if (customer.getExternalIdNumber() == null || "".equals(customer.getExternalIdNumber()))
                responseStr.append(ISOUtil.padleft("", branchMsg.EXT_ID_NUMBER, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getExternalIdNumber().trim(), branchMsg.EXT_ID_NUMBER, '0'));

            if (customer.getForeignCountryCode() == null || "".equals(customer.getForeignCountryCode()))
                responseStr.append(ISOUtil.padleft("", branchMsg.FOREIGN_COUNTRY_CODE, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getForeignCountryCode().trim(), branchMsg.FOREIGN_COUNTRY_CODE, '0'));

            if (customer.getTelNumber1() == null || "".equals(customer.getTelNumber1()))
                responseStr.append(ISOUtil.padleft("", branchMsg.TEL_NO1, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getTelNumber1().trim(), branchMsg.TEL_NO1, '0'));

            if (customer.getTelNumber2() == null || "".equals(customer.getTelNumber2()))
                responseStr.append(ISOUtil.padleft("", branchMsg.TEL_NO2, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getTelNumber2().trim(), branchMsg.TEL_NO2, '0'));

            if (customer.getCellPhone() == null || "".equals(customer.getCellPhone()))
                responseStr.append(ISOUtil.padleft("", branchMsg.MOBILE_NO, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getCellPhone().trim(), branchMsg.MOBILE_NO, '0'));

            if (customer.getFax() == null || "".equals(customer.getFax()))
                responseStr.append(ISOUtil.padleft("", branchMsg.FAX_NO, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getFax().trim(), branchMsg.FAX_NO, '0'));

            if (customer.getAddress1() == null || "".equals(customer.getAddress1().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ADDRESS1, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getAddress1().trim(), branchMsg.ADDRESS1, ' '));

            if (customer.getAddress2() == null || "".equals(customer.getAddress2().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ADDRESS2, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getAddress2().trim(), branchMsg.ADDRESS2, ' '));

            if (customer.getPostalCode() == null || "".equals(customer.getPostalCode()))
                responseStr.append(ISOUtil.padleft("", branchMsg.POSTAL_CODE, '0'));
            else
                responseStr.append(ISOUtil.padleft(customer.getPostalCode().trim(), branchMsg.POSTAL_CODE, '0'));

            if (account.getCurrency() == null || "".equals(account.getCurrency().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.CURRENCY_CODE, ' '));
            else
                responseStr.append(ISOUtil.padleft(account.getCurrency().trim(), branchMsg.CURRENCY_CODE, ' '));

            responseStr.append(ISOUtil.padleft(String.valueOf(account.getAccountStatus()).trim(), branchMsg.ACCOUNT_STATUS, '0'));
            responseStr.append(ISOUtil.padleft(String.valueOf(customer.getStatusMelli()).trim(), branchMsg.NATIONAL_CODE_VALID, '0'));

            if (account.getAccountOpenerName() == null || "".equals(account.getAccountOpenerName().trim()))
                responseStr.append(ISOUtil.padleft("", branchMsg.ACCOUNT_OPENER_NAME, ' '));
            else
                responseStr.append(ISOUtil.padleft(account.getAccountOpenerName().trim(), branchMsg.ACCOUNT_OPENER_NAME, ' '));

            responseStr.append(ISOUtil.padleft(String.valueOf(account.getWithdrawType()).trim(), branchMsg.WITHDRAW_TYPE, '0'));

            if (customer.getEmailAddress() == null || "".equals(customer.getEmailAddress()))
                responseStr.append(ISOUtil.padleft("", branchMsg.E_MAIL, ' '));
            else
                responseStr.append(ISOUtil.padleft(customer.getEmailAddress().trim(), branchMsg.E_MAIL, ' '));

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



