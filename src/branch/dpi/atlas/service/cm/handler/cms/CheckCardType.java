package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.util.ImmediateCardUtil;
import dpi.atlas.model.tj.entity.CardAccount;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by SH.Behnaz on 10/27/16.
 */
public class CheckCardType extends CMHandlerBase {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String cardNo = msg.getAttributeAsString(Fields.PAN);
        String row = msg.getAttributeAsString(Fields.ROW);
        msg.setAttribute(Fields.CARD_TYPE, Constants.NORMAL_CARD_SERIES);
        String messageType = msg.getAttributeAsString(Fields.MESSAGE_TYPE);
        String accountType;
        try {

            List<CardAccount> cardAccount = ChannelFacadeNew.getCFSCardAccounts(cardNo);

            if (cardAccount.isEmpty()) {
                if (messageType.equalsIgnoreCase(Constants.BALANCE_GROUPCARD_MSG_TYPE) || messageType.equalsIgnoreCase(Constants.STATEMENT_GROUPCARD_MSG_TYPE)) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_CARD_NUMBER));
                } else {

                    msg.setAttribute(Fields.CARD_TYPE, Constants.HOST_ID_FARAGIR);
                    return;
                }
            }

            Iterator it = cardAccount.iterator();
            while (it.hasNext()) {
                CardAccount cardAcc = (CardAccount) it.next();
                accountType = cardAcc.getAccountType();
                int series = cardAcc.getSeries();
                if (accountType.equals(Constants.GIFT_CARD_007)) {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                } else if (cardAcc.getCardType().equals(Constants.GROUP_CARD_TYPE_STR)) {
                    if (ImmediateCardUtil.isParent(series))
                        msg.setAttribute(Fields.CARD_TYPE, Constants.GROUP_CARD_PARENT);
                    else if (ImmediateCardUtil.isChild(series))
                        msg.setAttribute(Fields.CARD_TYPE, Constants.GROUP_CARD_CHILD);
                    else {
                        msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_ROW);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_ROW));
                    }
                }

            }

            if ((messageType.equalsIgnoreCase(Constants.BALANCE_GROUPCARD_MSG_TYPE) || messageType.equalsIgnoreCase(Constants.STATEMENT_GROUPCARD_MSG_TYPE) ) &&
                    !msg.getAttributeAsString(Fields.CARD_TYPE).equalsIgnoreCase(Constants.GROUP_CARD_CHILD) &&  !msg.getAttributeAsString(Fields.CARD_TYPE).equalsIgnoreCase(Constants.GROUP_CARD_PARENT)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));

            }

        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CheckCardType.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }


}
