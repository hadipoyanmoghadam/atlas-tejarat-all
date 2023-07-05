package dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.facade.CFSFacadeNew;

import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

import java.util.Map;
import java.sql.SQLException;

/**
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.7 $ $Date: 2007/10/30 15:13:59 $
 */

public class UpdateAccount4Branch extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String acc = msg.getAttributeAsString(accountField).trim();
        try {
            if (acc.length() < 13)
                acc = ISOUtil.zeropad(acc, 13);//Todo: chech this laetr,to remove this
        } catch (ISOException e) {
            log.error(e);
        }

        if (log.isDebugEnabled()) log.debug(accountField + "=" + acc);


        Account accountHolder = (Account) ((AccountData) holder.get(accountField)).getAccountHolder();
        if (accountHolder.getAccountStatus() == 1) {
                log.error("Account = " + acc + " is enabled before");
                throw new CFSFault(CFSFault.FLT_ENABLED_BEFORE, new Exception(ActionCode.ACCOUNT_ENABLED_BEFORE));
        } else if (accountHolder.getAccountStatus() == 2) {
                log.error("Account = " + acc + " is disabled before");
                throw new CFSFault(CFSFault.FLT_DISABLED_BEFORE, new Exception(ActionCode.ACCOUNT_DISABLED_BEFORE));
        }

        try {
            CFSFacadeNew.updateAccount(acc, 1, "");
        } catch (SQLException e) {
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        }

//        throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, new Exception(ActionCode.NOT_SUFFICIENT_FUNDS));
//        throw new CFSFault(CFSFault.FLT_ENABLED_BEFORE, new Exception(ActionCode.ACCOUNT_ENABLED_BEFORE));
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.DEST_ACCOUNT; // TODO or src?????
        }

    }

}
