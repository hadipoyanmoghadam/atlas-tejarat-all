package branch.dpi.atlas.service.cm.handler.manzume;


import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.util.Map;

/**
 * User: F.Heydari
 * Date: OCT 24, 2022
 * Time: 11:13:00 AM
 */

public class FlowIdFinderByPin extends TJServiceHandler {
    public void doProcess(CMMessage msg, Map map) throws CMFault
    {
        String pin = msg.getAttributeAsString(Fields.PIN);
        String flowID = null;

        if(flowID == null)
            if(pin.equals(Constants.PIN_ACCOUNT_INFO_MANZUME))
                flowID = "1";
        if(flowID == null)
            if(pin.equals(Constants.PIN_DEPOSIT_MANZUME) )
                flowID = "2";
        if(flowID == null)
            if(pin.equals(Constants.PIN_DEPOSIT_REVERSE_MANZUME))
                flowID = "3";
        if(flowID == null)
            if(pin.equals(Constants.PIN_FOLLOWUP_MANZUME))
                flowID = "4";

        if (log.isDebugEnabled())
            log.debug("flowID=" + flowID);
        if (flowID != null)
            msg.setAttribute(Fields.FLOW_ID, flowID);
        else {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_SERVICE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_SERVICE));
        }
    }
}
