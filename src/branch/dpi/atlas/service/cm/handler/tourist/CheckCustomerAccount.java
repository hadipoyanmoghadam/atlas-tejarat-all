package branch.dpi.atlas.service.cm.handler.tourist;

import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * User: R.Nasiri
 * Date: April 24, 2017
 * Time: 03:07 PM
 */
public class CheckCustomerAccount extends TJServiceHandler implements Configurable {

    ArrayList not_Checked = new ArrayList();
    ArrayList check_Dep_block = new ArrayList();
    TouristMessage touristMessage;
    String eStatus = "";
    String statusD = "";

    public void setConfiguration(Configuration configuration) throws ConfigurationException {

        String notChecked = configuration.get("notChecked");
        not_Checked = CMUtil.tokenizString(notChecked, ",");
        String checkDepBlock = configuration.get("checkDepBlock");
        check_Dep_block = CMUtil.tokenizString(checkDepBlock, ",");

    }

    public void doProcess(CMMessage msg, Map map) throws CMFault {

        if (msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equals("0"))
            return;

        touristMessage = (TouristMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String pin = touristMessage.getPin();

        if (not_Checked.contains(pin))
            return;
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.TRUE);
        String accountNo = (command.getParam(Fields.SRC_ACCOUNT) != null) ? command.getParam(Fields.SRC_ACCOUNT) :
                command.getParam(Fields.DEST_ACCOUNT);
        try {

            if (pin.equalsIgnoreCase(Constants.PIN_TOURIST_FUND_TRANSFER))
                checkSrcAndDestAccount(msg,command.getParam(Fields.SRC_ACCOUNT),command.getParam(Fields.DEST_ACCOUNT));
            else
                checkCustomerAccount(msg, accountNo, pin);

            if (msg.getAttribute(Constants.IS_LOCAL_CURRENCY).equals(Constants.FALSE)) {
                log.debug("Account Group is not in Local Currency Range.");
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_RANGE_NOT_FOUND);
                throw new ServerAuthenticationException(new Exception(ActionCode.ACCOUNT_RANGE_NOT_FOUND));
            }


            if (!eStatus.equals(Constants.E_STATUS_ACTIVE)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_IS_E_BLOCKED);
                throw new ServerAuthenticationException(new Exception(ActionCode.ACCOUNT_IS_E_BLOCKED));
            }
            if (check_Dep_block.contains(pin)) {
                if (statusD.endsWith(Constants.CM_BLOCK)) {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT);
                    throw new ServerAuthenticationException(new Exception(ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT));
                }
            }

        } catch (NotFoundException e) {
            log.debug("Customer Not Found.");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.CUSTOMER_NOT_FOUND);
        } catch (ServerAuthenticationException e) {
            throw new CMFault(CMFault.FAULT_INTERNAL);
        } catch (Exception e) {
            log.error("Exception in Check Customer account  for branch.");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, ActionCode.GENERAL_ERROR);
        }
    }

    private void checkCustomerAccount(CMMessage msg, String accountNo, String pin) throws Exception {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        CustomerServiceNew srcAccountData = ChannelFacadeNew.findCustomerAccountSrv(accountNo);
        String srcHostId = String.valueOf(srcAccountData.getHostId()).trim();
        String srcAccountNature = String.valueOf(srcAccountData.getAccountNature());
        String srcAccountGroup = srcAccountData.getAccountGroup().trim();
        String srcSMSNotification = srcAccountData.getSmsNotification().trim();
        statusD = srcAccountData.getSmsNotification().trim();
        eStatus = srcAccountData.geteStatus();

        if (!srcHostId.equals(String.valueOf(Constants.HOST_CFS))) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_SRC_HOST);
            throw new ServerAuthenticationException(ActionCode.INVALID_SRC_HOST);
        }
        if (pin.equals(Constants.PIN_TOURIST_CHARGE)) {
            command.addParam(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);
            command.addParam(Constants.SRC_HOST_ID, srcHostId);
            command.addParam(Constants.DEST_HOST_ID, " ");
            msg.setAttribute(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);
            msg.setAttribute(Constants.SRC_ACCOUNT_GROUP, srcAccountGroup);

        } else if (pin.equals(Constants.PIN_TOURIST_DISCHARGE) || pin.equals(Constants.PIN_TOURIST_REVOKE)) {
            command.addParam(Constants.DEST_SMS_NOTIFICATION, srcSMSNotification);
            command.addParam(Constants.DEST_HOST_ID, srcHostId);
            command.addParam(Constants.SRC_HOST_ID, " ");
            msg.setAttribute(Constants.DEST_ACCOUNT_NATURE, srcAccountNature);
            msg.setAttribute(Constants.DEST_ACCOUNT_GROUP, srcAccountGroup);
        }

        Vector currencyGroup = ChannelFacadeNew.getCMParam("CurrencyGroup");
        if (currencyGroup == null || currencyGroup.size() == 0 || srcAccountGroup == null || srcAccountGroup.equals("null"))
            throw new NotFoundException("currencyGroup Not Found. ");
        Iterator it = currencyGroup.iterator();
        while (it.hasNext()) {
            Cmparam param = (Cmparam) it.next();
            if (Integer.parseInt(srcAccountGroup) == param.getId()) {
                msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.FALSE);
                break;
            }
        }
    }

    private void checkSrcAndDestAccount(CMMessage msg, String srcAccountNo, String destAccountNo) throws Exception {
        byte statusMelliSRC;
        byte statusMelliDEST;

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        Map accounts = ChannelFacadeNew.findAccountHost(srcAccountNo, destAccountNo);

        CustomerServiceNew srcAccountData = (CustomerServiceNew) accounts.get(Constants.ACCOUNT_DATA);

        String srcHostId = String.valueOf(srcAccountData.getHostId()).trim();
        String srcAccountNature = String.valueOf(srcAccountData.getAccountNature());
        String srcAccountGroup = srcAccountData.getAccountGroup().trim();
        String srcSMSNotification = srcAccountData.getSmsNotification().trim();
        statusMelliSRC = srcAccountData.getStatusMelli();
        eStatus = srcAccountData.geteStatus();

        CustomerServiceNew destAccountData = (CustomerServiceNew)accounts.get(Constants.DEST_ACCOUNT_DATA);

        String destHostId = String.valueOf(destAccountData.getHostId()).trim();
        String destAccountNature = String.valueOf(destAccountData.getAccountNature());
        String destAccountGroup = destAccountData.getAccountGroup().trim();
        String destSMSNotification = destAccountData.getSmsNotification().trim();
        statusMelliDEST = destAccountData.getStatusMelli();
        statusD = destAccountData.getStatusD();

        if (!srcHostId.equals(String.valueOf(Constants.HOST_CFS))) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_SRC_HOST);
            throw new ServerAuthenticationException(ActionCode.INVALID_SRC_HOST);
        }

        if (!destHostId.equals(String.valueOf(Constants.HOST_CFS))) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_DST_HOST);
            throw new ServerAuthenticationException(ActionCode.INVALID_DST_HOST);
        }

        if (statusMelliSRC == Constants.MELLICODE_INVALID || statusMelliDEST == Constants.MELLICODE_INVALID) {
            log.debug("Melli Code Not Found.");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.MELLICODE_NOT_FOUND);
            throw new ServerAuthenticationException(new Exception(ActionCode.MELLICODE_NOT_FOUND));
        }

        command.addParam(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);
        command.addParam(Constants.SRC_HOST_ID, srcHostId);
        msg.setAttribute(Constants.SRC_HOST_ID, srcHostId);
        command.addParam(Constants.DEST_HOST_ID, destHostId);
        msg.setAttribute(Constants.DEST_HOST_ID, destHostId);
        msg.setAttribute(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);
        msg.setAttribute(Constants.DEST_ACCOUNT_NATURE, destAccountNature);
        msg.setAttribute(Constants.SRC_ACCOUNT_GROUP, srcAccountGroup);
        msg.setAttribute(Constants.DEST_ACCOUNT_GROUP, destAccountGroup.trim());
        command.addParam(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);
        command.addParam(Constants.DEST_SMS_NOTIFICATION, destSMSNotification);

        Vector currencyGroup = ChannelFacadeNew.getCMParam("CurrencyGroup");
        if (currencyGroup == null || currencyGroup.size() == 0 || srcAccountGroup == null || srcAccountGroup.equals("null"))
            throw new NotFoundException("currencyGroup Not Found. ");
        Iterator it = currencyGroup.iterator();
        while (it.hasNext()) {
            Cmparam param = (Cmparam) it.next();
            if (Integer.parseInt(srcAccountGroup) == param.getId()) {
                msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.FALSE);
                break;
            }
        }
    }
}


