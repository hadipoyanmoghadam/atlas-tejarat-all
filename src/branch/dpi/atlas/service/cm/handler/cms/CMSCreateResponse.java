package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.accountInfoResp.accountInfoResponse;
import branch.dpi.atlas.service.cm.handler.cms.balanceResp.balanceResponse;
import branch.dpi.atlas.service.cm.handler.cms.cancellationResp.cancellationResponse;
import branch.dpi.atlas.service.cm.handler.cms.cardInfo.cardInfoResponse;
import branch.dpi.atlas.service.cm.handler.cms.childCard.childCardResponse;
import branch.dpi.atlas.service.cm.handler.cms.customerResp.customerResponse;
import branch.dpi.atlas.service.cm.handler.cms.giftBalance.giftBalanceResponse;
import branch.dpi.atlas.service.cm.handler.cms.giftChargeResp.giftChargeResponse;
import branch.dpi.atlas.service.cm.handler.cms.giftDeposit.giftDepositResponse;
import branch.dpi.atlas.service.cm.handler.cms.giftDisChargeResp.giftDisChargeResponse;
import branch.dpi.atlas.service.cm.handler.cms.reCreateResp.reCreateResponse;
import branch.dpi.atlas.service.cm.handler.cms.revoke.revokeResponse;
import branch.dpi.atlas.service.cm.handler.cms.touristCard.touristCardResponse;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import branch.dpi.atlas.service.cm.handler.cms.jointAccountInfo.jointAccountInfoResponse;
import branch.dpi.atlas.service.cm.handler.cms.jointCard.jointCardResponse;
import branch.dpi.atlas.service.cm.handler.cms.createBatch.CreateGroupCardResponse;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.*;

import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: Feb 15, 2015
 * Time: 10:04:03 PM
 */
public class CMSCreateResponse extends CMHandlerBase implements Configurable {
    private String action_code;

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        action_code = configuration.get("action-code");
        if ((action_code == null))
            action_code = "";
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside CMSCreateResponse:process()");
        if (!action_code.equals(""))
            msg.setAttribute(Fields.ACTION_CODE, action_code);
        if (msg.getAttributeAsString(Fields.ACTION_MESSAGE) == null)
            msg.setAttribute(Fields.ACTION_MESSAGE, " ");

        try {

            String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);

            if (requestType.equals(Constants.CUSTOMER_ELEMENT)) {
                customerResponse customerResp = new customerResponse();
                customerResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.RE_CREATE_ELEMENT)) {
                reCreateResponse reCreateResp = new reCreateResponse();
                reCreateResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.ACCOUNTINFO_ELEMENT)) {
                accountInfoResponse accountInfoResp = new accountInfoResponse();
                accountInfoResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.CARDINFO_ELEMENT)) {
                cardInfoResponse cardInfoResp = new cardInfoResponse();
                cardInfoResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.REVOKE_ELEMENT)) {
                revokeResponse revokeResponse = new revokeResponse();
                revokeResponse.doProcess(msg, holder);
            } else if (requestType.equals(Constants.BALANCE_ELEMENT)) {
                balanceResponse balResp = new balanceResponse();
                balResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.CHARGE_GIFTCARD_ELEMENT)) {
                giftChargeResponse giftChargeResp = new giftChargeResponse();
                giftChargeResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.DISCHARGE_GIFTCARD_ELEMENT)) {
                giftDisChargeResponse giftDisChargeResp = new giftDisChargeResponse();
                giftDisChargeResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.CANCELLATION_ELEMENT)) {
                cancellationResponse cancellationResp = new cancellationResponse();
                cancellationResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.DEPOSIT_GIFTCARD_ELEMENT)) {
                giftDepositResponse giftDepositResp = new giftDepositResponse();
                giftDepositResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.BALANCE_GIFTCARD_ELEMENT)) {
                giftBalanceResponse giftbalanceResp = new giftBalanceResponse();
                giftbalanceResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.TOURISTCARD_ELEMENT)) {
                touristCardResponse touristCardResp = new touristCardResponse();
                touristCardResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.JOINTACCOUNT_ELEMENT)) {
                jointCardResponse jointCardResp = new jointCardResponse();
                jointCardResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.JOINTACCOUNTINFO_ELEMENT)) {
                jointAccountInfoResponse jointAccountInfoResp = new jointAccountInfoResponse();
                jointAccountInfoResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_CREATE_ELEMENT)) {
                CreatePolicyResponse policyResp = new CreatePolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_UPDATE_ALL_ELEMENT)) {
                UpdateAllPolicyResponse policyResp = new UpdateAllPolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_UPDATE_COLLECTION_ELEMENT)) {
                UpdateCollectionPolicyResponse policyResp = new UpdateCollectionPolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_UPDATE_ELEMENT)) {
                UpdatePolicyResponse policyResp = new UpdatePolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_PRESENTATION_ELEMENT)) {
                PresentationPolicyResponse policyResp = new PresentationPolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_ENDED_PRESENTATION_ELEMENT)) {
                PresentationEndedPolicyResponse policyResp = new PresentationEndedPolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_CREATE_BATCH_ELEMENT)) {
                CreateGroupCardResponse policyResp = new CreateGroupCardResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.CHILDCARD_ELEMENT)) {
                childCardResponse childCardResp = new childCardResponse();
                childCardResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.DCHARGE_ELEMENT)) {
                DChargeResponse dChargeResp = new DChargeResponse();
                dChargeResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.POLICY_ENDED_ELEMENT)) {
                EndedPolicyResponse policyResp = new EndedPolicyResponse();
                policyResp.doProcess(msg, holder);
            } else if (requestType.equals(Constants.IMMEDIATE_CHARGE_ELEMENT)) {
                ImmediateChargeResponse immediateChargeResponse = new ImmediateChargeResponse();
                immediateChargeResponse.doProcess(msg, holder);
            }

            int msgId = 1;
            if (msg.getAttribute(Fields.MESSAGE_ID) != null)
                msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
            msg.setAttribute(Fields.MESSAGE_ID, msgId + "");
            msg.setAttribute(Constants.RESULT,msg.getAttributeAsString(CMMessage.RESPONSE) );
        } catch (Exception e) {
            log.error("ERROR :::Inside CMSCreateResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }
    }
}

