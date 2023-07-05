package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: jan 12, 2015
 * Time: 10:04:03 PM
 */
public class SeparatCardFromAccount extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        int updateCount;
        CUSTOMERType customerMsg = (CUSTOMERType) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String accountNo = customerMsg.getAccountno();
        String cardNo = customerMsg.getCardno();

        try {

            if (msg.getAttributeAsString(Fields.HOST_ID).equalsIgnoreCase(Constants.HOST_ID_FARAGIR))
                updateCount = ChannelFacadeNew.DeactiveCardAccountInNonCFS(accountNo, cardNo);
            else
                updateCount = ChannelFacadeNew.DeactiveCardAccountInCFSCARD(accountNo, cardNo);

            if (updateCount <= 0) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD));
            }

        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside SeparatCardFromAccount.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}