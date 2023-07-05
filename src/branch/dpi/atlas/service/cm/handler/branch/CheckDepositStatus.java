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
 * Date: Dec 24 2019
 * Time: 04:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class CheckDepositStatus extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        BranchMessage bm = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        try {

            String accountStatus = bm.getAccountStatus();
            String statusD[] = ChannelFacadeNew.getDepositStatus(bm.getAccountNo());

            if (statusD.length == 3) {

                if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_ALL_REQUEST)) {
                    if (statusD[0].equalsIgnoreCase(Constants.UNBLOCK_STATUS_FLAG) && statusD[1].equalsIgnoreCase(Constants.UNBLOCK_STATUS_FLAG)) {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_ENABLED_BEFORE);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_ENABLED_BEFORE));

                    } else if (statusD[0].equalsIgnoreCase(Constants.UNBLOCK_STATUS_FLAG) || statusD[1].equalsIgnoreCase(Constants.UNBLOCK_STATUS_FLAG)) {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                    } else {
                        msg.setAttribute(Fields.HOST_ID, statusD[2]);
                        msg.setAttribute(Fields.STATUS_D, statusD[0] + statusD[1]);
                    }

                } else if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_NON_BRANCH_REQUEST)) {
                    if (statusD[0].equalsIgnoreCase(Constants.UNBLOCK_STATUS_FLAG)) {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_ENABLED_BEFORE);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_ENABLED_BEFORE));
                    } else {
                        msg.setAttribute(Fields.HOST_ID, statusD[2]);
                        msg.setAttribute(Fields.STATUS_D, statusD[0] + statusD[1]);
                    }

                } else if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_BRANCH_REQUEST)) {
                    if (statusD[1].equalsIgnoreCase(Constants.UNBLOCK_STATUS_FLAG)) {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_ENABLED_BEFORE);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_ENABLED_BEFORE));
                    } else {
                        msg.setAttribute(Fields.HOST_ID, statusD[2]);
                        msg.setAttribute(Fields.STATUS_D, statusD[0] + statusD[1]);
                    }

                } else if (accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_NON_BRANCH_REQUEST)) {
                    if (statusD[0].equalsIgnoreCase(Constants.BLOCK_STATUS_FLAG)) {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_DISABLED_BEFORE);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_DISABLED_BEFORE));
                    } else {
                        msg.setAttribute(Fields.HOST_ID, statusD[2]);
                        msg.setAttribute(Fields.STATUS_D, statusD[0] + statusD[1]);
                    }

                } else if (accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_BRANCH_REQUEST)) {
                    if (statusD[1].equalsIgnoreCase(Constants.BLOCK_STATUS_FLAG)) {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_DISABLED_BEFORE);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_DISABLED_BEFORE));
                    } else {
                        msg.setAttribute(Fields.HOST_ID, statusD[2]);
                        msg.setAttribute(Fields.STATUS_D, statusD[0] + statusD[1]);
                    }

                } else if (accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_ALL_REQUEST)) {
                    if (statusD[0].equalsIgnoreCase(Constants.BLOCK_STATUS_FLAG) && statusD[1].equalsIgnoreCase(Constants.BLOCK_STATUS_FLAG)) {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_DISABLED_BEFORE);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_DISABLED_BEFORE));

                    } else if (statusD[0].equalsIgnoreCase(Constants.BLOCK_STATUS_FLAG) || statusD[1].equalsIgnoreCase(Constants.BLOCK_STATUS_FLAG)) {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));

                    } else if (accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_ALL_REQUEST) || accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_NON_BRANCH_REQUEST) ||
                            accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_BRANCH_REQUEST) || accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_NON_BRANCH_REQUEST) ||
                            accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_BRANCH_REQUEST) || accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_ALL_REQUEST)) {

                        msg.setAttribute(Fields.HOST_ID, statusD[2]);
                        msg.setAttribute(Fields.STATUS_D, statusD[0] + statusD[1]);
                    }

                } else {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
                }

            } else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
                throw new CMFault(CMFault.FAULT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
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
            log.error("ERROR :::Inside CheckDepositStatus.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
