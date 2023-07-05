package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

public class CheckGiftCardLimit extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        if ((!branchMsg.getPin().equalsIgnoreCase(Constants.PIN_DEPOSIT_GIFTCARD)) ||
                (branchMsg.getPin().equalsIgnoreCase(Constants.PIN_DEPOSIT_GIFTCARD) && !branchMsg.getRequestType().equalsIgnoreCase(Constants.SINGLE_GIFT_CARD)))
            return;
        try {

            String identityCode = branchMsg.getNationalCode();
            long amount = Long.parseLong(branchMsg.getAmount());
            String date = DateUtil.getSystemDate();

            long limitAmount = Long.parseLong(ChannelFacadeNew.getCMParam("GiftCArdLimit", "0").trim());

            if (ChannelFacadeNew.checkGiftCardLimit(identityCode, date, amount, limitAmount)) {
                //if return true then invalid operation
                throw new ModelException(ActionCode.GIFT_CARD_LIMIT_EXCEEDED);
            }

        } catch (ModelException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GIFT_CARD_LIMIT_EXCEEDED);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.GIFT_CARD_LIMIT_EXCEEDED);
        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Params.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_EXTERNAL, ActionCode.DATABASE_ERROR);
        } catch (Exception e) {
            log.error(":::Inside CheckGiftCardLimit.doProcess >>" + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.GENERAL_ERROR);
        }
    }
}