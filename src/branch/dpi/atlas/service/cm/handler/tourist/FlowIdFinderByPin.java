package branch.dpi.atlas.service.cm.handler.tourist;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Apr 24, 2017
 * Time: 09:53 AM
 */
public class FlowIdFinderByPin extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map map) throws CMFault
    {
        String pin = msg.getAttributeAsString(Fields.PIN);
        String flowID = null;

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_TOURIST_BAL) || pin.equals(Constants.PIN_TOURIST_CARD_STATEMENT) ||
                    pin.equals(Constants.PIN_TOURIST_CHARGE) || pin.equals(Constants.PIN_TOURIST_DISCHARGE) ||
                    pin.equals(Constants.PIN_TOURIST_FOLLOWUP)|| pin.equals(Constants.PIN_TOURIST_CHARGE_STATEMENT) ||
                    pin.equals(Constants.PIN_TOURIST_REVOKE) || pin.equals(Constants.PIN_TOURIST_FUND_TRANSFER))
            flowID = "1";
        if(flowID ==  null)
            if(pin.equals(Constants.PIN_TOURIST_SUMMARY_REPORT))
                flowID = "2";
        
        if(log.isDebugEnabled())
            log.debug("flowID="+ flowID);
        if (flowID != null)
        msg.setAttribute(Fields.FLOW_ID, flowID);
       else {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_SERVICE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_SERVICE));
        }
    }
}
