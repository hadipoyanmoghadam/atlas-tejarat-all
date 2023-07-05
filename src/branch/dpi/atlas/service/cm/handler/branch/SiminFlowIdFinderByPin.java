package branch.dpi.atlas.service.cm.handler.branch;

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
 * Date: Apr 21, 2013
 * Time: 11:04:13 AM
 */
public class SiminFlowIdFinderByPin extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map map) throws CMFault
    {
        String pin = msg.getAttributeAsString(Fields.PIN);
        String flowID = null;
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String accountStatus = (null != branchMsg.getAccountStatus() ? branchMsg.getAccountStatus().trim() : "");

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_ACCOUNT_INFO_SIMIN) ||
                    pin.equals(Constants.PIN_REMOVE_DOC_SIMIN) || pin.equals(Constants.PIN_PIN_GET_STATEMENT_SIMIN))
                flowID = "1";

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_PIN_SET_SERVICE_STATUS_SIMIN))
                flowID = "2";
        if(flowID ==  null)
            if(pin.equals(Constants.PIN_CREATE_SIMIN_NAC))
                flowID = "6";

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_DEPOSIT_BLOCK_SIMIN))
                flowID = "3";

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_CREATE_NAC_ATM))
                flowID = "7";

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_OFFLINE_NAC_ATM))
                flowID = "8";

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_CHANGE_ATM_STATUS))
                flowID = "9";

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_CHANGE_STATUS_CBI))
                flowID = "11";

        if (flowID == null)
            if (pin.equals(Constants.PIN_CHANGE_ACCOUNT_STATUS_SIMIN)) {
                if (accountStatus.equals(Constants.BLOCK_ACCOUNT_STATUS) || accountStatus.equals(Constants.UNBLOCK_ACCOUNT_STATUS)) {
                    flowID = "12";
                } else
                    flowID = "1";
            }
        if(flowID ==  null)
            if(pin.equals(Constants.PIN_CHANGE_DEVICE_BRANCH))
                flowID = "13";

        if(log.isDebugEnabled())
            log.debug("flowID="+ flowID);
        if (flowID != null)
        msg.setAttribute(Fields.FLOW_ID, flowID);
       else {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
        }
    }
}
