package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.GiftCard;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.0 $ $Date: 2015/12/15 13:17:49 $
 */

public class GetBatchDepositTx extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String accountNo=msg.getAttributeAsString(Fields.BATCH_CREDIT_ACCOUNT);
        String branchCode = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
        GiftCard giftCard;
        try {
            if (log.isDebugEnabled()) log.debug("branchCode=" + branchCode);
            giftCard = CFSFacadeNew.getBatchDepositTx(branchCode,accountNo);

        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_TRANSACTION_NOT_FOUND, ActionCode.TRANSACTION_NOT_FOUND);
        } catch (SQLException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        }

        msg.setAttribute(Fields.AMOUNT, giftCard.getAmount());
        msg.setAttribute(Fields.BRANCH_DOC_NO, giftCard.getDocNo());
        msg.setAttribute(Fields.TRANS_DATE, giftCard.getDate());
        holder.put(Fields.GIFT_CARD_CLASS, giftCard);


    }
}
