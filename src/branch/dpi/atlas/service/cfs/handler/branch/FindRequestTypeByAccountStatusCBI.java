package branch.dpi.atlas.service.cfs.handler.branch;


import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * User: F.Heydari
 * Date: Dec 10, 2020
 * Time: 10:09:39 AM
 */
public class FindRequestTypeByAccountStatusCBI extends CMHandlerBase implements Configurable {

    public void doProcess(CMMessage cmMessage, Map map) throws CFSFault {
        try {
            String ResponseID = "RequestType";
            String accountStatus = cmMessage.getAttributeAsString(Fields.ACCOUNT_STATUS);
            if (log.isDebugEnabled()) log.debug("accountStatus= " + accountStatus);
            String flow = null;
           {
                if(accountStatus.equalsIgnoreCase("6"))
                    //block amount
                    flow = "1";
                else if (accountStatus.equalsIgnoreCase("4"))
                    //unblock amount
                    flow = "2";
            }

            if (log.isDebugEnabled()) log.debug("RequestType = " + flow);
            if (flow != null) cmMessage.setAttribute(ResponseID, flow);
            else {
                throw new CFSFault(CFSFault.FLT_UNSUPPORTED_MESSAGE, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
            }
        } catch (CFSFault e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inside:: FindRequestTypeByAccountStatusCBI:: " + e);
            cmMessage.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }


    }

}
