package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @version $Revision: 1.10 $ $Date: 2007/10/30 14:13:31 $
 * Update: SH.Behnaz
 */

public class CheckOperationValidity extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        String txCode = (String) msg.getAttribute(Fields.MESSAGE_TYPE);

        if (txCode.equalsIgnoreCase(TJCommand.CMD_FIVE_TRANSATION_LIST) || txCode.equalsIgnoreCase(TJCommand.CMD_CARD_STATEMENT)) {
            AccountData accountData = (AccountData) holder.get(accountField);
            Account account = (Account) accountData.getAccountHolder();
            String strAccountType = account.getAccountType();
            if ((CFSConstants.GIFT_CARD_007).equalsIgnoreCase(strAccountType))
                throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));

        } else if (txCode.equalsIgnoreCase(TJCommand.CMD_TRANSFER_FUND) ||
                txCode.equalsIgnoreCase(TJCommand.ACH_FUNDTRANSFER_CMD) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_TRANSFER_FUND_POS) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_PG) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_TEJ2TEJ_MP) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_KIOSK) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_POS) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_TEJ2TEJ_POS_BRANCH) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_POS_BRANCH) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_SAFE_BOX)|| txCode.equalsIgnoreCase(TJCommand.CMD_MANZUME_DEPOSIT))  {

            String destAccount = msg.getAttributeAsString(Fields.DEST_ACCOUNT);

            try {
                destAccount = ISOUtil.zeropad(destAccount, 13);
            } catch (ISOException e) {
                log.error("Can not zeropad account number = '" + destAccount + "' in CheckAccountNew : " + e.getMessage());
            }
            Account account = null;
            try {
                account = CFSFacadeNew.getAccount(destAccount);
                if (account.isLocked())
                    throw new CFSFault(CFSFault.FLT_LOCKED_ACCOUNT, ActionCode.ACCOUNT_BALANCE_INFORMATION_NOT_AVAILABLE);

                if (account.getAccountType().equalsIgnoreCase(CFSConstants.GIFT_CARD_007) && !txCode.equalsIgnoreCase(TJCommand.CMD_FUND_TRANSFER_SAFE_BOX))
                    throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));

            } catch (NotFoundException e) {
                if (log.isDebugEnabled()) log.debug("Destination Account Does Not Exist In CFS Accounts, " + e);

            } catch (SQLException e) {
                log.error(e);
                throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
            }

            if (account != null) holder.put(Fields.DEST_ACCOUNT, account.getAccountData());

        } else if (txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_NONTEJ2TEJ) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_NONTEJ2TEJ_PG) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_NONTEJ2TEJ_KIOSK) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_NONTEJ2TEJ_MP) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_NONTEJ2TEJ_POS) ||
                txCode.equalsIgnoreCase(TJCommand.CMD_NETWORK_FUND_TRANSFER_NONTEJ2TEJ_POS_BRANCH)) {
            
            String destAccount = msg.getAttributeAsString(Fields.DEST_ACCOUNT);
            String srcBin = msg.getAttributeAsString(Fields.SRC_BIN);
            String cardNo = msg.getAttributeAsString(Fields.PAN);
            try {
                destAccount = ISOUtil.zeropad(destAccount, 13);
            } catch (ISOException e) {
                log.error("Can not zeropad account number = '" + destAccount + "' in CheckAccountNew : " + e.getMessage());
            }

            Account account = null;
            try {
                account = CFSFacadeNew.getAccount(destAccount);
                if (account.isLocked())
                    throw new CFSFault(CFSFault.FLT_LOCKED_ACCOUNT, ActionCode.ACCOUNT_BALANCE_INFORMATION_NOT_AVAILABLE);
                
                if (!(srcBin.equalsIgnoreCase(msg.getAttributeAsString(CFSConstants.SOROSH_BIN)) && cardNo.equalsIgnoreCase(msg.getAttributeAsString(CFSConstants.SOROSH_PAN))) && account.getAccountType().equalsIgnoreCase(CFSConstants.GIFT_CARD_007))
                    throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));
            } catch (NotFoundException e) {
                if (log.isDebugEnabled()) log.debug("Destination Account Does Not Exist In CFS Accounts, " + e);

            } catch (SQLException e) {
                log.error(e);
                throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
            }
            if (account != null) holder.put(Fields.DEST_ACCOUNT, account.getAccountData());
        }
    }

    public void setConfiguration
            (Configuration
                    cfg) throws ConfigurationException {
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.SRC_ACCOUNT;
        }

    }

}
