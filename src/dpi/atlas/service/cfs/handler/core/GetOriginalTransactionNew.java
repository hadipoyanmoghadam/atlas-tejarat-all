package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
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
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.7 $ $Date: 2007/10/29 14:04:20 $
 */

public class GetOriginalTransactionNew extends CFSHandlerBase implements Configurable {


    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {
            String pan = (String) msg. getAttribute(Fields.PAN);
            String terminal_id = (String) msg.getAttribute(Fields.TERMINAL_ID);
            String date = (String) msg.getAttribute(Fields.DATE);
            String time = (String) msg.getAttribute(Fields.TIME);
            String rrn=msg.getAttributeAsString(Fields.MN_RRN);

            Tx tx = CFSFacadeNew.getOriginalTx(pan, terminal_id, date, time,rrn);
            
            if (tx.getIsReversed() == CFSConstants.REVERSED)
                throw new CFSFault(CFSFault.FLT_ALREADY_REVERSED, new Exception(ActionCode.ALREADY_REVERSED));
            else if (tx.getIsReversed() != CFSConstants.NOT_REVERSED)
                throw new CFSFault(CFSFault.FLT_FORMAT_NOT_SUPPORTED, new Exception(ActionCode.FORMAT_NOT_SUPPORTED));
            else
                holder.put("tx", tx);
        } catch (NotFoundException e) {
            throw new CFSFault(CFSFault.REVERSE_HAS_NO_ORIGINAL, new Exception(ActionCode.REVERSE_HAS_NO_ORIGINAL));
        } catch (SQLException e) {
            log.error("Can not run SQL Statement: " + e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        }

    }
}
