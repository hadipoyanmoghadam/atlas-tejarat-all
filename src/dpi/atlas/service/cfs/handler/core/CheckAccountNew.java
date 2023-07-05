package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.7 $ $Date: 2007/10/30 15:13:59 $
 */

public class CheckAccountNew extends CFSHandlerBase implements Configurable {

    protected String accountField;
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        String acc = msg.getAttributeAsString(accountField);
        Account account = null;
        try {
            acc = ISOUtil.zeropad(acc, 13);
            if (log.isDebugEnabled()) log.debug(accountField + "=" + acc);
            account = CFSFacadeNew.getAccount(acc);
            if (account.isLocked())
                throw new CFSFault(CFSFault.FLT_LOCKED_ACCOUNT, ActionCode.ACCOUNT_BALANCE_INFORMATION_NOT_AVAILABLE);
            log.info("Balance = " + account.getAccountBalance());
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + acc + "' in CheckAccountNew : " + e.getMessage());
        } catch (NotFoundException e) {
            throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } catch (SQLException e) {
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        }
        holder.put(accountField, account.getAccountData());
        msg.setAttribute(accountField, account.getAccountNo());
    }

    public void setAccountField(String accountField) {
        this.accountField = accountField;
    }

    public CheckAccountNew() {
        accountField = Fields.SRC_ACCOUNT;
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
