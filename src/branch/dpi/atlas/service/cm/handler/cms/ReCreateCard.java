package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import branch.dpi.atlas.service.cm.handler.cms.reCreate.RECREATEType;
import dpi.atlas.model.tj.entity.Card;
import dpi.atlas.model.tj.entity.CardAccount;
import dpi.atlas.model.tj.entity.NonCFSCardAccount;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
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
 * Date: jan 12, 2015
 * Time: 10:04:03 PM
 */
public class ReCreateCard extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            List<CardAccount> cardAccount = (ArrayList<CardAccount>) holder.get(Fields.CARD_CFS_ACCOUNTS);
            List<NonCFSCardAccount> nonCFScardAccount = (ArrayList<NonCFSCardAccount>) holder.get(Fields.CARD_FARA_ACCOUNTS);
            Card cardObj = (Card) holder.get(Constants.CARD);
            RECREATEType reCreateMsg = (RECREATEType) msg.getAttribute(CMMessage.COMMAND_OBJ);
            String newCardNo = reCreateMsg.getNewcardno();
            String editdate = reCreateMsg.getEditdate();
            String cardNo = reCreateMsg.getCardno();

            ChannelFacadeNew.reCreateCard(nonCFScardAccount, cardAccount, newCardNo, editdate,cardNo,cardObj,msg.getAttributeAsString(Fields.SESSION_ID));

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

