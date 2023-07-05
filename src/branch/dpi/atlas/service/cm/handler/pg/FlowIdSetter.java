package branch.dpi.atlas.service.cm.handler.pg;

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
 * Date: September 11, 2017
 * Time: 10:24:03
 */
public class FlowIdSetter extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map map)  throws CMFault
    {

        String messageType =msg.getAttributeAsString(Fields.MESSAGE_TYPE);
        String flow = null;

            if (checkCase(messageType, Constants.POLICY_UPDATE_MSG_TYPE) )   // PolicyUpdateReq
                flow = "1";
        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_UPDATE_ALL_MSG_TYPE) )   // PolicyUpdateAllReq
                flow = "2";
        if (flow == null)
             if (checkCase(messageType, Constants.POLICY_UPDATE_COLLECTION_MSG_TYPE) )   // PolicyUpdate cardList Req
                 flow = "3";

        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_PRESENTATION_MSG_TYPE) )   // PolicyPresentationReq
                flow = "4";

        if (flow == null)
                 if (checkCase(messageType, Constants.CHARGE_RECORDS_MSG_TYPE) )   // chargeRecordsReq
                     flow = "5";

        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_ENDED_PRESENTATION_MSG_TYPE) )   // PolicyEndedPresentationReq
                flow = "6";

        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_CREATE_MSG_TYPE) )   // PolicyCreateReq
                flow = "7";
        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_HISTORY_MSG_TYPE) )   // PolicyHistoryReq
                flow = "8";
        if (flow == null)
            if (checkCase(messageType, Constants.CHARGE_REPORT_MSG_TYPE)  )   // ChargeReportReq
                flow = "9";

        if (flow == null)
            if (checkCase(messageType, Constants.BALANCE_GROUPCARD_MSG_TYPE) || checkCase(messageType, Constants.STATEMENT_GROUPCARD_MSG_TYPE) )   //Balance  && statement && DCHARGE groupCard
                flow = "10";

        if (flow == null)
            if ( checkCase(messageType, Constants.DCHARGE_MSG_TYPE)|| checkCase(messageType, Constants.WFP_STATEMENT_MSG_TYPE) ||
                    checkCase(messageType, Constants.IMMEDIATE_CHARGE_MSG_TYPE) || checkCase(messageType, Constants.STATEMENT_ACCOUNT_MSG_TYPE))
                flow = "11";

        if (flow == null)
                  if (checkCase(messageType, Constants.SMS_REGISTER_MSG_TYPE) )   // SMSRegisterReq
                      flow = "12";
        if (flow == null)
                  if (checkCase(messageType, Constants.SMS_INQUIRY_MSG_TYPE) )   // SMSInquiryReq
                      flow = "13";

        if (flow == null)
            if (checkCase(messageType, Constants.CHILD_INFO_MSG_TYPE) )   // childInfoReq
                flow = "14";
        if (flow == null)
            if (checkCase(messageType, Constants.WFP_CHARGE_MSG_TYPE) )   //
                flow = "15";
        if (flow == null)
            if (checkCase(messageType, Constants.SUMMARY_MSG_TYPE) )   // charge and transaction summary report
                flow = "16";
        if (flow == null)
            if (checkCase(messageType, Constants.STOCK_DEPOSIT_MSG_TYPE) )   // stock deposit
                flow = "17";
        if (flow == null)
            if (checkCase(messageType, Constants.STOCK_FOLLOWUP_MSG_TYPE) )   // stock followUp
                flow = "18";

        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_ENDED_MSG_TYPE) )   // PolicyEndedReq
                flow = "22";


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
