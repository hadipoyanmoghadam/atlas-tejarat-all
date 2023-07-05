package branch.dpi.atlas.service.cm.handler.cms;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: March 7, 2015
 * Time: 9:24:03 AM
 */
public class MainFlowIdSetter extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map map)  throws CMFault
    {
        String exist = msg.getAttributeAsString(Fields.EXIST);
        String mainFlowID = null;

        if(exist.equals("1"))
           mainFlowID = "1";         //Duplicate message
        if(mainFlowID ==  null)
           mainFlowID = "2";

        msg.setAttribute(Fields.MAIN_FLOW_ID, mainFlowID);
        if(log.isDebugEnabled())
            log.debug("mainFlowID="+ mainFlowID);
    }
}
