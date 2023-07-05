package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * User: salehi
 * Email: ebrahim_slh@yahoo.com
 * Date: Mar 4, 2006
 * Time: 9:14:34 AM
 */
public class CheckNonCardAccDigits extends CFSHandlerBase {
    private String accountField;
                  
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String acc = ((String) msg.getAttribute(accountField)).substring(3);
        if (log.isDebugEnabled()) log.debug("acc=" + acc);

        String totalAccount = (String) msg.getAttribute(Fields.TOTAL_DEST_ACCOUNT);


        int sum = 0;
        for (int i = 10; i >= 4; i--)
            sum += Integer.parseInt(acc.trim().substring(i - 1, i)) * (11 - i);

        sum += Integer.parseInt(acc.trim().substring(0, 1)) * 4;
        sum += Integer.parseInt(acc.trim().substring(1, 2)) * 3;
        sum += Integer.parseInt(acc.trim().substring(2, 3)) * 2;

        if (Long.parseLong(totalAccount.substring(0, 3)) != 0 ||
                Long.parseLong(acc.trim()) == 0 ||
                ((sum % 11) != 0))
            throw new CFSFault(CFSFault.FLT_DESTINATION_ACCOUNT_INVALID, new Exception(ActionCode.DESTINATION_ACCOUNT_INVALID));
    }

    public CheckNonCardAccDigits() {
        accountField = Fields.DEST_ACCOUNT;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.DEST_ACCOUNT;
        }
    }
}
