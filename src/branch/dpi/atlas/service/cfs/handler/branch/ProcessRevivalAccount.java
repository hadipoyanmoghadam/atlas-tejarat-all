package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
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
 * Date: Feb 24, 2014
 * Time: 14:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessRevivalAccount extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(ProcessRevivalAccount.class);
    protected String accountField;

    public ProcessRevivalAccount() {
        accountField = Fields.SRC_ACCOUNT;
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        String acc = msg.getAttributeAsString(accountField);
        try {
            acc = ISOUtil.zeropad(acc, 13);
            if (log.isDebugEnabled()) log.debug(accountField + "=" + acc);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + acc + "' in ProcessRevivalAccount : " + e.getMessage());
        }
        try {

            String branchId = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
            String sessionId=msg.getAttributeAsString(Fields.SESSION_ID);
            String channelType=msg.getAttributeAsString(Fields.SERVICE_TYPE);
            CFSFacadeNew.doRevivalAccountTransactional(acc, branchId,sessionId,channelType);

        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } catch (ServerAuthenticationException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
            throw new CFSFault(CFSFault.FLT_TRANSACTION_NOT_PERMITTED_TO_TERMINAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
        } catch (ModelException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
            throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));
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
