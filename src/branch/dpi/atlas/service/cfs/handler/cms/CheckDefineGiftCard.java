package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.0 $ $Date: 2015/06/10 9:07:49 $
 */

public class CheckDefineGiftCard extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String cardNo = msg.getAttributeAsString(Fields.PAN);

        try {
            if (CFSFacadeNew.ExistCardInDB(cardNo)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE);
                throw new CFSFault(CFSFault.FLT_CARD_HAS_BEEN_DEFINED_BEFORE , new Exception(ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE));
            }

        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        }
    }

}
