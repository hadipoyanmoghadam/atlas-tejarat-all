package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Nov 05, 2018
 * Time: 03:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class InquiryRevokeAccount extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(InquiryRevokeAccount.class);
    protected String accountField;

    public InquiryRevokeAccount() {
        accountField = Fields.SRC_ACCOUNT;
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        String acc = msg.getAttributeAsString(accountField);
        String nationalCode=msg.getAttributeAsString(Fields.NATIONAL_CODE);
        String externalIdNumber= msg.getAttributeAsString(Fields.EXTERNAL_ID_NUMBER);
        String branchId = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();

        try {
            acc = ISOUtil.zeropad(acc, 13);
            if (log.isDebugEnabled()) log.debug(accountField + "=" + acc);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + acc + "' in InquiryRevokeAccount : " + e.getMessage());
        }
        try {

            int result=CFSFacadeNew.inquiryRevokeAccount(acc, branchId,nationalCode,externalIdNumber);

            switch (result){
                case 0: //Approve
                    break;
                case 1: // revoke account before
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_DISABLED_BEFORE);
                    throw new CFSFault(CFSFault.FLT_DISABLED_BEFORE, new Exception(ActionCode.ACCOUNT_DISABLED_BEFORE));
                case 2: // account has balance
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_HAS_BALANCE);
                    throw new CFSFault(CFSFault.FLT_ACCOUNT_HAS_BALANCE, new Exception(ActionCode.ACCOUNT_HAS_BALANCE));
                case 3: // TRANSACTION_NOT_PERMITTED_TO_TERMINAL
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                    throw new CFSFault(CFSFault.FLT_TRANSACTION_NOT_PERMITTED_TO_TERMINAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                case 4: //ROW_ASSIGNED_TO_CARD
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ROW_ASSIGNED_TO_CARD);
                    throw new CFSFault(CFSFault.FLT_ROW_ASSIGNED_TO_CARD, new Exception(ActionCode.ROW_ASSIGNED_TO_CARD));
                case 5: //ACCOUNT_IS_E_BLOCKED
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_IS_E_BLOCKED);
                    throw new CFSFault(CFSFault.FLT_ACCOUNT_IS_E_BLOCKED, new Exception(ActionCode.ACCOUNT_IS_E_BLOCKED));
                case 6: //Account Not Found in SRV
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
                    throw new CFSFault(CFSFault.FLT_CUSTOMER_NOT_FOUND, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
                case 7: //Account Not Found in customeraccounts
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
                    throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
                case 8: //Account has block amount
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_HAS_BLOCKED_AMOUNT);
                    throw new CFSFault(CFSFault.FLT_ACCOUNT_HAS_BLOCKED_AMOUNT, new Exception(ActionCode.ACCOUNT_HAS_BLOCKED_AMOUNT));
                case 9: //Account is blocked
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_BAD_STATUS);
                    throw new CFSFault(CFSFault.ACC_BAD_STATUS, new Exception(ActionCode.ACCOUNT_BAD_STATUS));
                case 10: //Account not found in custacc
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ROW_NOT_FOUND);
                    throw new CFSFault(CFSFault.FLT_ROW_NOT_FOUND, new Exception(ActionCode.ROW_NOT_FOUND));
                case 11: //invalid operation
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));
                case 12: // Account is deposit block
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT);
                    throw new CFSFault(CFSFault.FLT_ACCOUNT_IS_DEPOSIT_BLOCK, new Exception(ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT));
                default:
                    log.error("ERROR:: invalid result");
                    throw new Exception();
            }
        } catch (CFSFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }
        msg.setAttribute(CMMessage.RESPONSE, command);
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
