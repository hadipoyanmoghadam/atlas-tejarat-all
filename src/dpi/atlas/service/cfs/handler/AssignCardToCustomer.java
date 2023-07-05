package dpi.atlas.service.cfs.handler;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * @author <a href="mailto:PNaeimi@dpi2.dpi.net.ir">Parisa Naeimi</a>
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.4 $ $Date: 2007/10/29 14:04:20 $
 */

public class AssignCardToCustomer extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

//1386/04/03 Boroon commented to minimize db access (This handler is not used anymore) - START
/*
        dpi.atlas.model.tj.entity.Customer customer = (dpi.atlas.model.tj.entity.Customer)holder.get(CFSConstants.CUSTOMER);
        Card card = (dpi.atlas.model.tj.entity.Card)holder.get(CFSConstants.CARD);
        card.setCustomer(customer);

        try {
            getCFSFacade().putCard(card);
        }
        catch (ModelException me) {
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR_RETRY , me);
        }

//        if (card.getCardPk() == null) // Card does not exist in DB
//            getCFSFacade().saveCard(card);
//        else // Card exists in DB, but hadn't been assigned to any customer
//            getCFSFacade().updateCard(card);

        if(log.isInfoEnabled())         log.info("Card " + card.getCardNo() + card.getSequenceNo() + " successfully assigned to customer " + customer.getCustomerId());

//        Card card = null;
//
//        if (holder.containsKey(CFSConstants.CARD)) { // Card exists in DB but not assigned to any customer yet
//            card = (Card)holder.get(CFSConstants.CARD);
//            card.setCustomer(customer);
//            getCFSFacade().updateCard(card);
//        }
//        else { // Card does not exist in DB
//            card = new Card();
//            card.setCardNo((String)msg.getAttribute(Fields.PAN));
//            card.setSequenceNo((String)msg.getAttribute(Fields.CARD_SEQUENCE_NUMBER));
//            card.setCustomer(customer);
//            getCFSFacade().saveCard(card);
//        }
*/
//1386/04/03 Boroon commented to minimize db access (This handler is not used anymore) - END        
    }

}