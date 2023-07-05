package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Dec 23 2019
 * Time: 11:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class SetDepositStatus extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        BranchMessage bm = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            String statusE = null;
            String statusB = null;
            String messageType = null;
            String accountStatus = bm.getAccountStatus();

            if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_ALL_REQUEST)) {
                statusE = Constants.UNBLOCKED_E_STATUS;
                statusB = Constants.UNBLOCK_STATUS_FLAG;
                messageType = Fields.UNBLOCK_DEPOSIT;

            } else if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_NON_BRANCH_REQUEST)) {
                statusE = Constants.UNBLOCK_STATUS_FLAG;
                messageType = Fields.UNBLOCK_DEPOSIT;

            } else if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_BRANCH_REQUEST)) {
                statusB = Constants.UNBLOCK_STATUS_FLAG;
                messageType = Fields.UNBLOCK_DEPOSIT;

            } else if (accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_NON_BRANCH_REQUEST)) {
                statusE = Constants.BLOCK_STATUS_FLAG;
                messageType = Fields.BLOCK_DEPOSIT;

            } else if (accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_BRANCH_REQUEST)) {
                statusB = Constants.BLOCK_STATUS_FLAG;
                messageType = Fields.BLOCK_DEPOSIT;

            } else if (accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_ALL_REQUEST)) {
                statusB = Constants.BLOCK_STATUS_FLAG;
                statusE = Constants.BLOCK_STATUS_FLAG;
                messageType = Fields.BLOCK_DEPOSIT;

            } else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
            }

            String sessionId = String.valueOf(msg.getAttribute(Fields.SESSION_ID));
            String channelType = msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
            String description = bm.getDocumentDescription();
            if (description != null && !description.equalsIgnoreCase(""))
                description = description.trim();


            ChannelFacadeNew.updateDepositStatus(bm.getAccountNo(), sessionId, bm.getTerminalId(),
                    bm.getUserId(), bm.getBranchCode(), statusE, statusB, messageType, channelType, description);
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside SetDepositStatus.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
