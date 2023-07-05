package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Sep 15, 2013
 * Time: 11:04:13 AM
 */
public class RequestType4NAC extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map map) throws CMFault {
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String request = branchMsg.getRequestType();
        String pin=branchMsg.getPin();
        String requestType = null;

        if (requestType == null)
            if (request.equals("1"))  // Create NAC
                requestType = "1";
        if (requestType == null){
            if(pin.equals("60100")){
            if (request.equals("2"))   // Update NAC
                requestType = "2";
            }
        }
        if (log.isDebugEnabled())
            log.debug("requestType=" + requestType);
        if (requestType != null)
            msg.setAttribute(Fields.NAC_REQUEST_TYPE, requestType);
        else {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_SERVICE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_SERVICE));
        }
    }
}
