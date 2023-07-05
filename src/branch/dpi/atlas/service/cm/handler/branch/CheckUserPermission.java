package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
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
 * Date: Dec 28 2019
 * Time: 04:54 PM
 */
public class CheckUserPermission extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        BranchMessage bm = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        try {

            String accountStatus = bm.getAccountStatus();

            if(!accountStatus.startsWith(Constants.UNBLOCK_PREFIX))
                return;

                if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_ALL_REQUEST)) {

                    if (!ChannelFacadeNew.checkUser(bm.getAccountNo(),Constants.BRANCH_POSTFIX,bm.getUserId()) ||
                            !ChannelFacadeNew.checkUser(bm.getAccountNo(),Constants.NON_BRANCH_POSTFIX,bm.getUserId())){

                        msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                    }
                } else if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_NON_BRANCH_REQUEST)) {
                    if (!ChannelFacadeNew.checkUser(bm.getAccountNo(),Constants.NON_BRANCH_POSTFIX,bm.getUserId())){

                        msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                    }

                } else if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_BRANCH_REQUEST)) {
                    if (!ChannelFacadeNew.checkUser(bm.getAccountNo(),Constants.BRANCH_POSTFIX,bm.getUserId())){

                        msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                    }
                } else {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
                }


        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CMFault(CMFault.FAULT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside SetServiceStatus.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
