package branch.dpi.atlas.service.cm.handler.branch;

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
public class FlowIdFinderByPin extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map map) throws CMFault
    {
        String pin = msg.getAttributeAsString(Fields.PIN);
        String flowID = null;

        if(flowID == null)
            if(pin.equals(Constants.PIN_CREATE_NAC))
                flowID = "1";
        if(flowID == null)
            if(pin.equals(Constants.PIN_GET_ACCOUNT_STATUS) || pin.equals(Constants.PIN_GET_MINI_ACCOUNT_STATUS))
                flowID = "2";
        if(flowID == null)
            if(pin.equals(Constants.PIN_UPDATE_ACCOUNT))
                flowID = "3";
        if(flowID == null)
            if(pin.equals(Constants.PIN_ONLINE_ACCOUNT))
                flowID = "4";
        if(flowID == null)
            if(pin.equals(Constants.PIN_ACCOUNT_LIST))
                flowID = "5";
        if(flowID == null)
            if(pin.equals(Constants.PIN_SET_SERVICE_STATUS))
                flowID = "6";
        if(flowID == null)
            if(pin.equals(Constants.PIN_CREATE_NAC_BATCH))
                flowID = "7";
        if(flowID == null)
            if(pin.equals(Constants.PIN_BLOCK_REPORT) || pin.equals(Constants.PIN_SIMIN_BLOCK_REPORT))
                flowID = "8";
        if(flowID == null)
            if(pin.equals(Constants.PIN_ACCOUNT_TO_CARD))
                flowID = "9";
        if(flowID == null)
            if(pin.equals(Constants.PIN_SIMIN_ACCOUNT_BLOCK_REPORT) || pin.equals(Constants.PIN_ACCOUNT_BLOCK_REPORT))
                flowID = "10";
        if(flowID == null)
            if(pin.equals(Constants.PIN_REMOVE_ROW))
                flowID = "11";
        if(flowID == null)
            if(pin.equals(Constants.PIN_UPDATE_ROW))
                flowID = "12";
        if(flowID == null)
            if(pin.equals(Constants.PIN_PINPAD_ACCOUNT))
                flowID = "13";
        if(flowID ==  null)
            if(pin.equals(Constants.PIN_CHANGE_ACCOUNT_STATUS) || pin.equals(Constants.PIN_RESET_ACCOUNT) ||
                    pin.equals(Constants.PIN_CANCELLATION_GIFTCARD)  || pin.equals(Constants.PIN_WITHDRAW_REMITTANCE)
                    ||pin.equals(Constants.PIN_RESEND_PAYA))
                flowID = "14";
        if(flowID ==  null)
            if(pin.equals(Constants.PIN_BALANCE) || pin.equals(Constants.PIN_DEPOSIT) || pin.equals(Constants.PIN_DEPOSIT_REVERSE) ||
                    pin.equals(Constants.PIN_WITHDRAW) || pin.equals(Constants.PIN_WITHDRAW_REVERSE)|| pin.equals(Constants.PIN_FOLLOWUP) ||
                    pin.equals(Constants.PIN_STATEMENT) || pin.equals(Constants.PIN_COUNT_STATEMENT) ||  pin.equals(Constants.PIN_DEPOSIT_GIFTCARD) ||
            pin.equals(Constants.PIN_DEPOSIT_GIFTCARD_REVERSE) || pin.equals(Constants.PIN_WITHDRAW_GIFTCARD) ||
            pin.equals(Constants.PIN_DISCHARGE_GIFTCARD) || pin.equals(Constants.PIN_FULL_STATEMENT)|| pin.equals(Constants.PIN_WITHDRAW_ATM)||pin.equals(Constants.PIN_DEPOSIT_ATM)
                    ||pin.equals(Constants.PIN_REVERSE_DEPOSIT_ATM)||pin.equals(Constants.PIN_REVERSE_WITHDRAW_ATM)
                    ||pin.equals(Constants.PIN_SEND_PAYA)
                    ||  pin.equals(Constants.PIN_WITHDRAW_WAGE) || pin.equals(Constants.PIN_REVERSE_WITHDRAW_WAGE))
            flowID = "15";

        if(flowID == null)
            if(pin.equals(Constants.PIN_ACCOUNT_TYPE_INQUIRY))
                flowID = "16";

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_CHANGE_ACCOUNT_TYPE))
                flowID = "19";

        if(flowID ==  null)
            if(pin.equals(Constants.PIN_ACCOUNT_INQUIRY))
                flowID = "20";
        
        if (flowID == null)
            if (pin.equals(Constants.PIN_IsValid_Verhoef) || pin.equals(Constants.PIN_IsValid_ExpenseVerhoef))
                flowID = "21";
        if (flowID == null)
            if (pin.equals(Constants.PIN_REMITTANCE_INQUIRY))
                flowID = "22";
        if (flowID == null)
            if (pin.equals(Constants.PIN_INQUIRY_ATM))
                flowID = "27";
        if (flowID == null)
            if (pin.equals(Constants.PIN_REGISTER_PAYA_REQUEST))
                flowID = "30";
        if (flowID == null)
            if (pin.equals(Constants.PIN_GET_PAYA))
                flowID = "31";
        if (flowID == null)
            if (pin.equals(Constants.PIN_INACTIVE_PAYA))
                flowID = "32";
        if (flowID == null)
            if (pin.equals(Constants.PIN_GET_IN_DUE_DATE_PAYA))
                flowID = "33";
        if (flowID == null)
            if (pin.equals(Constants.PIN_DELETE_PAYA))
                flowID = "34";
        if (flowID == null)
            if (pin.equals(Constants.PIN_REPORT_PAYA))
                flowID = "35";

        if (log.isDebugEnabled())
            log.debug("flowID=" + flowID);
        if (flowID != null)
        msg.setAttribute(Fields.FLOW_ID, flowID);
       else {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_SERVICE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_SERVICE));
        }
    }
}
