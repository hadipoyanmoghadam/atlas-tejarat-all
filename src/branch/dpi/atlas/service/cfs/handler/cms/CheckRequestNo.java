package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.tj.entity.GiftCard;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.0 $ $Date: 2015/06/20 16:17:49 $
 */

public class CheckRequestNo extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String branchCode=msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
        String requestNo=msg.getAttributeAsString(Fields.REQUEST_NO);

        GiftCard giftCard;
        try {
            if (log.isDebugEnabled()) log.debug("branchCode=" + branchCode);

            giftCard = CFSFacadeNew.getGiftCardTx(branchCode, requestNo,TJCommand.CMD_CMS_CANCELLATION);
            if(giftCard!=null)
                throw new CFSFault(CFSFault.FLT_CANCELLATION_ALREADY_EXISTS, ActionCode.CMS_CANCELLATION_ALREADY_EXISTS);

                giftCard = CFSFacadeNew.getGiftCardTx(branchCode, requestNo,TJCommand.CMD_CMS_GIFTCARD_CHARGE);
            if(giftCard==null)
                throw new CFSFault(CFSFault.FLT_REQUEST_NUMBER_NOT_FOUND, ActionCode.CMS_REQUEST_NUMBER_NOT_FOUND);

        } catch (SQLException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        }

        msg.setAttribute(Fields.NATIONAL_CODE, giftCard.getNationalcode());
        msg.setAttribute(Fields.BRANCH_DOC_NO, giftCard.getDocNo());

    }
}
