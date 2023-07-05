package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.source.sparrow.message.SparrowTransaction;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.source.sparrow.message.SparrowHeader;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.entity.CustomerServiceNew;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.entity.cms.Cmparam;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.util.*;
import java.sql.SQLException;

/**
 * Created by Behnaz.
 * To change this template use File | Settings | File Templates.
 */

public class HostIdFinderByAccountNo extends TJServiceHandler implements Configurable {

    ArrayList not_Checked_For_Arzi_array = new ArrayList();
    ArrayList Checked_Dest_Account = new ArrayList();
    ArrayList Checked_National_Code = new ArrayList();
    ArrayList Checked_GroupCard_FT = new ArrayList();
    String savedNationalCode = "";
    String eStatus = "";

    public void setConfiguration(Configuration configuration) throws ConfigurationException {

        String notCheckedForArzi = configuration.get(Fields.NOT_CHECKED_FOR_ARZI);
        not_Checked_For_Arzi_array = CMUtil.tokenizString(notCheckedForArzi, ",");

        String getDestAccount = configuration.get("GetDestAccount");
        Checked_Dest_Account = CMUtil.tokenizString(getDestAccount, ",");

        String checkedNationalCode = configuration.get("CheckedNationalCode");
        Checked_National_Code = CMUtil.tokenizString(checkedNationalCode, ",");

        String checkedGruopCardFT = configuration.get("CheckedGroupCardFT");
        Checked_GroupCard_FT = CMUtil.tokenizString(checkedGruopCardFT, ",");
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        String hostId = "HostID";

        String transactionType = getAttribute(msg, holder, "transactionType");
        String messageType = getAttribute(msg, holder, "messageType");

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.TRUE);

