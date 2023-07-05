package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Card;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;


public class CheckCardNew extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String cardNo = msg.getAttributeAsString(Fields.PAN);

        if (cardNo == null || cardNo.equals(""))
        {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
            throw new CFSFault(CFSFault.FLT_INVALID_CARD_NUMBER, new Exception(ActionCode.INVALID_CARD_NUMBER));
        }
        Card card ;
        try {
            
            card = CFSFacadeNew.getCard(cardNo);
        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.debug("Card " + cardNo);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
            throw new CFSFault(CFSFault.FLT_INVALID_CARD_NUMBER, new Exception(ActionCode.INVALID_CARD_NUMBER));
        }
        catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        }
        holder.put(CFSConstants.CARD, card);
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);    
    }
}
