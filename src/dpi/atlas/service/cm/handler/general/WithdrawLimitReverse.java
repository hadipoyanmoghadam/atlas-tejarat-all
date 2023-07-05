package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.model.tj.entity.CardAccount;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.ArrayList;
import java.util.Map;
import java.sql.SQLException;

/**
 * Date: Dec 30, 2007
 * Time: 10:14:21 AM
 * Update Date: june 29 2014
 * Update User: SH.Behnaz
 * To change this template use File | Settings | File Templates.
 */
public class WithdrawLimitReverse extends TJServiceHandler implements Configurable {

    ArrayList CMD_TO_BE_CHECKED_ARRAY = new ArrayList();
    ArrayList ACTIONCODE_TO_BE_BYPASSED_ARRAY = new ArrayList();


    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        String cmdToBeChecked = configuration.get(Fields.CMD_TO_REVERSE_WITHDRAWLIMIT);
        CMD_TO_BE_CHECKED_ARRAY = CMUtil.tokenizString(cmdToBeChecked, ",");

        String actioncodeToBeBypassed = configuration.get(Fields.ACTIONCODE_TO_BE_BYPASSED);
        ACTIONCODE_TO_BE_BYPASSED_ARRAY = CMUtil.tokenizString(actioncodeToBeBypassed, ",");

    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String fieldNameAmount;
        String fieldNameDate;
        String transactionType = getAttribute(msg, holder, "transactionType");
        String messageType = getAttribute(msg, holder, "messageType");

        if (!CMD_TO_BE_CHECKED_ARRAY.contains(messageType + transactionType)) {
            return;
        }

        CMResultSet result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);

        String actionCode = result.getHeaderField(Params.ACTION_CODE);

        if (actionCode.equalsIgnoreCase(ActionCode.APPROVED)) {
            return;
        }

        if (ACTIONCODE_TO_BE_BYPASSED_ARRAY.contains(actionCode)) {
            return;
        }

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String reqAmount = (String) command.getCommandParams().get(Constants.AMOUNT);

        long reqWithdrawAmount = 0;
        if (reqAmount != null && !"".equals(reqAmount))
            reqWithdrawAmount = Long.parseLong(reqAmount.substring(1, reqAmount.length()));

        String accNo = (String) command.getCommandParams().get(Constants.SRC_ACCOUNT);
        String pan = (String) command.getCommandParams().get(Constants.PAN);
        String srcHostId = (String) command.getCommandParams().get(Constants.HOST);
        String commandName = command.getParam(Fields.TX_CODE);

        String tableName;
        if (srcHostId.equals(Constants.CFS_HOSTID))
            tableName = CardAccount.CFS_CARD_ACCOUNTS_TABLE_NAME;
        else
            tableName = CardAccount.NON_CFS_CARD_ACCOUNTS_TABLE_NAME;

        if (commandName.equals(TJCommand.CMD_SPARROW_POS_BRANCH_SHETAB_CASH_REQUEST)) {
            fieldNameAmount = "PBWITHDRAW_AMOUNT";
            fieldNameDate = "PBWITHDRAW_DATE";
        } else if (commandName.equals(TJCommand.CMD_CASH_REQUEST)) {
            return;
        } else if (commandName.equals(TJCommand.CMD_NETWORK_FAST_CASH) || commandName.equals(TJCommand.CMD_NETWORK_CASH_WITHDRAWAL)) {
            fieldNameAmount = "STBWITHDRAW_AMOUNT";
            fieldNameDate = "STBWITHDRAW_DATE";
        } else {
            fieldNameAmount = "WITHDRAW_AMOUNT";
            fieldNameDate = "WITHDRAW_DATE";
        }

        try {
            ChannelFacadeNew.updateCardAcc(tableName, -reqWithdrawAmount, accNo, pan, fieldNameAmount, fieldNameDate);
        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
            msg.setAttribute(CMMessage.FAULT_CODE, CMFault.FAULT_AUTH_SERVER);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, e);
        }
    }

    private String getAttribute(CMMessage cmMessage, Map map, String key) {
        String str = (String) cmMessage.getAttribute(key);
        if (str == null)
            str = (String) map.get(key);
        return str;
    }

}
