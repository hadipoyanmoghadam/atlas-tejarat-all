package branch.dpi.atlas.service.cm.handler.manzume;

import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.entity.CustomerServiceNew;
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
 * Date: Nov 06, 2022
 * Time: 9:34:35 AM
 */
public class HostIdFinderByAccountNo extends CMHandlerBase {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            ManzumeMessage manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

            CustomerServiceNew srcAccountData = ChannelFacadeNew.findCustomerAccountSrv(manzumeMessage.getAccountNo());
            String hostId = String.valueOf(srcAccountData.getHostId()).trim();
            String statusD = String.valueOf(srcAccountData.getStatusD());
            String srcSMSNotification = srcAccountData.getSmsNotification().trim();
            String accoutGroup=srcAccountData.getAccountGroup().trim();
            int accountNature = srcAccountData.getAccountNature();
            int statusMeli = srcAccountData.getStatusMelli();

            String pin = manzumeMessage.getPin().trim();

            if (hostId.equals(Constants.HOST_ID_CFS) || hostId.equals(Constants.HOST_ID_FARAGIR)) {
                if (pin.equals(Constants.PIN_ACCOUNT_INFO_MANZUME)) {
                    if (accountNature != 0) {
                        manzumeMessage.setAccountType(Constants.HAS_ID);
                    } else {
                        manzumeMessage.setAccountType(Constants.HAS_NOT_ID);
                    }
                }

                if (pin.equals(Constants.PIN_DEPOSIT_MANZUME)) {
                    if (statusMeli == 0) {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                        throw new ServerAuthenticationException(new Exception(ActionCode.INVALID_OPERATION));
                    }

                }
                msg.setAttribute(Fields.STATUS_D, statusD);
                msg.setAttribute(Fields.HOST_ID, hostId);
                msg.setAttribute(Fields.ACCOUNT_NO, manzumeMessage.getAccountNo());
                msg.setAttribute(Constants.DEST_ACCOUNT_NATURE, String.valueOf(accountNature));
                msg.setAttribute(Fields.SMS_NOTIFICATION, srcSMSNotification);
                msg.setAttribute(Fields.ACCOUNT_GROUP, accoutGroup);
            } else if (hostId.equals(Constants.HOST_ID_SGB)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_DST_HOST);
                throw new ServerAuthenticationException(new Exception(ActionCode.INVALID_DST_HOST));
            } else if (hostId.equals(Constants.HOST_ID_UNKNOWN)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                throw new ModelException(new Exception(ActionCode.ACCOUNT_NOT_FOUND));
            } else if (hostId.equals(Constants.HOST_ID_MANZOOMEH)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_DST_HOST);
                throw new ServerAuthenticationException(new Exception(ActionCode.INVALID_DST_HOST));
            } else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_DST_HOST);
                throw new ServerAuthenticationException(new Exception(ActionCode.INVALID_DST_HOST));
            }


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

        } catch (Exception e) {
            log.error("ERROR :::Inside HostIdFinderByAccountNo.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
