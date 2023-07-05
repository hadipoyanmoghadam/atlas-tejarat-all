package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
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

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.6 $ $Date: 2007/10/29 14:04:20 $
 */

public class GetFTAccountRangeNew extends CFSHandlerBase implements Configurable {

    private String accountField;

    public void setAccountField(String accountField) {
        this.accountField = accountField;
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String acc = msg.getAttributeAsString(accountField);
        String range = acc.substring(3, 8);

        try {
            int accRange = CFSFacadeNew.getAccountRange(range);
            if (accRange != CFSConstants.IRANIAN_CURRENCY) {
                log.debug("For " + accountField + " = "+ acc + ", account range type for account range " + range + " is not local currency");
                throw new CFSFault(CFSFault.FLT_ACCOUNT_RANGE_NOT_FOUND, new Exception(ActionCode.ACCOUNT_RANGE_NOT_FOUND));
            }
        } catch (SQLException e) {
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }
        catch (NotFoundException e) {
            if (log.isInfoEnabled()) log.info("For " + accountField + " = "+ acc + ", account Range " + range + " does not exist");
            throw new CFSFault(CFSFault.FLT_ACCOUNT_RANGE_NOT_FOUND, new Exception(ActionCode.ACCOUNT_RANGE_NOT_FOUND));
        }
    }

    public GetFTAccountRangeNew() {
        accountField = Fields.DEST_ACCOUNT;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))){
            accountField = Fields.DEST_ACCOUNT;
        }
    }
}
