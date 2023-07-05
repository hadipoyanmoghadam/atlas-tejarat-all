package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: March 13, 2021
 * Time: 11:36:03 AM
 */
public class HostIdFinderByAccountNo4CheckIsRevoke extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            String hostId = "";
            String statusD = "";
            String srcSMSNotification = "";
            String isRevoked = "";
            BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            String[] inquiry = ChannelFacadeNew.checkIsRevoke(branchMsg.getAccountNo());
            if (inquiry[0] != null) {
                isRevoked = inquiry[0];
                if (isRevoked.equals(Constants.NOT_REVOKED)) {
                    hostId = String.valueOf(inquiry[1]).trim();
                    statusD = String.valueOf(inquiry[2]).trim();
                    srcSMSNotification = inquiry[3].trim();
                } else {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.ALREADY_REVOKE);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ALREADY_REVOKE));
                }
            } else {
                throw new Exception("ERROR ::: in HostIdFinderByAccountNo4CheckIsRevoke.doProcess()!!");
            }

            if (hostId.equals(Constants.HOST_ID_SGB)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_SRC_HOST);
                throw new ServerAuthenticationException(new Exception(ActionCode.INVALID_SRC_HOST));
            }

            if (hostId.equals(Constants.HOST_ID_UNKNOWN)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                throw new ModelException(new Exception(ActionCode.ACCOUNT_NOT_FOUND));
            }

            msg.setAttribute(Fields.STATUS_D, statusD);
            msg.setAttribute(Fields.HOST_ID, hostId);
            msg.setAttribute(Fields.ACCOUNT_NO, branchMsg.getAccountNo());
            command.addParam(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));

        } catch (ServerAuthenticationException e) {
            throw new CMFault(CMFault.FAULT_INTERNAL);

        } catch (ModelException e) {
            throw new CMFault(CMFault.FAULT_INTERNAL);

        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));

        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside HostIdFinderByAccountNo.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}