        String accountNo = command.getParam(Fields.SRC_ACCOUNT);
        String destAccountNo = command.getParam(Fields.DEST_ACCOUNT);
        try {
            if (accountNo == null || "".equals(accountNo.trim()))
                throw new NotFoundException(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            accountNo = ISOUtil.zeropad(accountNo, 13);

            if (Checked_GroupCard_FT.contains(messageType + transactionType) && destAccountNo != null && !"".equals(destAccountNo) && destAccountNo.startsWith(Constants.BANKE_TEJARAT_BIN_NEW))
            {
                getSrcAccountHostID(msg, accountNo);
                if (!ChannelFacadeNew.isGroupCard(msg.getAttributeAsString(Constants.SRC_ACCOUNT_GROUP))){
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new ServerAuthenticationException(new Exception(ActionCode.INVALID_OPERATION));
                }
                command.addParam(Constants.DEST_HOST_ID, (String) msg.getAttribute(Constants.SRC_HOST_ID));
                msg.setAttribute(Constants.DEST_HOST_ID,msg.getAttribute(Constants.SRC_HOST_ID));
            }else if (Checked_Dest_Account.contains(messageType + transactionType) && destAccountNo != null && !"".equals(destAccountNo))
                getSrcAndDestAccountHostID(msg, destAccountNo, accountNo);
            else if ((messageType + transactionType).equalsIgnoreCase("599") || (messageType + transactionType).equalsIgnoreCase("099"))
                getLOROHostID(msg);
            else
                getSrcAccountHostID(msg, accountNo);

            String srcBranchCode = command.getParam(Fields.SRC_BRANCH);
            if (Checked_National_Code.contains(messageType + transactionType) &&
                    (srcBranchCode != null) && srcBranchCode.startsWith(Fields.PAYMENT_GATEWAY_INDICATOR)) {

                String inputNationalCode = command.getParam(Fields.MERCHANT_LOCATION).substring(0, 10);

                if (savedNationalCode == null || !savedNationalCode.trim().equals(inputNationalCode)) {
                    log.debug("Melli Code Not Valid.");
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.MELLICODE_INVALID);
                    throw new ServerAuthenticationException(new Exception(ActionCode.MELLICODE_INVALID));
                }
            }


            if (eStatus.equals(Constants.E_STATUS_CLOSE) &&  !messageType.equals("0") && !messageType.equals("7") ) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_IS_E_BLOCKED);
                throw new ServerAuthenticationException(new Exception(ActionCode.ACCOUNT_IS_E_BLOCKED));
            }

            if (!not_Checked_For_Arzi_array.contains(messageType + transactionType) && msg.getAttribute(Constants.IS_LOCAL_CURRENCY).equals(Constants.FALSE)) {
                log.debug("Account Group is not in Local Currency Range.");
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_RANGE_NOT_FOUND);
                throw new ServerAuthenticationException(new Exception(ActionCode.ACCOUNT_RANGE_NOT_FOUND));
            }

            if (msg.getAttribute(Constants.SRC_HOST_ID).equals(Constants.HOST_ID_SGB)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.SOURCE_ACCOUNT_INVALID);
                throw new ServerAuthenticationException(new Exception(ActionCode.SOURCE_ACCOUNT_INVALID));
            }

            msg.setAttribute(hostId, command.getParam(Constants.SRC_HOST_ID));
            command.addHeaderParam(Fields.SRC_ACCOUNT, accountNo);
            command.addHeaderParam(Fields.DEST_ACCOUNT, command.getParam(Fields.DEST_ACCOUNT));
            msg.setAttribute(CMMessage.REQUEST, command);

        } catch (NotFoundException e) {
            log.debug("Customer with Account No = " + accountNo + " or with Account No = " + destAccountNo + "  Not Found");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (ServerAuthenticationException e) {
            throw new CMFault(CMFault.FAULT_INTERNAL);
        } catch (SQLException e) {
            log.error("DB has encountered an exception during fething data from tbcustomersrv...");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_DATA_ERROR));
        } catch (Exception e) {
            log.error("Exception");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

    private String getAttribute(CMMessage cmMessage, Map map, String key) {
        String str = (String) cmMessage.getAttribute(key);
        if (str == null)
            str = (String) map.get(key);
        return str;
    }

    private void getSrcAndDestAccountHostID(CMMessage msg, String destAccountNo, String accountNo) throws Exception {
        byte statusMelliSRC;
        byte statusMelliDEST;

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        if (destAccountNo.length() > 13)
            destAccountNo = destAccountNo.substring(0, 13);
        else
            destAccountNo = ISOUtil.zeropad(destAccountNo, 13);

        Map accounts = ChannelFacadeNew.findAccountHost(accountNo, destAccountNo);

        CustomerServiceNew srcAccountData = (CustomerServiceNew) accounts.get(Constants.ACCOUNT_DATA);

        String srcHostId = String.valueOf(srcAccountData.getHostId()).trim();
        String srcAccountNature = String.valueOf(srcAccountData.getAccountNature()).trim();
        String srcAccountGroup = srcAccountData.getAccountGroup().trim();
        String srcSMSNotification = srcAccountData.getSmsNotification().trim();
        statusMelliSRC = srcAccountData.getStatusMelli();
        savedNationalCode = srcAccountData.getNationalCode();
        eStatus = srcAccountData.geteStatus();

        CustomerServiceNew destAccountData = (CustomerServiceNew) accounts.get(Constants.DEST_ACCOUNT_DATA);

        String destHostId = String.valueOf(destAccountData.getHostId()).trim();
        String destAccountNature = String.valueOf(destAccountData.getAccountNature()).trim();
        String destAccountGroup = destAccountData.getAccountGroup().trim();
        String destSMSNotification = destAccountData.getSmsNotification().trim();
        statusMelliDEST = destAccountData.getStatusMelli();

        command.addParam(Constants.SRC_HOST_ID, srcHostId);
        msg.setAttribute(Constants.SRC_HOST_ID, srcHostId);
        command.addParam(Constants.DEST_HOST_ID, destHostId);
        msg.setAttribute(Constants.DEST_HOST_ID, destHostId);
        msg.setAttribute(Constants.DEST_ACCOUNT_NATURE, destAccountNature);
        msg.setAttribute(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);
        msg.setAttribute(Constants.SRC_ACCOUNT_GROUP, srcAccountGroup);
        msg.setAttribute(Constants.DEST_ACCOUNT_GROUP, destAccountGroup);
        command.addParam(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);
        command.addParam(Constants.DEST_SMS_NOTIFICATION, destSMSNotification);

        if (statusMelliSRC == Constants.MELLICODE_INVALID || statusMelliDEST == Constants.MELLICODE_INVALID) {
            log.debug("Melli Code Not Found.");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.MELLICODE_NOT_FOUND);
            throw new ServerAuthenticationException(new Exception(ActionCode.MELLICODE_NOT_FOUND));
        }


        Vector currencyGroup = ChannelFacadeNew.getCMParam("CurrencyGroup");
        if (currencyGroup == null || currencyGroup.size() == 0 || srcAccountGroup == null || destAccountGroup == null || srcAccountGroup.equals("null") || destAccountGroup.equals("null"))
            throw new NotFoundException("currencyGroup Not Found. ");
        Iterator it = currencyGroup.iterator();
        while (it.hasNext()) {
            Cmparam param = (Cmparam) it.next();
            if (Integer.parseInt(srcAccountGroup) == param.getId().intValue() || Integer.parseInt(destAccountGroup) == param.getId().intValue()) {
                msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.FALSE);
                break;
            }
        }
    }

    private void getSrcAccountHostID(CMMessage msg, String accountNo) throws Exception {
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        CustomerServiceNew srcAccountData = ChannelFacadeNew.findCustomerAccountSrv(accountNo);

        String srcHostId = String.valueOf(srcAccountData.getHostId()).trim();
        String srcAccountNature = String.valueOf(srcAccountData.getAccountNature());
        String srcAccountGroup = srcAccountData.getAccountGroup().trim();
        String srcSMSNotification = srcAccountData.getSmsNotification().trim();
        savedNationalCode = srcAccountData.getNationalCode();
        eStatus = srcAccountData.geteStatus();

        command.addParam(Constants.SRC_HOST_ID, srcHostId);
        msg.setAttribute(Constants.SRC_HOST_ID, srcHostId);

        command.addParam(Constants.DEST_HOST_ID, "");
        msg.setAttribute(Constants.DEST_HOST_ID, "");
        msg.setAttribute(Constants.SRC_ACCOUNT_NATURE, srcAccountNature);
        msg.setAttribute(Constants.SRC_ACCOUNT_GROUP, srcAccountGroup);
	    command.addParam(Constants.SRC_ACCOUNT_GROUP, srcAccountGroup);
        command.addParam(Constants.SRC_SMS_NOTIFICATION, srcSMSNotification);

        if (srcAccountData.getStatusMelli() == Constants.MELLICODE_INVALID) {
            log.debug("Melli Code Not Found.");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.MELLICODE_NOT_FOUND);
            throw new ServerAuthenticationException(new Exception(ActionCode.MELLICODE_NOT_FOUND));
        }

        Vector currencyGroup = ChannelFacadeNew.getCMParam("CurrencyGroup");
        if (currencyGroup == null || currencyGroup.size() == 0 || srcAccountGroup == null || srcAccountGroup.equals("null"))
            throw new NotFoundException("currencyGroup Not Found. ");
        Iterator it = currencyGroup.iterator();
        while (it.hasNext()) {
            Cmparam param = (Cmparam) it.next();
            if (Integer.parseInt(srcAccountGroup) == param.getId().intValue()) {
                msg.setAttribute(Constants.IS_LOCAL_CURRENCY, Constants.FALSE);
                break;
            }
        }

    }

    private void getLOROHostID(CMMessage msg) throws Exception {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        command.addParam(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
        msg.setAttribute(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);

        command.addParam(Constants.DEST_HOST_ID, "");
        msg.setAttribute(Constants.DEST_HOST_ID, "");

    }

}
