package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.util.Constants;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.model.ModelException;
import org.jpos.core.Configurable;

import java.util.Map;
import java.sql.SQLException;

/**
 * User: Behnaz
 * Date: Dec 28, 2010
 * Time: 10:14:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class DailyWithdrawLimitControl extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String messageType = command.getParam(Fields.TX_CODE);
        boolean isCommandNFT =messageType.startsWith(CFSConstants.NFT_PREFIX);
        String reqAmount = command.getParam(Constants.AMOUNT);
        String accNo = command.getParam(Constants.SRC_ACCOUNT);

       if (messageType.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ_PG) || messageType.equals(TJCommand.CMD_NETWORK_FUND_TRANSFER_GROUP_CARD_PG))
             isCommandNFT=false;

        if(!isCommandNFT)
            return;

        Long dailyWithdrawalLimitLong = (Long) holder.get(CFSConstants.NFT_TRANSACTION_TYPE + CFSConstants.DAILY_WITHDRAWAL_LIMIT);
        long reqWithdrawAmount = 0;
        if (reqAmount != null && !"".equals(reqAmount))
            reqWithdrawAmount = Long.parseLong(reqAmount.substring(1, reqAmount.length()));
        try {

            ChannelFacadeNew.updateDailyWithdrawLimit4NFT(accNo, reqWithdrawAmount, dailyWithdrawalLimitLong, messageType);


        } catch (ModelException e) {
            log.debug(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.EXCEEEDS_WITHDRAWAL_AMOUNT_LIMIT);
            throw new CMFault(CMFault.FAULT_INTERNAL, new CMFault(ActionCode.EXCEEEDS_WITHDRAWAL_AMOUNT_LIMIT));
        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new CMFault(ActionCode.DATABASE_ERROR));
        }


    }


}
