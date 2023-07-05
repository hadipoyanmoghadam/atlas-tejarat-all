package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.GiftCard;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:Behnaz.sh@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.1 $ $Date: 2015/6/20 9:37:04 $
 */

public class GetOriginalTransaction4GiftCard extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        try {

            GiftCard giftCard = (GiftCard) holder.get(Fields.GIFT_CARD_TX);

            String branchCode = giftCard.getBranchCode();
            String cardNo = giftCard.getCardNo();
            String destAccountNo = giftCard.getDestAccountNo();
            String docNo = giftCard.getDocNo();
            String date = giftCard.getDate();
            String time = giftCard.getTime();
            String amount = giftCard.getAmount();


            Tx tx = CFSFacadeNew.getOriginalTx4GiftCard(destAccountNo, date, amount, branchCode, docNo, time, cardNo);
            log.info("tx.getTxPk() = " + tx.getTxPk());

            if (tx.getIsReversed() == CFSConstants.REVERSED)
                throw new CFSFault(CFSFault.FLT_ALREADY_REVERSED, ActionCode.ALREADY_REVERSED);
            else if (tx.getIsReversed() != CFSConstants.NOT_REVERSED)
                throw new CFSFault(CFSFault.FLT_FORMAT_NOT_SUPPORTED, ActionCode.FORMAT_NOT_SUPPORTED);
            else
                holder.put("tx", tx);
        } catch (NotFoundException e) {
            throw new CFSFault(CFSFault.FLT_UNABLE_TO_LOCATE_RECORD_ON_FILE, ActionCode.UNABLE_TO_LOCATE_RECORD_ON_FILE);
        } catch (SQLException e) {
            log.error("Can not run SQL Statement: " + e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        }
    }

}
