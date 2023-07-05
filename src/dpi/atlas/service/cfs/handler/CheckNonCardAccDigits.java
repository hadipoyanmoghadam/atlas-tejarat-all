package dpi.atlas.service.cfs.handler;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
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
    private String accountDescription;
    private String onlineOrOffline;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String acc = (String) msg.getAttribute(accountDescription);
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
            throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));


    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        accountDescription = cfg.get(CFSConstants.ACCOUNT_DESCRIPTION);
        onlineOrOffline = cfg.get(CFSConstants.ONLINE_OR_OFFLINE);
        if ((accountDescription == null) || (accountDescription.trim().equals("")))
            log.fatal("Account Description is not Specified");
        if ((onlineOrOffline == null) || (onlineOrOffline.trim().equals("")))
            log.fatal("Online-or-offline is not Specified");
    }

}
