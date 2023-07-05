package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.model.ModelException;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.ArrayList;
import java.util.Map;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User:Behnaz
 * Date: Dec 28, 2010
 * Time: 11:14:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class DailyWithdrawLimitReverse extends TJServiceHandler implements Configurable {

    ArrayList ACTIONCODE_TO_BE_BYPASSED_ARRAY = new ArrayList();


    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        String actioncodeToBeBypassed = configuration.get(Fields.ACTIONCODE_TO_BE_BYPASSED);
        ACTIONCODE_TO_BE_BYPASSED_ARRAY = CMUtil.tokenizString(actioncodeToBeBypassed, ",");
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMResultSet result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);

        String actionCode = result.getHeaderField(Params.ACTION_CODE);

        if (ACTIONCODE_TO_BE_BYPASSED_ARRAY.contains(actionCode))
            return;

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String messageType = command.getParam(Fields.TX_CODE);
        String accNo = (String) command.getCommandParams().get(Constants.SRC_ACCOUNT);
        String reqAmount = command.getParam(Constants.AMOUNT);

        boolean isCommandNFT =messageType.startsWith(CFSConstants.NFT_PREFIX);


        if (messageType.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_PG) || messageType.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_GROUP_CARD_PG)
            || messageType.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_GROUP_CARD_REVERSAL_PG) || messageType.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_REVERSAL_PG))

              isCommandNFT=false;

        if (!isCommandNFT)
            return;

        Long dailyWithdrawalLimitLong = null;

//        long reqWithdrawAmount = 0;
//        if (reqAmount != null && !"".equals(reqAmount)) {
//            reqWithdrawAmount = Long.parseLong(reqAmount.substring(1, reqAmount.length()));
//        }
        long reqWithdrawAmount = Long.parseLong(reqAmount);

        try {
            ChannelFacadeNew.ReverseDailyWithdrawLimit4NFT(accNo, reqWithdrawAmount, dailyWithdrawalLimitLong, messageType, "");

        } catch (ModelException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.EXCEEEDS_WITHDRAWAL_AMOUNT_LIMIT);
            throw new CMFault(CMFault.FAULT_INTERNAL, new CMFault(ActionCode.EXCEEEDS_WITHDRAWAL_AMOUNT_LIMIT));

        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new CMFault(ActionCode.DATABASE_ERROR));
        }
    }

}
