package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: jan 12, 2015
 * Time: 2:04:03 PM
 */
public class CreateCardInFaragir extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CUSTOMERType customerMsg = (CUSTOMERType) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String accountNo = customerMsg.getAccountno();
        String accountGroup = customerMsg.getAccountgroup();
        String cardNo = customerMsg.getCardno();
        String editDate = customerMsg.getEditdate();
        String latinName = customerMsg.getNamefamilylatin();

        try {

            if (ChannelFacadeNew.ExistCardInDB(cardNo)) {
                ChannelFacadeNew.UpdateCardForFARA(cardNo, accountNo, editDate, accountGroup,"");
                msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.SUCCESSFULL_WITH_CARD_EXISTANCE_MESSAGE);
            } else
                ChannelFacadeNew.InsertCardForFARA(cardNo, accountNo, editDate, accountGroup,"",latinName);

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CreateCardInFaragir.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

