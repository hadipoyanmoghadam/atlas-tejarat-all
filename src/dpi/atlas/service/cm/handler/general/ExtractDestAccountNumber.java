package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.model.tj.entity.CustomerServiceNew;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.sql.SQLException;

/**
 * Created by Behnaz (87/03/08).
 * To change this template use File | Settings | File Templates.
 */
public class ExtractDestAccountNumber extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(ExtractDestAccountNumber.class);

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        try {
            String utilityTypeCode = (String) msg.getAttribute(Fields.BLPY_UTILITY_TYPE_CODE);
            String subSidiaryCompanyCode = (String) msg.getAttribute(Fields.BLPY_SUBSIDIARY_CODE);
            String destAcc = ChannelFacadeNew.getBillAccount(subSidiaryCompanyCode, utilityTypeCode);
            destAcc  =  ISOUtil.zeropad(destAcc, 13);

            if (destAcc == null || "".equals(destAcc)) {
                msg.setAttribute(Fields.APPROVED, Constants.NOTAUTHORISED);
                msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
                throw new CMFault(CMFault.FAULT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
            }
            msg.setAttribute(Fields.APPROVED, Constants.AUTHORISED);
            msg.setAttribute(Fields.AUTHORISED, Constants.AUTHORISED);
            msg.setAttribute(Fields.DEST_ACCOUNT_NO, destAcc);

            if((command.getParam(Fields.SERVICE_TYPE).equals(Constants.ISO_SERVICE)  &&
                    command.getCommandName().equals(TJCommand.CMD_BILL_PAYMENT_VERIFICATION)))
                return;
            CustomerServiceNew destAccount = ChannelFacadeNew.findCustomerAccountSrv(destAcc);

            if (destAccount.getStatusMelli() == Constants.MELLICODE_INVALID) {
                log.debug("Melli Code Not Found.");
                msg.setAttribute(Params.ACTION_CODE, ActionCode.MELLICODE_NOT_FOUND);
                throw new ServerAuthenticationException(new Exception(ActionCode.MELLICODE_NOT_FOUND));
            }

            if (command.getHeaderParam(Fields.SERVICE_TYPE).equals(Constants.ISO_SERVICE)) {
                command.addParam(Constants.DEST_HOST_ID, String.valueOf(destAccount.getHostId()).trim());
                msg.setAttribute(Constants.DEST_HOST_ID, String.valueOf(destAccount.getHostId()));
                command.addParam(Fields.TOTAL_DEST_ACCOUNT, command.getParam(Fields.DEST_ACCOUNT));
                command.addParam(Fields.DEST_ACCOUNT, destAcc);
                command.addHeaderParam(Fields.DEST_ACCOUNT, destAcc);
                msg.setAttribute(Constants.DEST_ACCOUNT_NATURE, String.valueOf(destAccount.getAccountNature()));
            } else {
                command.addParam(Fields.DEST_ACCOUNT, destAcc);
                msg.setAttribute(Constants.DEST_HOST_ID, String.valueOf(destAccount.getHostId()));
                command.addParam(Constants.DEST_HOST_ID, String.valueOf(destAccount.getHostId()));
                AccountData destAccountData = new AccountData(destAccount.getAccountNo(), AccountData.ACCOUNT_CFS_CUSTOMER, AccountData.ACCOUNT_BOTH_CREDIT_DEBIT, destAccount);
                holder.put(Constants.DEST_ACCOUNT_DATA, destAccountData);
            }


        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.debug(" No AccountNo Exist");
            msg.setAttribute(Fields.APPROVED, Constants.NOTAUTHORISED);
            msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_FOR_BAR_CODE_BILL_PAYMENT);
            throw new CMFault(CFSFault.FLT_BILL_PAYMENT_NO_ACCOUNT_FOR_BARCODE, new Exception(ActionCode.NO_ACCOUNT_FOR_BAR_CODE_BILL_PAYMENT));
        }
        catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Fields.APPROVED, Constants.NOTAUTHORISED);
            msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        } catch (ISOException e) {
            msg.setAttribute(Fields.APPROVED, Constants.NOTAUTHORISED);
            msg.setAttribute(Fields.AUTHORISED,Constants.NOTAUTHORISED);
            throw new CMFault(CMFault.FAULT_EXTERNAL,ActionCode.GENERAL_ERROR);
        } catch (ServerAuthenticationException e) {
                    throw new CMFault(CMFault.FAULT_INTERNAL);
        }

    }
}



