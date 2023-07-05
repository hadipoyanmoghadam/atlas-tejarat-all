package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.tj.entity.CardAccount;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:20 $
 */

public class GetCardAccounts extends CFSHandlerBase implements Configurable {

    private String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {


        String cardNo = msg.getAttributeAsString(Fields.PAN);
        Collection cardAccounts = null;


        try {
            cardAccounts = CFSFacadeNew.getCardAccounts(cardNo);
        }
        catch (SQLException me) {
            log.error(me);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, me);
        }

        ArrayList accoutsList = new ArrayList();
        Iterator it = cardAccounts.iterator();
        try {
            while (it.hasNext()) {
                CardAccount cardAccount = (CardAccount) it.next();
                dpi.atlas.model.tj.entity.Account account =CFSFacadeNew.getAccount(cardAccount.getAccountNo());
                if (cardAccount.getMaxTransLimit().longValue() != CFSConstants.IGNORE_MAX_TRANS_LIMIT)
                    account.setAccountBalance(Math.min(account.getAccountBalance(), cardAccount.getMaxTransLimit().longValue()));
                accoutsList.add(account);

            }
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, e);
        }

        holder.put("accounts", accoutsList);
        //holder.put("accounts", cardAccounts);

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
