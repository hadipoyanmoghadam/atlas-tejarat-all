package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @version $Revision: 1.10 $ $Date: 2007/10/30 14:13:31 $
 */

public class CheckAccountStatus extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public CheckAccountStatus() {
        accountField = Fields.SRC_ACCOUNT;
    }

    public void setAccountField(String accountField) {
        this.accountField = accountField;
    }


    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        if (log.isInfoEnabled()) log.info("--------->>> inside CheckAccountStatus:doProcess()");

        if (!holder.containsKey(accountField)) return;

        AccountData accountData = (AccountData) holder.get(accountField);
        dpi.atlas.model.tj.entity.Account account = (Account) accountData.getAccountHolder();

        if (account.getAccountStatus() != CFSConstants.ACC_STATUS_ACTIVE) {
            holder.remove(accountField);

            if (accountField.equals(Fields.SRC_ACCOUNT)) {
                throw new CFSFault(CFSFault.FLT_FROM_ACC_BAD_STATUS, new Exception(ActionCode.FROM_ACCOUNT_BAD_STATUS));
            } else if (accountField.equals(Fields.DEST_ACCOUNT)) {
                throw new CFSFault(CFSFault.FLT_TO_ACC_BAD_STATUS, new Exception(ActionCode.TO_ACCOUNT_BAD_STATUS));
            } else {
                throw new CFSFault(CFSFault.ACC_BAD_STATUS, new Exception(ActionCode.ACCOUNT_BAD_STATUS));
            }
        }

//        if (account.isLocked()) {
//            if (log.isDebugEnabled()) log.debug("account is locked");
//            throw new CFSFault(CFSFault.FLT_LOCKED_ACCOUNT, new Exception(ActionCode.LOCKED_ACCOUNT));
//        } else {
            int hostID = account.getHostID();
            try {
                boolean hostIsLocked = CFSFacadeNew.getHostLockStatus(hostID);
                if (hostIsLocked)
                    throw new CFSFault(CFSFault.FLT_LOCKED_HOST, new Exception(ActionCode.LOCKED_HOST));
            } catch (NotFoundException e) {
                log.error(e);
                throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, new Exception(ActionCode.GENERAL_ERROR));
            } catch (SQLException e) {
                log.error(e);
                throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
            }
//        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.SRC_ACCOUNT;
        }
    }


}
