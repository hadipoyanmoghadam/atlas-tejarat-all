package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

public class GetFTAccount extends CFSHandlerBase {
    private String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String acc = msg.getAttributeAsString(accountField);
//        acc = "000" + acc;//Todo: chech this laetr,to remove this
        dpi.atlas.model.tj.entity.Account account = null;

        try {
            account = CFSFacadeNew.getAccount(acc);
        } catch (NotFoundException e) {
            if (log.isDebugEnabled())
                log.debug("Destination Account Does Not Exist In CFS Accounts, " + e);
        } catch (SQLException me) {
            log.error(me);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }
        if (account != null ) holder.put(accountField, account.getAccountData());
    }

    public GetFTAccount() {
        accountField = Fields.DEST_ACCOUNT;;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(destAccount)");
            accountField = Fields.DEST_ACCOUNT;

        }
    }

}
