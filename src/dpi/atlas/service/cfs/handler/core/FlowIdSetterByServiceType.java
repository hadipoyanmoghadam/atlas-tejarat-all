package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * User: R.Nasiri
 * Date: june 22, 2013
 * Time: 12:15:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlowIdSetterByServiceType extends CMHandlerBase implements Configurable {

    public void doProcess(CMMessage cmMessage, Map map) throws CMFault {
        String ResponseID = "FlowID";

        String serviceType = getAttribute(cmMessage, map, "serviceType");
        if (log.isDebugEnabled()) log.debug("serviceType= " + serviceType);

        String flow = null;
        if (serviceType.equalsIgnoreCase(Constants.ISO_SERVICE) || serviceType.equalsIgnoreCase(Fields.SERVICE_PG) ||
                serviceType.equalsIgnoreCase(Fields.SERVICE_GROUP_WEB))  //ISO or PG or GroupWeb
                    flow = "1";
                else
                    flow = "2";  // EBanking

        if (log.isDebugEnabled()) log.debug("flow = " + flow);
        if (flow != null) cmMessage.setAttribute(ResponseID, flow);
    }

    private boolean checkCase(String key, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, ",");
        boolean existanceFlag = false;
        while (tokenizer.hasMoreTokens()) {
            String val = tokenizer.nextToken();
            if (val.equalsIgnoreCase(key)) {
                existanceFlag = true;
                break;
            }
        }
        return existanceFlag;
    }

    private String getAttribute(CMMessage cmMessage, Map map, String key) {
        String str = (String) cmMessage.getAttribute(key);
        if (str == null)
            str = (String) map.get(key);
        return str;
    }

}
