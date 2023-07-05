package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Nov 10, 2013
 * Time: 03:39:21 PM
 */
public class ChangeAccountStatus4Branch extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String acc = msg.getAttributeAsString(accountField).trim();
        try {
            if (acc.length() < 13)
                acc = ISOUtil.zeropad(acc, 13);
        } catch (ISOException e) {
            log.error(e);
        }

        if (log.isDebugEnabled()) log.debug(accountField + "=" + acc);

        int status = Integer.parseInt(msg.getAttributeAsString(Fields.ACCOUNT_STATUS));

        try {
            String branchId = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
            String serviceType = (String) msg.getAttribute(Fields.SERVICE_TYPE);
            String chnUser="";
            Tx tx=(Tx) msg.getAttribute(Constants.WAGE_TX);
            String sgbBranchId = CFSFacadeNew.getSgbBranchId(acc, status);
            if (serviceType.equals(Constants.BRANCH_SERVICE)) {
                chnUser = Constants.CHN_USER_BRANCH;
                if (!branchId.equalsIgnoreCase(sgbBranchId)) {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                    throw new CFSFault(CFSFault.FLT_TRANSACTION_NOT_PERMITTED_TO_TERMINAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                }
            } else { //Simin
                chnUser = Constants.CHN_USER_SIMIN;
            }
            String terminalID = (String) msg.getAttribute(Fields.TERMINAL_ID);
            if (terminalID == null) terminalID = "";

            String userID = (String) msg.getAttribute(Fields.USER_ID);
            if (userID == null) userID = "";

            String sessionId=(String)msg.getAttribute(Fields.SESSION_ID);
            String documentDescribtion = msg.getAttributeAsString(Fields.DOCUMENT_DESCRIPTION).trim();

            String documentStatus=msg.getAttributeAsString(Fields.WAGE_WITH_DOCUMENT);
            if(documentStatus==null)
                documentStatus="";

            boolean havingDocument=CFSFacadeNew.updateAccountStatus(acc, status, sessionId, terminalID, userID, branchId, chnUser,documentDescribtion,tx,documentStatus);
            if (havingDocument==true) {
                holder.put(Fields.SOURCE_ACCOUNT_BALANCE, tx.getSrc_account_balance());
                holder.put(Fields.DEST_ACCOUNT_BALANCE, tx.getDest_account_balance());
                msg.setAttribute(Fields.HAVING_DOCUMENT, Constants.TRUE);
            } else
                msg.setAttribute(Fields.HAVING_DOCUMENT, Constants.FALSE);

        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        } catch (ServerAuthenticationException e) {
            if (status == 1) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_ENABLED_BEFORE);
                throw new CFSFault(CFSFault.FLT_ENABLED_BEFORE, new Exception(ActionCode.ACCOUNT_ENABLED_BEFORE));
            } else if (status == 0) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_DISABLED_BEFORE);
                throw new CFSFault(CFSFault.FLT_DISABLED_BEFORE, new Exception(ActionCode.ACCOUNT_DISABLED_BEFORE));
            }
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
