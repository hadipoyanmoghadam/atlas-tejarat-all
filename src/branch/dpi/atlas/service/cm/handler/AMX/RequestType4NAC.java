package branch.dpi.atlas.service.cm.handler.AMX;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
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
 * Date: Dec 10, 2018
 * Time: 04:39 PM
 */
public class RequestType4NAC extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map map) throws CMFault {
        AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String request = amxMessage.getRequestType();
        String pin = amxMessage.getPin();
        String requestType = null;

        if (requestType == null)
            if (request.equals("1"))  // Create NAC
                requestType = "1";
        if (requestType == null) {
            if (request.equals("2"))   // Update NAC
                requestType = "2";
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
