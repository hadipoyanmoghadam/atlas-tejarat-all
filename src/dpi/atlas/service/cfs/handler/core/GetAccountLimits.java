package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.entity.cms.AccountType;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;


/**
 * User: SH.Behnaz
 * Date: Oct 18, 2007
 * Time: 2:22:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetAccountLimits extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(GetAccountLimits.class);

    protected String accountField;

    public GetAccountLimits() {
        accountField = Fields.SRC_ACCOUNT;
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        Long minBalance ;
        AccountType accountType;
        String accountTypeId;

        String strAccountType ;
        if (log.isDebugEnabled()) log.debug("accountField=" + accountField);

        AccountData accountData = (AccountData) holder.get(accountField);
        Account account = (Account) accountData.getAccountHolder();
        strAccountType = account.getAccountType();

        if (log.isDebugEnabled()) log.debug("account.getAccountNo=" + account.getAccountNo());
        if (log.isDebugEnabled()) log.debug("strAccountType=" + strAccountType);

        try {
            accountType = CFSFacadeNew.getAccountType(strAccountType);
            minBalance = accountType.getMinBalance();
            accountTypeId = accountType.getId();

        } catch (NotFoundException e) {
            log.debug("There is no limit, so set 0 as defaut");
            minBalance = new Long(0); // TODO must be converted to a CONSTANT
            accountTypeId = "-1"; // TODO must be converted to a CONSTANT
        } catch (SQLException e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        }

        msg.setAttribute(Fields.WITHDRAW_LIMIT_ACCOUNT_TYPE, accountTypeId);
        msg.setAttribute(Fields.BALANCE_MINIMUM, minBalance);
        accountData.setMinBalance(minBalance.longValue());
    }


    public void setAccountField(String accountField) {
        this.accountField = accountField;
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
