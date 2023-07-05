package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.cardInfo.CardInfoReq;
import dpi.atlas.model.NotFoundException;
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
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: March 9, 2015
 * Time: 10:10:03 AM
 */
public class GetCFSCardAccount extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CardInfoReq cardInfoMsg = (CardInfoReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String cardNo = cardInfoMsg.getCardno();
        String hostId;
        String accountNo;
        try {

            String[]  accountInfo = ChannelFacadeNew.getCFSCardAccount(cardNo);
            if (accountInfo[0]==null || accountInfo[0].equals("")) {
                accountNo = ChannelFacadeNew.getCardAccount(cardNo, Constants.TABLE_NAME_NON_CFS_CARD_ACCOUNT);
                if (accountNo.equals(""))
                    throw new NotFoundException(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_CARD_NUMBER));
                else
                    hostId = Constants.HOST_ID_FARAGIR;
            } else {
                hostId = Constants.HOST_ID_CFS;
                accountNo=accountInfo[0];
                msg.setAttribute(Fields.CARD_TYPE, accountInfo[1]);
            }

            msg.setAttribute(Fields.HOST_ID, hostId);
            msg.setAttribute(Fields.ACCOUNT_NO, accountNo);

        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.debug("Card " + cardNo + " Does Not Exist");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.INVALID_CARD_NUMBER);

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside GetCFSCardAccount.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

