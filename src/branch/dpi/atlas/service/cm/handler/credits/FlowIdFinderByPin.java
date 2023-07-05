package branch.dpi.atlas.service.cm.handler.credits;

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
 * Date: Sep 4, 2017
 * Time: 02:42 PM
 */
public class FlowIdFinderByPin extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map map) throws CMFault
    {
        String pin = msg.getAttributeAsString(Fields.PIN);
        String flowID = null;

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_CREDITS_BAL) || pin.equals(Constants.PIN_CREDITS_DEPOSIT) || pin.equals(Constants.PIN_CREDITS_DEPOSIT_REVERSE) ||
                    pin.equals(Constants.PIN_CREDITS_WITHDRAW) || pin.equals(Constants.PIN_CREDITS_WITHDRAW_REVERSE)|| pin.equals(Constants.PIN_CREDITS_FOLLOWUP) ||
                    pin.equals(Constants.PIN_CREDITS_STATEMENT))
                flowID = "1";
        if(flowID ==  null)
            if(pin.equals(Constants.PIN_CREDITS_PICHACK_CHECK_STATUS))
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
