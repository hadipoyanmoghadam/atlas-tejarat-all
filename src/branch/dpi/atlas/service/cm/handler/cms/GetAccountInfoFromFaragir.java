package branch.dpi.atlas.service.cm.handler.cms;

import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.entity.Customer;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: Feb 24, 2015
 * Time: 10:04:03 PM
 */
public class GetAccountInfoFromFaragir extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMResultSet result;
        result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);
        String actionCode = null;
        if (result != null)
            actionCode = result.getHeaderField(Fields.ACTION_CODE);

        if (log.isDebugEnabled()) log.debug("actionCode:" + actionCode);

        if (actionCode == null) {
            actionCode = ActionCode.GENERAL_ERROR;
            log.error("Returned ActionCode is null");
        }

        if (actionCode.equals(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED) && msg.getAttributeAsString(Fields.HOST_ID).equals(Constants.HOST_ID_SGB))
            actionCode = ActionCode.ACCOUNT_IS_OFFLINE;

        msg.setAttribute(Fields.ACTION_CODE, actionCode);

        try {
            if (!actionCode.startsWith("00"))
                return;

            result.moveFirst();

            result.next();

            String messageType = msg.getAttributeAsString(Fields.MESSAGE_TYPE);
            String nationalCode=result.getString("NATIONALCODE");
            if(nationalCode!=null)
                nationalCode=nationalCode.trim();
            if(result.getString("ACCTYPE").equalsIgnoreCase("1") && nationalCode.length()>10)
                   nationalCode= nationalCode.substring(1,11);
            else if(result.getString("ACCTYPE").equalsIgnoreCase("3") && nationalCode.length()>10 && nationalCode.startsWith("0")){
                nationalCode= nationalCode.substring(1,11);
            }

            if (messageType.equals(Constants.ACCOUNTINFO_MSG_TYPE) && msg.getAttributeAsString(Fields.HOST_ID).equals(Constants.HOST_ID_UNKNOWN)) {
                String accountGroup = result.getString("ACCGROUP");
                if (accountGroup != null && !accountGroup.trim().equalsIgnoreCase("") && accountGroup.trim().length() < 3)
                    accountGroup = ISOUtil.padleft(accountGroup.trim(), 3, '0');
//               ChannelFacadeNew.insertCustomerAccountInSRV(result.getString("ACCNO"), Constants.HOST_FARAGIR, result.getString("ACCGROUP"), nationalCode);
                ChannelFacadeNew.insertCustomerAccountInSRV(result.getString("ACCNO"), Constants.HOST_FARAGIR, accountGroup, nationalCode);
            }


            Customer customer = new Customer();
            Account account = new Account();
            String branch_id=result.getString("BRANCHCODE");
            if(branch_id!=null){
                branch_id=branch_id.trim();
                if(branch_id.length()>5)
                branch_id=branch_id.substring(2);
                if(branch_id.length()<5)
                    branch_id=ISOUtil.padleft(branch_id,5,' ');
            }else {
                branch_id=" ";
            }
            account.setSgb_branch_id(branch_id);
            account.setAccountType(result.getString("ACCGROUP"));
            account.setAccountTitle(result.getString("ACCTYPE"));
            customer.setGender(Integer.parseInt(result.getString("SEX")));
            customer.setNationalCode(nationalCode);
            customer.setExternalIdNumber(result.getString("FOREINGCODE"));
            customer.setIdNumber(result.getString("IDNUMBER"));
            customer.setBirthDate(result.getString("BIRTHDATE"));
            customer.setCellPhone(result.getString("MOBILENO"));
            customer.setFatherName(result.getString("FATHERNAME"));
            customer.setFirstName(result.getString("CUSTOMERNAME"));
            customer.setLastName(result.getString("CUSTOMERFAMILY"));
            holder.put(Constants.CUSTOMER_ACCOUNT, account);
            holder.put(Constants.CUSTOMER, customer);


        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));

        } catch (Exception e) {
            log.error("ERROR :::Inside GetAccountInfoFromFaragir.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

