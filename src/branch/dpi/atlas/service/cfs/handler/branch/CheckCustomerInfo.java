package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.entity.Customer;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: July 7, 2015
 * Time: 01:54 PM
 */

public class CheckCustomerInfo extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        Customer customer = null;
        AccountData accountData = (AccountData) holder.get(accountField);
        Account account = (Account) accountData.getAccountHolder();
        List<String> info = new ArrayList<String>();
        try {
            info = CFSFacadeNew.getCustomerInfo(account.getCustomerId().trim());

        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CFSFault(CFSFault.FLT_CUSTOMER_NOT_FOUND, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside checkCustomerInfo.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, new Exception(ActionCode.GENERAL_ERROR));
        }
        msg.setAttribute(Constants.FIRST_NAME, info.get(0));
        msg.setAttribute(Constants.LAST_NAME, info.get(1));

    }

    public void setAccountField(String accountField) {
        this.accountField = accountField;
    }

    public CheckCustomerInfo() {
        accountField = Fields.SRC_ACCOUNT;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.SRC_ACCOUNT;
        }

    }

}
