package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.GiftCard;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.0 $ $Date: 2015/06/09 13:17:49 $
 */

public class GetGiftCardTx extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String branchCode=msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
        String requestNo=msg.getAttributeAsString(Fields.REQUEST_NO);
        String cardNo=msg.getAttributeAsString(Fields.PAN);

        GiftCard giftCard;
        try {
            if (log.isDebugEnabled()) log.debug("branchCode=" + branchCode);
                giftCard = CFSFacadeNew.getGiftCardTx(branchCode, requestNo, cardNo, TJCommand.CMD_CMS_GIFTCARD_CHARGE);

            if(giftCard.getIsDone().equals(Constants.REVERSED))
                throw new CFSFault(CFSFault.FLT_ALREADY_REVERSED, ActionCode.ALREADY_REVERSED);

        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.REVERSE_HAS_NO_ORIGINAL, ActionCode.REVERSE_HAS_NO_ORIGINAL);
        } catch (SQLException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        }

        holder.put(Fields.GIFT_CARD_TX,giftCard);

    }
}
