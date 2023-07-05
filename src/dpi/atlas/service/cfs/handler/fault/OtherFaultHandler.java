package dpi.atlas.service.cfs.handler.fault;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class OtherFaultHandler extends CFSFaultHandler {
    private static Log log = LogFactory.getLog(OtherFaultHandler.class);

    public String getActionCode(Object obj, Map holder) throws CFSFault {
        CMMessage msg = ((CMMessage) obj);
        String faultCode = (String) msg.getAttribute(CMMessage.FAULT_CODE);
        String resp;
        if (faultCode.equals(CFSFault.FLT_DUPLICATE))
            resp = ActionCode.DUPLICATE;
        else
            resp = ActionCode.GENERAL_ERROR;

        return resp;
    }

}