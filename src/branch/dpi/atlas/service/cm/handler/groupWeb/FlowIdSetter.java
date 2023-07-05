package branch.dpi.atlas.service.cm.handler.groupWeb;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 14, 2020
 * Time: 10:56 AM
 */
public class FlowIdSetter extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map map)  throws CMFault
    {

        String messageType =msg.getAttributeAsString(Fields.MESSAGE_TYPE);
        String flow = null;

        if (flow == null)
                 if (checkCase(messageType, Constants.CHARGE_RECORDS_MSG_TYPE) )   // chargeRecordsReq
                     flow = "1";

        if (flow == null)
            if (checkCase(messageType, Constants.BALANCE_GROUPCARD_MSG_TYPE) || checkCase(messageType, Constants.STATEMENT_GROUPCARD_MSG_TYPE) )   //Balance  && statement && DCHARGE groupCard
                flow = "2";

        if (flow == null)
            if ( checkCase(messageType, Constants.STATEMENT_ACCOUNT_MSG_TYPE))
                flow = "3";

        if (flow == null)
            if (checkCase(messageType, Constants.CHILD_INFO_MSG_TYPE) )   // childInfoReq
                flow = "4";
        if (flow == null)
            if (checkCase(messageType, Constants.SUMMARY_MSG_TYPE) )   // charge and transaction summary report
                flow = "5";


        if (log.isDebugEnabled()) log.debug("flow=" + flow);
        if (flow != null) msg.setAttribute(Fields.FLOW_ID, flow);
        else {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
        }
    }
    private boolean checkCase(String key, String value) {
        if (key == null)
            return false;
        if (value.indexOf(key) > -1)
            return true;
        return false;
    }

}
