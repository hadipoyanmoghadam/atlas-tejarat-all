package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:Behnaz.sh@dpi.ir">SH.Behnaz</a>
 * @version $Revision: 1.1 $ $Date: 2013/9/28 8:57:04 $
  */

public class FollowUpTransactionBySequence extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String serviceType=msg.getAttributeAsString(Fields.SERVICE_TYPE);
        String origMessageData = msg.getAttributeAsString(Fields.ORIG_MESSAGE_DATA);

        String origmsgSeq = origMessageData.substring(0, 12);
        String date = origMessageData.substring(12, 20);
        String time = origMessageData.substring(20, 26);

        if(serviceType!=null && serviceType.trim().equalsIgnoreCase(Constants.CREDITS_SERVICE)){
            if(origmsgSeq!=null && origmsgSeq.trim().startsWith(Constants.NC_SERVICE))
                serviceType=Constants.NC_SERVICE;
        }

        try {

        boolean exist = CFSFacadeNew.getFollowTx(date, origmsgSeq, time,serviceType);
         if(exist)
             msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, ActionCode.APPROVED);
         else
         {
             msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, ActionCode.FLW_HAS_NO_ORIGINAL);
             msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.FLW_HAS_NO_ORIGINAL);

         }
          

        } catch (SQLException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, ActionCode.DATABASE_ERROR);
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}

