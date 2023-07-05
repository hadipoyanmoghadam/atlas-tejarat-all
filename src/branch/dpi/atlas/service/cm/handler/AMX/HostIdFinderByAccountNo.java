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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;


/**
 * User: R.Nasiri
 * Date: Oct 19, 2021
 * Time: 01:27 PM
 */
public class HostIdFinderByAccountNo extends TJServiceHandler implements Configurable {

    String eStatus = "";

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.TRUE);

        String accountNo = command.getParam(Fields.SRC_ACCOUNT);
        try {
            if (accountNo == null || "".equals(accountNo.trim()))
                throw new NotFoundException(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);

            getSrcAccountHostID(msg, accountNo);


            if (eStatus.equals(Constants.E_STATUS_CLOSE)) {
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

        } catch (NotFoundException e) {
            log.debug("Customer with Account No = " + accountNo + "Not Found");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (ServerAuthenticationException e) {
            throw new CMFault(CMFault.FAULT_INTERNAL);
        } catch (SQLException e) {
            log.error("DB has encountered an exception during fetching data from tbcustomersrv...");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_DATA_ERROR));
        } catch (Exception e) {
            log.error("Exception" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

    private void getSrcAccountHostID(CMMessage msg, String accountNo) throws Exception {
        byte statusMelliSRC;

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        CustomerServiceNew srcAccountData = ChannelFacadeNew.findCustomerAccountSrv(accountNo);

        String srcHostId = String.valueOf(srcAccountData.getHostId()).trim();
        String srcAccountNature = String.valueOf(srcAccountData.getAccountNature()).trim();
        String srcAccountGroup = srcAccountData.getAccountGroup();
        String srcSMSNotification = srcAccountData.getSmsNotification().trim();
        statusMelliSRC = srcAccountData.getStatusMelli();
        eStatus = srcAccountData.geteStatus();

        if (srcAccountGroup == null) {
            log.error("Account Group is not specified for account = " + accountNo);
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new ServerAuthenticationException(new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        }

        if (srcHostId.equals(String.valueOf(Constants.HOST_SGB))) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_SRC_HOST);
            throw new ServerAuthenticationException(ActionCode.INVALID_SRC_HOST);
        }

        command.addParam(Constants.SRC_HOST_ID, srcHostId);
        msg.setAttribute(Constants.SRC_HOST_ID, srcHostId);
        msg.setAttribute(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);
        msg.setAttribute(Constants.SRC_ACCOUNT_GROUP, srcAccountGroup.trim());
        command.addParam(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);

        if (statusMelliSRC == Constants.MELLICODE_INVALID) {
            log.debug("Melli Code Not Found.");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.MELLICODE_NOT_FOUND);
            throw new ServerAuthenticationException(new Exception(ActionCode.MELLICODE_NOT_FOUND));
        }

        Vector currencyGroup = ChannelFacadeNew.getCMParam("CurrencyGroup");
        if (currencyGroup == null || currencyGroup.size() == 0)
            throw new NotFoundException("currencyGroup Not Found. ");
        for (Object aCurrencyGroup : currencyGroup) {
            Cmparam param = (Cmparam) aCurrencyGroup;
            if (Integer.parseInt(srcAccountGroup.trim()) == param.getId()) {
                msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.FALSE);
                break;
            }
        }
    }
}
