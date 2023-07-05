package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Branch;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
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
 * @author <a href="mailto:behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.0 $ $Date: 2015/06/08 11:17:49 $
 */

public class GetGiftCardAccounts extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String branchCode = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
        String accountNo = "";

        Branch branch;
        try {
            if (log.isDebugEnabled()) log.debug("branchCode=" + branchCode);
            if (!branchCode.equals(""))
                branch = CFSFacadeNew.getGiftCardAccounts(branchCode);
            else
                throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);

            if(branch.getBranch_credit_acc().equals("") || branch.getCenter_credit_acc().equals(""))
                throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);

            msg.setAttribute(Fields.E_CONFLICT_ACCOUNT, branch.getE_conflict_acc());
            msg.setAttribute(Fields.CENTER_CREDIT_ACCOUNT, branch.getCenter_credit_acc());
            msg.setAttribute(Fields.BRANCH_CREDIT_ACCOUNT, branch.getBranch_credit_acc());
            if ( branch.getBatch_credit_acc()!= null && !branch.getBatch_credit_acc().equals(""))
                msg.setAttribute(Fields.BATCH_CREDIT_ACCOUNT, branch.getBatch_credit_acc());

            if (msg.getAttributeAsString(Fields.MESSAGE_TYPE) != null && !msg.getAttributeAsString(Fields.MESSAGE_TYPE).equals("") &&
                    msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_GIFTCARD)) {

                if (branch.getE_conflict_acc().equals("") || branch.getAccountNo().equals(""))
                    throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);

                accountNo = msg.getAttributeAsString(Fields.SRC_ACCOUNT);
                accountNo = ISOUtil.zeropad(accountNo, 13);
                if (!branch.getE_conflict_acc().equals(accountNo))
                    throw new CFSFault(CFSFault.FLT_TRANSACTION_NOT_PERMITTED_TO_TERMINAL, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                else {
                    msg.setAttribute(accountField, branch.getE_conflict_acc());
                    msg.setAttribute(Fields.TOTAL_DEST_ACCOUNT, branch.getAccountNo());
                }

            }else if (msg.getAttributeAsString(Fields.MESSAGE_TYPE) != null && !msg.getAttributeAsString(Fields.MESSAGE_TYPE).equals("") &&
                    msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase(TJCommand.CMD_BRANCH_DISCHARGE_GIFTCARD)) {

                if (branch.getE_conflict_acc().equals(""))
                    throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);

                accountNo = msg.getAttributeAsString(accountField);
                accountNo = ISOUtil.zeropad(accountNo, 13);
                if (!branch.getE_conflict_acc().equals(accountNo))
                    throw new CFSFault(CFSFault.FLT_TRANSACTION_NOT_PERMITTED_TO_TERMINAL, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                else {
                    msg.setAttribute(accountField, branch.getE_conflict_acc());
                    msg.setAttribute(Fields.TOTAL_DEST_ACCOUNT, branch.getE_conflict_acc());
                }
            }else if (msg.getAttributeAsString(Fields.MESSAGE_TYPE) != null && !msg.getAttributeAsString(Fields.MESSAGE_TYPE).equals("") &&
                    msg.getAttributeAsString(Fields.MESSAGE_TYPE).equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT_BATCH_GIFTCARD)) {
                if (branch.getBatch_credit_acc().equals(""))
                    throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);

            }else {
                msg.setAttribute(accountField, branch.getBranch_credit_acc());
            }

        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);
        } catch (SQLException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + accountNo + "' in GetGiftCardAccounts : " + e.getMessage());
        }


    }


    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            log.fatal("Account field is not Specified");
            throw new ConfigurationException("Account field is not Specified");
        }
    }
}
