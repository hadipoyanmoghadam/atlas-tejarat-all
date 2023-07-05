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
 * Date: Nov 12, 2018
 * Time: 04:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class UnConditionalRevokeAccount extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(UnConditionalRevokeAccount.class);
    protected String accountField;

    public UnConditionalRevokeAccount() {
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
            log.error("Can not zeropad account number = '" + acc + "' in UnConditionalRevokeAccount : " + e.getMessage());
        }
        try {

            String sessionId=(String)msg.getAttribute(Fields.SESSION_ID);
            String channelType=msg.getAttributeAsString(Fields.SERVICE_TYPE);

            int result=CFSFacadeNew.unConditionalRevokeAccount(acc, branchId,nationalCode,externalIdNumber,sessionId,channelType);

            switch (result){
                case 0: //Approve
                    break;
                case 1: // revoke account before
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_DISABLED_BEFORE);
                    throw new CFSFault(CFSFault.FLT_DISABLED_BEFORE, new Exception(ActionCode.ACCOUNT_DISABLED_BEFORE));
                case 2: //REVOKE group or gift account is invalid
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));
                case 4: //REVOKE_ROW is invalid
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));
                case 6: //Account Not Found in SRV
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
                    throw new CFSFault(CFSFault.FLT_CUSTOMER_NOT_FOUND, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
                case 7: //Account Not Found in customeraccounts
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
                    throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
                case 10: //Account not found in custacc
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ROW_NOT_FOUND);
                    throw new CFSFault(CFSFault.FLT_ROW_NOT_FOUND, new Exception(ActionCode.ROW_NOT_FOUND));
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
