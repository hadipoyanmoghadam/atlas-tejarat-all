package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.entity.CustomerServiceNew;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.entity.cms.Cmparam;

import java.util.Map;
import java.util.Vector;
import java.util.Iterator;
import java.util.ArrayList;

import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: Jun 3, 2013
 * Time: 11:21:44 AM
 */
public class CheckCustomerAccount extends TJServiceHandler implements Configurable {

    ArrayList not_Permitted = new ArrayList();
    ArrayList invalid_Gift = new ArrayList();
    ArrayList check_Dep_Block = new ArrayList();
    BranchMessage branchMsg;
    String eStatus = "";
    String statusD = "";

    public void setConfiguration(Configuration configuration) throws ConfigurationException {

        String notPermitted = configuration.get("notPermitted");
        not_Permitted = CMUtil.tokenizString(notPermitted, ",");

        String invalidGift = configuration.get("invalidGift");
        invalid_Gift = CMUtil.tokenizString(invalidGift, ",");

        String checkDepBlock = configuration.get("checkDepBlock");
        check_Dep_Block = CMUtil.tokenizString(checkDepBlock, ",");
    }

    public void doProcess(CMMessage msg, Map map) throws CMFault {

        if (msg.getAttributeAsString(Fields.REQUEST_IS_ACCOUNT_BASED).equals("0"))
            return;
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.TRUE);
        String accountNo = (command.getParam(Fields.SRC_ACCOUNT) != null) ? command.getParam(Fields.SRC_ACCOUNT) :
                command.getParam(Fields.DEST_ACCOUNT);
        try {
            branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            String pin = branchMsg.getPin();
            checkCustomerAccount(msg, accountNo, pin);

            if (msg.getAttribute(Constants.IS_LOCAL_CURRENCY).equals(Constants.FALSE)) {
                log.debug("Account Group is not in Local Currency Range.");
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_RANGE_NOT_FOUND);
                throw new ServerAuthenticationException(new Exception(ActionCode.ACCOUNT_RANGE_NOT_FOUND));
            }


            if (not_Permitted.contains(pin)) {
                int accountNature = Integer.parseInt(msg.getAttributeAsString(Constants.DEST_ACCOUNT_NATURE));
                if (accountNature == Constants.ACCOUNT_NATURE_NO_DEPOSIT) {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new ServerAuthenticationException(new Exception(ActionCode.INVALID_OPERATION));
                }
            }
            if (invalid_Gift.contains(pin)) {
                if ((msg.getAttributeAsString(Constants.SRC_ACCOUNT_GROUP)!=null && msg.getAttributeAsString(Constants.SRC_ACCOUNT_GROUP).trim().equals(Constants.GIFT_CARD_007)) ||
                        (msg.getAttributeAsString(Constants.DEST_ACCOUNT_GROUP)!=null && msg.getAttribute(Constants.DEST_ACCOUNT_GROUP).equals(Constants.GIFT_CARD_007))) {

                    String[] accountInfo = ChannelFacadeNew.findCustomerID(accountNo,"","");
                    if (accountInfo[0] == null || accountInfo[0].equals("")) {
                        throw new NotFoundException("Account not found");
                    }
                    if (accountInfo[1] != null && !accountInfo[1].equals("") && accountInfo[1].equals(Constants.GIFT_CARD_ACCOUNT_TITLE)) {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                        throw new ServerAuthenticationException(new Exception(ActionCode.INVALID_OPERATION));
                    }

                }
            }
            if (eStatus.equals(Constants.E_STATUS_CLOSE)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_IS_E_BLOCKED);
                throw new ServerAuthenticationException(new Exception(ActionCode.ACCOUNT_IS_E_BLOCKED));
            }
            if (check_Dep_Block.contains(pin)) {
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
        statusD = srcAccountData.getStatusD().trim();
        eStatus = srcAccountData.geteStatus();

        if (!srcHostId.equals(String.valueOf(Constants.HOST_CFS))) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_SRC_HOST);
            throw new ServerAuthenticationException(ActionCode.INVALID_SRC_HOST);
        }
        if (pin.equals(Constants.PIN_WITHDRAW) || pin.equals(Constants.PIN_DEPOSIT_REVERSE)  ||
                pin.equals(Constants.PIN_DEPOSIT_GIFTCARD_REVERSE) || pin.equals(Constants.PIN_WITHDRAW_ATM) ||
                pin.equals(Constants.PIN_REVERSE_DEPOSIT_ATM) || pin.equals(Constants.PIN_WITHDRAW_WAGE)) {
            command.addParam(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);
            command.addParam(Constants.SRC_HOST_ID, srcHostId);
            command.addParam(Constants.DEST_HOST_ID, " ");
            command.addParam(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);
            msg.setAttribute(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);
            msg.setAttribute(Constants.SRC_ACCOUNT_GROUP, srcAccountGroup);

        } else if (pin.equals(Constants.PIN_DEPOSIT) || pin.equals(Constants.PIN_WITHDRAW_REVERSE) ||
                pin.equals(Constants.PIN_DEPOSIT_GIFTCARD) || pin.equals(Constants.PIN_DEPOSIT_ATM) ||
                pin.equals(Constants.PIN_REVERSE_WITHDRAW_ATM) || pin.equals(Constants.PIN_SEND_PAYA) ||
                pin.equals(Constants.PIN_REVERSE_WITHDRAW_WAGE)) {
            command.addParam(Constants.DEST_SMS_NOTIFICATION, srcSMSNotification);
            command.addParam(Constants.DEST_HOST_ID, srcHostId);
            command.addParam(Constants.SRC_HOST_ID, " ");
            command.addParam(Constants.DEST_ACCOUNT_NATURE, srcAccountNature);
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
}


