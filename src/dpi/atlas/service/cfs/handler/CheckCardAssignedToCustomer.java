package dpi.atlas.service.cfs.handler;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * @author <a href="mailto:PNaeimi@dpi2.dpi.net.ir">Parisa Naeimi</a>
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.5 $ $Date: 2007/10/29 14:04:20 $
 */

public class CheckCardAssignedToCustomer extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

//1386/04/03 Boroon commented to minimize db access (This handler is not used anymore) - START
/*
        String cardNo = (String)msg.getAttribute(Fields.PAN);
        String cardSeqNo = (String)msg.getAttribute(Fields.CARD_SEQUENCE_NUMBER);

        dpi.atlas.model.tj.entity.Card card = (dpi.atlas.model.tj.entity.Card)holder.get(CFSConstants.CARD);

        if (card.getCustomer() != null) { // Card exists and assigned to a customer
            if(log.isInfoEnabled())         log.info("Card " + cardNo + cardSeqNo + " already assigned to customer " + card.getCustomer().getCustomerId());
            throw new CFSFault(CFSFault.FLT_DUPLICATE_NEW_RECORD_REJECTED_LOG, new Exception(ActionCode.DUPLICATE_NEW_RECORD_REJECTED));
        }
        else
            if(log.isInfoEnabled())         log.info("Card " + cardNo + cardSeqNo + " has not assigned to any customer yet");

//        else // Card exists in DB but not assigned to any customer, so put it in holder for future use
//            holder.put(CFSConstants.CARD, card);


//        Customer customer = (Customer)holder.get(CFSConstants.CUSTOMER);
//
//        Set customerCardsSet = customer.getCards();
//
//        if (log.isDebugEnabled())             log.debug(customerCardsSet);
//
//        Iterator it = customerCardsSet.iterator();
//        while (it.hasNext()) {
//            Card card = (Card)it.next();
//            if (log.isDebugEnabled())             log.debug(card);
//            if (card.getCardNo().equals(cardNo) && card.getSequenceNo().longValue() == Long.parseLong(cardSeqNo)) {
//                if(log.isInfoEnabled())         log.info("Card " + card.getCardNo() + " already assigned to customer " + customer.getCustomerPk());
//                throw new CFSFault(CFSFault.FLT_DUPLICATE_NEW_RECORD_REJECTED, new Exception(ActionCode.DUPLICATE_NEW_RECORD_REJECTED));
//            }
//        }
*/
//1386/04/03 Boroon commented to minimize db access (This handler is not used anymore) - END
    }

}