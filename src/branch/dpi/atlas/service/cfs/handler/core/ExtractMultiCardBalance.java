package branch.dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @version $Revision: 1.6 $ $Date: 2007/10/30 14:09:50 $
 */

public class ExtractMultiCardBalance extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String transactionSource = (String) msg.getAttribute(Fields.SERVICE_TYPE);
        String srcHostId = (String) msg.getAttribute(Constants.SRC_HOST_ID);

        if (log.isDebugEnabled()) log.debug("transactionSource:" + transactionSource);
        if (log.isDebugEnabled()) log.debug("srcHostId:" + srcHostId);

        boolean isSrcHostCFS = srcHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);
        if (isSrcHostCFS){

                Long maxtTransLimit = (Long) holder.get(Fields.MAX_TRANS_LIMIT);
                if (maxtTransLimit.longValue() != CFSConstants.IGNORE_MAX_TRANS_LIMIT) {
                    AccountData accData = (AccountData) holder.get(accountField);
                    long newBal = Math.min(accData.getAccountBalance(), maxtTransLimit.longValue());
                    log.info("newBal = " + newBal);

                    accData.setAccountBalance(newBal);
                    holder.put(accountField, accData);
                }
        }
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
