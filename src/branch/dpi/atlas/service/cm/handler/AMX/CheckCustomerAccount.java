package branch.dpi.atlas.service.cm.handler.AMX;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * User: R.Nasiri
 * Date: Dec 5, 2018
 * Time: 04:02 PM
 */
public class CheckCustomerAccount extends TJServiceHandler implements Configurable {

    ArrayList not_Checked = new ArrayList();
    AmxMessage amxMessage;
    String eStatus = "";

    public void setConfiguration(Configuration configuration) throws ConfigurationException {

        String notChecked = configuration.get("notChecked");
        not_Checked = CMUtil.tokenizString(notChecked, ",");

    }

    public void doProcess(CMMessage msg, Map map) throws CMFault {

        amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String pin = amxMessage.getPin();

        if (not_Checked.contains(pin))
            return;
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.TRUE);
        String accountNo = (command.getParam(Fields.SRC_ACCOUNT) != null) ? command.getParam(Fields.SRC_ACCOUNT) :
                command.getParam(Fields.DEST_ACCOUNT);
        try {

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
        eStatus = srcAccountData.geteStatus();

        if (!srcHostId.equals(String.valueOf(Constants.HOST_CFS))) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_SRC_HOST);
            throw new ServerAuthenticationException(ActionCode.INVALID_SRC_HOST);
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


