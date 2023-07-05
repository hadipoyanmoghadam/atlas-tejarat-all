package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;


public class CheckCardAccount4Revoke extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {
            AccountData accountData = (AccountData) holder.get(accountField);
            Account account = (Account) accountData.getAccountHolder();

            Long maxTransLimit = (Long) holder.get(Fields.MAX_TRANS_LIMIT);

            if (!msg.getAttribute(Fields.CARD_TYPE).equals(CFSConstants.NORMAL_CARD) || maxTransLimit.longValue() == CFSConstants.IGNORE_MAX_TRANS_LIMIT)
                throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));

            if (account.getAccountBalance() < maxTransLimit.longValue()) {
                log.debug("Insufficient funds for account " + account.getAccountNo());
                holder.remove(accountField);
                throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, new Exception(ActionCode.NOT_SUFFICIENT_FUNDS));
            }

        } catch (CFSFault cfsFault) {
            throw cfsFault;

        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, new Exception(ActionCode.GENERAL_ERROR));

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