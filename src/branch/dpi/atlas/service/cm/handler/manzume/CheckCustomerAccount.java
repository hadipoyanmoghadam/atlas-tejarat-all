package branch.dpi.atlas.service.cm.handler.manzume;

import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
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
 * * Modified by user F.Heydari
 * Date: NOV 5, 2022
 * Time: 11:21:44 AM
 */
public class CheckCustomerAccount extends TJServiceHandler implements Configurable {


    ManzumeMessage manzumeMessage;
    String eStatus = "";
    String statusD = "";
    ArrayList check_Dep_block = new ArrayList();

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        String checkDepBlock = configuration.get("checkDepBlock");
        check_Dep_block = CMUtil.tokenizString(checkDepBlock, ",");
    }

    public void doProcess(CMMessage msg, Map map) throws CMFault {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);


        msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.TRUE);

        try {
            manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            String pin = manzumeMessage.getPin();
            String accountNo="";

//            String accountNo = (command.getParam(Fields.DEST_ACCOUNT) != null) ? command.getParam(Fields.DEST_ACCOUNT) :
//                    command.getParam(Fields.SRC_ACCOUNT);

            if(pin.equalsIgnoreCase(Constants.PIN_DEPOSIT_MANZUME) || pin.equalsIgnoreCase(Constants.PIN_DEPOSIT_REVERSE_MANZUME)){

             accountNo = (msg.getAttributeAsString(Fields.DEST_ACCOUNT) != null) ? msg.getAttributeAsString(Fields.DEST_ACCOUNT) :
                    command.getParam(Fields.SRC_ACCOUNT);}
            else{
                 accountNo = (command.getParam(Fields.DEST_ACCOUNT) != null) ? command.getParam(Fields.DEST_ACCOUNT) :
                   command.getParam(Fields.SRC_ACCOUNT);
            }
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
        String destHostId = String.valueOf(srcAccountData.getHostId()).trim();
        String srcAccountNature = String.valueOf(srcAccountData.getAccountNature());
        String srcAccountGroup = srcAccountData.getAccountGroup().trim();
        String srcSMSNotification = srcAccountData.getSmsNotification().trim();
        eStatus = srcAccountData.geteStatus();
        statusD = srcAccountData.getStatusD();

        if (pin.equalsIgnoreCase(Constants.PIN_DEPOSIT_MANZUME)) {
            if (destHostId.equals(String.valueOf(Constants.HOST_SGB))) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_SRC_HOST);
                throw new ServerAuthenticationException(ActionCode.INVALID_SRC_HOST);
            }
        }

        msg.setAttribute(Fields.ACCOUNT_NO, accountNo);
        if (pin.equals(Constants.PIN_DEPOSIT_REVERSE_MANZUME)) {
            command.addParam(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);
            command.addParam(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);
            msg.setAttribute(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);
            msg.setAttribute(Constants.SRC_ACCOUNT_GROUP, srcAccountGroup);

        } else if (pin.equals(Constants.PIN_DEPOSIT_MANZUME)) {
            command.addParam(Constants.DEST_SMS_NOTIFICATION, srcSMSNotification);
            command.addParam(Constants.SRC_ACCOUNT, msg.getAttributeAsString(Fields.SRC_ACCOUNT));
            command.addParam(Constants.DEST_ACCOUNT_NATURE, srcAccountNature);
            msg.setAttribute(Constants.DEST_ACCOUNT_NATURE, srcAccountNature);
            msg.setAttribute(Constants.DEST_ACCOUNT_GROUP, srcAccountGroup);
        }


        //destAccountNo
        msg.setAttribute(Fields.ACCOUNT_NO, accountNo);
        command.addParam(Constants.DST_ACCOUNT,  msg.getAttributeAsString(Fields.ACCOUNT_NO));

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


