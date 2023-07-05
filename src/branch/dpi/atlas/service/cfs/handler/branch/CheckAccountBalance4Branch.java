package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.model.tj.entity.Account;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

public class CheckAccountBalance4Branch extends CFSHandlerBase implements Configurable {

    protected String accountField;
    protected String amountField;


    public CheckAccountBalance4Branch() {
        accountField = Fields.SRC_ACCOUNT;
        amountField = Fields.AMOUNT;
    }


    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

       try{
        AccountData accountData = (AccountData) holder.get(accountField);
        dpi.atlas.model.tj.entity.Account account = (Account) accountData.getAccountHolder();

        if (account.getAccountBalance() < Long.parseLong((String) msg.getAttribute(amountField))) {
            log.debug("Insufficient funds for account " + account.getAccountNo());
            log.debug("Force Post: " + msg.getAttribute(Fields.FORCE_POST));
            holder.remove(accountField);
            throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, ActionCode.NOT_SUFFICIENT_FUNDS);
        }

       } catch (CFSFault cfsFault) {
               throw cfsFault;

       }catch(Exception  e){
        log.error(e);
        throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);

    }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.SRC_ACCOUNT;
        }

        amountField = cfg.get(CFSConstants.AMOUNT_FIELD);
        if ((amountField == null) || (amountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Amount Field is not Specified, set to default value(amount)");
            amountField = Fields.AMOUNT;

        }

    }

}
