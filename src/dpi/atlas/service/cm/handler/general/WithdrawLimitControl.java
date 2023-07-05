package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import dpi.atlas.model.tj.entity.CardAccount;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.sql.SQLException;

/**
 * Date: Dec 30, 2007
 * Time: 10:14:21 AM
 * Update Date: june 29 2014
 * Update User: SH.Behnaz
 * To change this template use File | Settings | File Templates.
 */
public class WithdrawLimitControl extends TJServiceHandler implements Configurable {

    ArrayList CMD_TO_BE_CHECKED_ARRAY = new ArrayList();


    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        String cmdToBeChecked = configuration.get(Fields.CMD_TO_BE_CHECKED);
        CMD_TO_BE_CHECKED_ARRAY = CMUtil.tokenizString(cmdToBeChecked, ",");
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("inside WithdrawLimitControl.doProcess(CMMessage msg, Map holder)");

        String transactionType = getAttribute(msg, holder, "transactionType");
        String messageType = getAttribute(msg, holder, "messageType");
        if (!CMD_TO_BE_CHECKED_ARRAY.contains(messageType + transactionType))
            return;

        try {

            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            String reqAmount = (String) command.getCommandParams().get(Constants.AMOUNT);
            long reqWithdrawAmount = 0;
            if (reqAmount != null && !"".equals(reqAmount))
                reqWithdrawAmount = Long.parseLong(reqAmount.substring(1, reqAmount.length()));

            String accNo = (String) command.getCommandParams().get(Constants.SRC_ACCOUNT);
            String PAN = (String) command.getCommandParams().get(Constants.PAN);
            String srcHostId = (String) command.getCommandParams().get(Constants.HOST);
            String commandName = command.getParam(Fields.TX_CODE);
            Long maxCardLimit = (Long) holder.get(Fields.WITHDRAW_LIMIT);

            String tableName;
            if (srcHostId.equals(Constants.CFS_HOSTID))
                tableName = CardAccount.CFS_CARD_ACCOUNTS_TABLE_NAME;
            else
                tableName = CardAccount.NON_CFS_CARD_ACCOUNTS_TABLE_NAME;

            if (commandName.equals(TJCommand.CMD_SPARROW_POS_BRANCH_SHETAB_CASH_REQUEST))
                checkWithdrawLimit4BranchPos(maxCardLimit, reqWithdrawAmount, PAN, accNo, tableName);
            else if (commandName.equals(TJCommand.CMD_CASH_REQUEST))
                return;
            else if (commandName.equals(TJCommand.CMD_FAST_CASH) || commandName.equals(TJCommand.CMD_CASH_WITHDRAWAL)) {
                checkWithdrawLimit4ATMLocal(maxCardLimit, reqWithdrawAmount, PAN, accNo, tableName);
            } else if (commandName.equals(TJCommand.CMD_NETWORK_FAST_CASH) || commandName.equals(TJCommand.CMD_NETWORK_CASH_WITHDRAWAL)) {
                Long shetabCardLimit = (Long) holder.get(Fields.WITHDRAW_LIMIT_SHETAB);
                checkWithdrawLimit4ATMShetab(maxCardLimit, shetabCardLimit, reqWithdrawAmount, PAN, accNo, tableName);
            } else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_SERVICE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_SERVICE));
            }

        } catch (ServerAuthenticationException e) {
            log.debug(e);
            msg.setAttribute(CMMessage.FAULT_CODE, CMFault.FAULT_AUTH_SERVER);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.EXCEEEDS_WITHDRAWAL_AMOUNT_LIMIT);
            throw new CMFault(CMFault.FAULT_INTERNAL, e);
        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(CMMessage.FAULT_CODE, CMFault.FAULT_AUTH_SERVER);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, e);
        }
    }

    private void checkWithdrawLimit4BranchPos(long maxCardLimit, long reqWithdrawAmount, String PAN, String accNo, String tableName) throws ServerAuthenticationException, SQLException {
        String fieldNameAmount = "PBWITHDRAW_AMOUNT";
        String fieldNameDate = "PBWITHDRAW_DATE";

        long cardCurrentWithdrawAmount = ChannelFacadeNew.findCardCurrentWithdrawAmount(PAN, fieldNameAmount, fieldNameDate);
        if (cardCurrentWithdrawAmount + reqWithdrawAmount > maxCardLimit)
            throw new ServerAuthenticationException(Constants.INVALID_AMOUNT_REQ);
        ChannelFacadeNew.updateCardAcc(tableName, reqWithdrawAmount, accNo, PAN, fieldNameAmount, fieldNameDate);

    }

    private void checkWithdrawLimit4ATMLocal(long maxCardLimit, long reqWithdrawAmount, String PAN, String accNo, String tableName) throws ServerAuthenticationException, SQLException {
        String fieldNameAmount = "WITHDRAW_AMOUNT";
        String fieldNameDate = "WITHDRAW_DATE";
        List<Long> withdrawAmntSet;
        long cardCurrentWithdrawAmount = 0;

        withdrawAmntSet = ChannelFacadeNew.findCardCurrentWithdrawAmount(PAN, CardAccount.CFS_CARD_ACCOUNTS_TABLE_NAME);
        if (withdrawAmntSet.size() > 0) {
            cardCurrentWithdrawAmount +=  withdrawAmntSet.get(0);
            cardCurrentWithdrawAmount +=  withdrawAmntSet.get(1);
        }

        withdrawAmntSet = ChannelFacadeNew.findCardCurrentWithdrawAmount(PAN, CardAccount.NON_CFS_CARD_ACCOUNTS_TABLE_NAME);
        if (withdrawAmntSet.size() > 0) {
            cardCurrentWithdrawAmount +=  withdrawAmntSet.get(0);
            cardCurrentWithdrawAmount +=  withdrawAmntSet.get(1);
        }
        if (cardCurrentWithdrawAmount + reqWithdrawAmount > maxCardLimit)
            throw new ServerAuthenticationException(Constants.INVALID_AMOUNT_REQ);

        ChannelFacadeNew.updateCardAcc(tableName, reqWithdrawAmount, accNo, PAN, fieldNameAmount, fieldNameDate);

    }

    private void checkWithdrawLimit4ATMShetab(long maxCardLimit, long shetabCardLimit, long reqWithdrawAmount, String PAN, String accNo, String tableName) throws ServerAuthenticationException, SQLException {
        String fieldNameAmount = "STBWITHDRAW_AMOUNT";
        String fieldNameDate = "STBWITHDRAW_DATE";
        List<Long> withdrawAmntSet;
        long cardCurrentWithdrawAmountLocal = 0;
        long cardCurrentWithdrawAmountSTB = 0;

        withdrawAmntSet = ChannelFacadeNew.findCardCurrentWithdrawAmount(PAN, CardAccount.CFS_CARD_ACCOUNTS_TABLE_NAME);
        if (withdrawAmntSet.size() > 0) {
            cardCurrentWithdrawAmountLocal +=  withdrawAmntSet.get(0);
            cardCurrentWithdrawAmountSTB +=  withdrawAmntSet.get(1);
        }
        withdrawAmntSet = ChannelFacadeNew.findCardCurrentWithdrawAmount(PAN, CardAccount.NON_CFS_CARD_ACCOUNTS_TABLE_NAME);
        if (withdrawAmntSet.size() > 0) {
            cardCurrentWithdrawAmountLocal +=  withdrawAmntSet.get(0);
            cardCurrentWithdrawAmountSTB +=  withdrawAmntSet.get(1);
        }
        if (cardCurrentWithdrawAmountSTB + reqWithdrawAmount > shetabCardLimit)
            throw new ServerAuthenticationException(Constants.INVALID_AMOUNT_REQ);

        if (cardCurrentWithdrawAmountLocal + cardCurrentWithdrawAmountSTB + reqWithdrawAmount > maxCardLimit)
            throw new ServerAuthenticationException(Constants.INVALID_AMOUNT_REQ);

        ChannelFacadeNew.updateCardAcc(tableName, reqWithdrawAmount, accNo, PAN, fieldNameAmount, fieldNameDate);

    }

    private String getAttribute(CMMessage cmMessage, Map map, String key) {
        String str = (String) cmMessage.getAttribute(key);
        if (str == null)
            str = (String) map.get(key);
        return str;
    }

}
