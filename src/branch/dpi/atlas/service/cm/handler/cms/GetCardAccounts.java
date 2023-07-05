package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import branch.dpi.atlas.service.cm.handler.cms.reCreate.RECREATEType;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Card;
import dpi.atlas.model.tj.entity.CardAccount;
import dpi.atlas.model.tj.entity.CustomerServiceNew;
import dpi.atlas.model.tj.entity.NonCFSCardAccount;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: jan 13, 2015
 * Time: 2:04:03 PM
 */
public class GetCardAccounts extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        List<CardAccount> cardAccount = new ArrayList<CardAccount>();
        List<NonCFSCardAccount> nonCFScardAccount = new ArrayList<NonCFSCardAccount>();
        RECREATEType reCreateMsg = (RECREATEType) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String cardNo = reCreateMsg.getCardno();

        try {

            Card card = ChannelFacadeNew.getCard(cardNo);
            holder.put(Constants.CARD, card);

            cardAccount = ChannelFacadeNew.getCFSCardAccounts(cardNo);
            holder.put(Fields.CARD_CFS_ACCOUNTS, cardAccount);

            nonCFScardAccount = ChannelFacadeNew.getNonCFSCardAccounts(cardNo);
            holder.put(Fields.CARD_FARA_ACCOUNTS, nonCFScardAccount);

            if (cardAccount.size() == 0 && nonCFScardAccount.size() == 0)
                throw new NotFoundException(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_CARD_NUMBER));

        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.debug("Card " + cardNo + " Does Not Exist");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.INVALID_CARD_NUMBER);

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CreateCardInCFS.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

