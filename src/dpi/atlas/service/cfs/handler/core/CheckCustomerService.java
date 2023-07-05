package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.5 $ $Date: 2007/11/01 22:24:13 $
 */

public class CheckCustomerService extends CFSHandlerBase implements Configurable {

    private String amountField;
    private String accountField;

    public CheckCustomerService() {
        accountField = Fields.SRC_ACCOUNT;
        amountField = Fields.AMOUNT;
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        long amount = Long.parseLong((String) msg.getAttribute(amountField));
        try {
            String txValue = CFSFacadeNew.getCustomerTmplSrvTxValue(Constants.ISO_CUSTOMER_DEFAULT_TEMPLATEID, "FT", "AmountLimit");
            if ((Long.parseLong(txValue.trim())) < amount) {
                if (log.isDebugEnabled()) log.debug("Maximum transfer amount exceeded");
                throw new CFSFault(CFSFault.FLT_EXCEEDS_MAX_TRANSFER_AMOUNT, new Exception(ActionCode.EXCEEDS_MAX_TRANSFER_AMOUNT));
            }
        } catch (SQLException e) {
            log.error("Can not run SQL Statement: " + e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            log.warn(e);
            throw new CFSFault(CFSFault.FLT_CUSTOMER_NOT_FOUND, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        amountField = cfg.get(CFSConstants.AMOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.SRC_ACCOUNT;
        }

        if ((amountField == null) || (amountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(amount)");
            amountField = Fields.AMOUNT;
        }
    }
}
