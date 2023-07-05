package branch.dpi.atlas.service.cm.handler.cms;

import dpi.atlas.model.facade.cm.ServerAuthenticationException;
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
 * Created by SH.Behnaz on 10/27/16.
 */
public class RevokeCard extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        int updateCount = 0;
        String cardNo = msg.getAttributeAsString(Fields.PAN);
        String rrn = msg.getAttributeAsString(Fields.RRN);
        String cardType = msg.getAttributeAsString(Fields.CARD_TYPE);
        String sessionId = msg.getAttributeAsString(Fields.SESSION_ID);


        try {
            if (cardType.equalsIgnoreCase(Constants.GROUP_CARD_PARENT))
                updateCount = ChannelFacadeNew.DeactiveCardAccount4Parent(cardNo);
            else if (cardType.equalsIgnoreCase(Constants.GROUP_CARD_CHILD))
                updateCount =  ChannelFacadeNew.DeactiveCardAccount4Child(cardNo,sessionId,rrn);
            else{
                updateCount = ChannelFacadeNew.DeactiveCardAccountInNonCFS(cardNo);

                if (!msg.getAttributeAsString(Fields.CARD_TYPE).equalsIgnoreCase(Constants.HOST_ID_FARAGIR))
                    updateCount = updateCount + ChannelFacadeNew.DeactiveCardAccountInCFSCARD(cardNo);
            }
            if (updateCount <= 0) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.CARD_HAS_BEEN_REVOKED_BEFORE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CARD_HAS_BEEN_REVOKED_BEFORE));
            }

        } catch (CMFault e) {
            throw e;
        } catch (ServerAuthenticationException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.PARENT_HAS_ACTIVE_CHILD_CARD);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PARENT_HAS_ACTIVE_CHILD_CARD));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside RevokeCard.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
