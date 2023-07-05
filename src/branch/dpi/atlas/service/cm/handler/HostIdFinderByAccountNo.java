package branch.dpi.atlas.service.cm.handler;


import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.entity.cms.Cmparam;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.entity.CustomerServiceNew;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;


/**
 * User: R.Nasiri
 * Date: July 10, 2019
 * Time: 03:23 PM
 */
public class HostIdFinderByAccountNo extends TJServiceHandler implements Configurable {

    ArrayList Checked_Dest_Account = new ArrayList();
    AmxMessage amxMessage;
    String eStatus = "";

    public void setConfiguration(Configuration configuration) throws ConfigurationException {

        String getDestAccount = configuration.get("GetDestAccount");
        Checked_Dest_Account = CMUtil.tokenizString(getDestAccount, ",");
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String pin = amxMessage.getPin();

        msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.TRUE);

        String accountNo = command.getParam(Fields.SRC_ACCOUNT);
        String destAccountNo = command.getParam(Fields.DEST_ACCOUNT);
        try {
            if (accountNo == null || "".equals(accountNo.trim()))
                throw new NotFoundException(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            
            if (Checked_Dest_Account.contains(pin) && destAccountNo != null && !"".equals(destAccountNo))
                getSrcAndDestAccountHostID(msg, destAccountNo, accountNo,pin);


            boolean isReversed = (Boolean)msg.getAttribute(Constants.ISREVERSED);
            if (eStatus.equals(Constants.E_STATUS_CLOSE) &&  !isReversed ) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_IS_E_BLOCKED);
                throw new ServerAuthenticationException(new Exception(ActionCode.ACCOUNT_IS_E_BLOCKED));
            }

            if (msg.getAttribute(Constants.IS_LOCAL_CURRENCY).equals(Constants.FALSE)) {
                log.debug("Account Group is not in Local Currency Range.");
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_RANGE_NOT_FOUND);
                throw new ServerAuthenticationException(new Exception(ActionCode.ACCOUNT_RANGE_NOT_FOUND));
            }

            msg.setAttribute(Fields.HOST_ID, command.getParam(Constants.SRC_HOST_ID));
            command.addHeaderParam(Fields.SRC_ACCOUNT, accountNo);
            command.addHeaderParam(Fields.DEST_ACCOUNT, command.getParam(Fields.DEST_ACCOUNT));

        } catch (NotFoundException e) {
            log.debug("Customer with Account No = " + accountNo + " or with Account No = " + destAccountNo + "  Not Found");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (ServerAuthenticationException e) {
            throw new CMFault(CMFault.FAULT_INTERNAL);
        } catch (SQLException e) {
            log.error("DB has encountered an exception during fetching data from tbcustomersrv...");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_DATA_ERROR));
        } catch (Exception e) {
            log.error("Exception"+ e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

    private void getSrcAndDestAccountHostID(CMMessage msg, String destAccountNo, String accountNo,String pin) throws Exception {
        byte statusMelliSRC;
        byte statusMelliDEST;

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        Map accounts = ChannelFacadeNew.findAccountHost(accountNo, destAccountNo);

        CustomerServiceNew srcAccountData = (CustomerServiceNew) accounts.get(Constants.ACCOUNT_DATA);

        String srcHostId = String.valueOf(srcAccountData.getHostId()).trim();
        String srcAccountNature = String.valueOf(srcAccountData.getAccountNature()).trim();
        String srcAccountGroup = srcAccountData.getAccountGroup();
        String srcSMSNotification = srcAccountData.getSmsNotification().trim();
        statusMelliSRC = srcAccountData.getStatusMelli();
        eStatus = srcAccountData.geteStatus();

        CustomerServiceNew destAccountData = (CustomerServiceNew)accounts.get(Constants.DEST_ACCOUNT_DATA);

        String destHostId = String.valueOf(destAccountData.getHostId()).trim();
        String destAccountNature = String.valueOf(destAccountData.getAccountNature()).trim();
        String destAccountGroup = destAccountData.getAccountGroup();
        String destSMSNotification = destAccountData.getSmsNotification().trim();
        statusMelliDEST = destAccountData.getStatusMelli();

        if(srcAccountGroup == null || destAccountGroup == null){
            log.error("Account Group is not specified for account = " + accountNo + " OR account = " + destAccountNo);
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new ServerAuthenticationException(new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        }

        if (pin != null && !pin.equalsIgnoreCase(Constants.PIN_FOLLOW_UP_SAFE_BOX)) {

            if (srcHostId.equals(String.valueOf(Constants.HOST_SGB)) && !amxMessage.getRequestType().equalsIgnoreCase(Constants.STAMP_DOCUMENT)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_SRC_HOST);
                throw new ServerAuthenticationException(ActionCode.INVALID_SRC_HOST);
            }
            if (destHostId.equals(String.valueOf(Constants.HOST_SGB)) && !amxMessage.getRequestType().equalsIgnoreCase(Constants.STAMP_DOCUMENT)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_DST_HOST);
                throw new ServerAuthenticationException(ActionCode.INVALID_DST_HOST);
            }
        }

        command.addParam(Constants.SRC_HOST_ID, srcHostId);
        msg.setAttribute(Constants.SRC_HOST_ID, srcHostId);
        command.addParam(Constants.DEST_HOST_ID, destHostId);
        msg.setAttribute(Constants.DEST_HOST_ID, destHostId);
        msg.setAttribute(Constants.DEST_ACCOUNT_NATURE, destAccountNature);
        msg.setAttribute(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);
        msg.setAttribute(Constants.SRC_ACCOUNT_GROUP, srcAccountGroup.trim());
        msg.setAttribute(Constants.DEST_ACCOUNT_GROUP, destAccountGroup.trim());
        command.addParam(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);
        command.addParam(Constants.DEST_SMS_NOTIFICATION, destSMSNotification);

        if (statusMelliSRC == Constants.MELLICODE_INVALID || statusMelliDEST == Constants.MELLICODE_INVALID) {
            log.debug("Melli Code Not Found.");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.MELLICODE_NOT_FOUND);
            throw new ServerAuthenticationException(new Exception(ActionCode.MELLICODE_NOT_FOUND));
        }

        Vector currencyGroup = ChannelFacadeNew.getCMParam("CurrencyGroup");
        if (currencyGroup == null || currencyGroup.size() == 0)
            throw new NotFoundException("currencyGroup Not Found. ");
        for (Object aCurrencyGroup : currencyGroup) {
            Cmparam param = (Cmparam) aCurrencyGroup;
            if (Integer.parseInt(srcAccountGroup.trim()) == param.getId() ||
                    Integer.parseInt(destAccountGroup.trim()) == param.getId()) {
                msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.FALSE);
                break;
            }
        }
    }
}
