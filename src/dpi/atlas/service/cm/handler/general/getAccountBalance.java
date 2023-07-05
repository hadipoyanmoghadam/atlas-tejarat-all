package dpi.atlas.service.cm.handler.general;

import dpi.atlas.core.Account;
import dpi.atlas.core.AccountsList;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.host.globus.ofs.convertor.NumberConvertor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.util.Map;

/**
 * getAccountBalance class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.5 $ $Date: 2007/10/29 14:04:24 $
 */
public class getAccountBalance extends CMHandlerBase implements Configurable {

    private static Log log = LogFactory.getLog(getAccountBalance.class);


    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside GetBalanceHandler:process()");

        ISOMsg isoRes = null;

        isoRes = (ISOMsg) holder.get("iso-res");
//        isoRes.dump(System.out,"");

        AccountsList accountsList = null;

        if (isoRes.hasField(Account.ACCOUNT_FEILD_NUMBER))
            accountsList = new AccountsList(isoRes.getString(Account.ACCOUNT_FEILD_NUMBER));
        else {
            throw new CMFault("No Account Information");
        }


        try {
            Account selectedAccount = ((Account) accountsList.getAccounts().iterator().next());
            String bal = NumberConvertor.convertFromISO(selectedAccount.getAccountAvailableBalance());

            isoRes.set(4, bal);
        } catch (ISOException e) {
            log.error("Can not set amount to ISO msg,RRN : " + isoRes.getString(37));
        }

//        isoRes.dump(System.out,"");
        holder.put("iso-res", isoRes);
    }


    public void setConfiguration(Configuration cfg) throws ConfigurationException {
    }
}

