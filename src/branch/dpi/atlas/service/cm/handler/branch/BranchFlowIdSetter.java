package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;

import java.util.Map;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: Apr 21, 2013
 * Time: 11:04:13 AM
 */
public class BranchFlowIdSetter extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map map)
    {
        String exist = msg.getAttributeAsString(Fields.BRANCH_TX_EXIST);
        String origExist = msg.getAttributeAsString(Fields.BRANCH_ORIG_EXIST);
        boolean isReversed = (Boolean)msg.getAttribute(Fields.ISREVERSED);
        String mainFlowID = null;

        if(exist.equals("1"))
            if(!isReversed)
                mainFlowID = "1";
                else
                mainFlowID = "2";
        if(mainFlowID == null)
            if(isReversed)
                if (origExist.equals("0"))
                    mainFlowID = "3";
        if(mainFlowID ==  null)
            mainFlowID = "4";
        
        msg.setAttribute(Fields.MAIN_FLOW_ID, mainFlowID);
        if(log.isDebugEnabled())
            log.debug("mainFlowID="+ mainFlowID);
    }
}
