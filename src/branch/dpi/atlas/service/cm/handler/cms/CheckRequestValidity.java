package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.reCreate.RECREATEType;
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
 * Date: jan 13, 2015
 * Time: 1:44:03 PM
 */
public class CheckRequestValidity extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        int updateCount;
        RECREATEType reCreateMsg = (RECREATEType) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String cardNo = reCreateMsg.getCardno();
        String newCardNo = reCreateMsg.getNewcardno();

        try {

            if (cardNo.equalsIgnoreCase(newCardNo)){
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.SOURCE_AND_DESTINATION_ACCOUNTS_ARE_IDENTICAL);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.SOURCE_AND_DESTINATION_ACCOUNTS_ARE_IDENTICAL));
            }

            if (ChannelFacadeNew.ExistCardInDB(newCardNo)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE));
            }

        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CheckRequestValidity.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}