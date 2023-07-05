package branch.dpi.atlas.service.cm.handler.AMX;

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
            if(pin.equals(Constants.PIN_CHANGE_ACCOUNT_STATUS_AMX))
            flowID = "1";

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_CREATE_NAC_AMX))
                flowID = "2";
        if(flowID ==  null)
            if(pin.equals(Constants.PIN_FUND_TRANSFER_SAFE_BOX))
                flowID = "3";
//        if(flowID ==  null)
//            if(pin.equals(Constants.PIN_FUND_TRANSFER_REVERSE_SAFE_BOX))
//                flowID = "4";
        if(flowID ==  null)
            if(pin.equals(Constants.PIN_FOLLOW_UP_SAFE_BOX))
                flowID = "5";
        if(flowID ==  null){
            if(msg.getAttributeAsString(CMMessage.SERVICE_TYPE).equalsIgnoreCase(Constants.MARHONAT_SERVICE) &&
            pin.equals(Constants.PIN_BALANCE_SAFE_BOX))
                flowID = "6";
        }

        
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
