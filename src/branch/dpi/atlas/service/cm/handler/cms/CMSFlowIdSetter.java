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
 * Date: jan 10, 2015
 * Time: 2:24:03 PM
 */
public class CMSFlowIdSetter extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map map)  throws CMFault
    {

        String messageType =msg.getAttributeAsString(Fields.MESSAGE_TYPE);
        String flow = null;

        if (checkCase(messageType, Constants.CREATE_MSG_TYPE))   //recordFlag=11
                flow = "1";
        if (flow == null)
            if (checkCase(messageType, Constants.SEPARAT_MSG_TYPE)) //recordFlag=44
                flow = "2";
        if (flow == null)
            if (checkCase(messageType, Constants.RECREATE_MSG_TYPE))   //ReCreate
                flow = "3";
        if (flow == null)
            if (checkCase(messageType, Constants.ACCOUNTINFO_MSG_TYPE))   //AccountInfo
                flow = "4";
        if (flow == null)
            if (checkCase(messageType, Constants.CARDINFO_MSG_TYPE))   //CardInfoReq
                flow = "5";
        if (flow == null)
            if (checkCase(messageType, Constants.REVOKE_MSG_TYPE))   //RevokeReq
                flow = "6";
        if (flow == null)
            if (checkCase(messageType, Constants.UPDATE_MSG_TYPE) || checkCase(messageType, Constants.JOINT_UPDATE_MSG_TYPE)) //recordFlag=22
                flow = "8";
        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_CREATE_MSG_TYPE))   //PolicyCreateReq
                flow = "15";
        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_UPDATE_MSG_TYPE) )   // PolicyUpdateReq
                flow = "16";
        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_PRESENTATION_MSG_TYPE) )   // PolicyPresentationReq
                flow = "17";
        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_UPDATE_ALL_MSG_TYPE) )   // PolicyUpdateAllReq
                flow = "18";
        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_UPDATE_COLLECTION_MSG_TYPE) )   // PolicyUpdate cardList Req
                flow = "19";
        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_CREATE_BATCH_MSG_TYPE) )   // create card and Policy batch Req
                flow = "20";
        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_ENDED_PRESENTATION_MSG_TYPE) )   // PolicyEndedPresentationReq
                flow = "21";
        if (flow == null)
            if (checkCase(messageType, Constants.POLICY_ENDED_MSG_TYPE) )   // PolicyEndedReq
                flow = "22";
        if (flow == null)
            if (checkCase(messageType, Constants.BALANCE_MSG_TYPE) || checkCase(messageType, Constants.DEPOSIT_GIFTCARD_MSG_TYPE) || checkCase(messageType, Constants.BALANCE_GIFTCARD_MSG_TYPE) || checkCase(messageType, Constants.DCHARGE_MSG_TYPE) )   //Balance  && getDepositTX
                            flow = "10";
        if (flow == null)
            if (checkCase(messageType, Constants.CHARGE_GIFTCARD_MSG_TYPE) || checkCase(messageType, Constants.DISCHARGE_GIFTCARD_MSG_TYPE) ||
                    checkCase(messageType, Constants.IMMEDIATE_CHARGE_MSG_TYPE))   //GiftCard Charge && DisCharge
                flow = "11";
        if (flow == null)
            if (checkCase(messageType, Constants.CANCELLATION_MSG_TYPE))   //Cancellation
                flow = "11";

        if (flow == null)
            if (checkCase(messageType, Constants.JOINT_CREATE_MSG_TYPE))   //create card 11
                flow = "1";
        if (flow == null)
            if (checkCase(messageType, Constants.JOINT_ACCOUNTINFO_MSG_TYPE))   //AccountInfo
                flow = "7";
        if (flow == null)
            if (checkCase(messageType, Constants.CREATE_CHILD_MSG_TYPE) || checkCase(messageType, Constants.UPDATE_CHILD_MSG_TYPE))   //create card 11 && update card 22
                flow = "9";

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
