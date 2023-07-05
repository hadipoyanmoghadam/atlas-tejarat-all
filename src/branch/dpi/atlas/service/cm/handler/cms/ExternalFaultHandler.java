package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.accountInfoResp.accountInfoResponse;
import branch.dpi.atlas.service.cm.handler.cms.balanceResp.balanceResponse;
import branch.dpi.atlas.service.cm.handler.cms.cancellationResp.cancellationResponse;
import branch.dpi.atlas.service.cm.handler.cms.cardInfo.cardInfoResponse;
import branch.dpi.atlas.service.cm.handler.cms.childCard.childCardResponse;
import branch.dpi.atlas.service.cm.handler.cms.customerResp.customerResponse;
import branch.dpi.atlas.service.cm.handler.cms.giftChargeResp.giftChargeResponse;
import branch.dpi.atlas.service.cm.handler.cms.giftDeposit.giftDepositResponse;
import branch.dpi.atlas.service.cm.handler.cms.giftDisChargeResp.giftDisChargeResponse;
import branch.dpi.atlas.service.cm.handler.cms.reCreateResp.reCreateResponse;
import branch.dpi.atlas.service.cm.handler.cms.revoke.revokeResponse;
import branch.dpi.atlas.service.cm.handler.cms.touristCard.touristCardResponse;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import java.util.Map;
import branch.dpi.atlas.service.cm.handler.cms.jointAccountInfo.jointAccountInfoResponse;
import branch.dpi.atlas.service.cm.handler.cms.jointCard.jointCardResponse;
import branch.dpi.atlas.service.cm.handler.cms.createBatch.CreateGroupCardResponse;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.*;

/**
 * User: SH.Behnaz
 * Date: Feb 10, 2015
 * Time: 3:54:53 PM
 */
public class ExternalFaultHandler extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(ExternalFaultHandler.class);
    Connector connector;

    public void doProcess(CMMessage msg, Map holder) {
        if (log.isInfoEnabled()) log.info("Inside ExternalFaultHandler:process()");

        try {

                String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);
                String actionMsg = msg.getAttributeAsString(Fields.ACTION_MESSAGE);
                String actionCode = msg.getAttributeAsString(Fields.ACTION_CODE);
                if (actionMsg == null)
                    msg.setAttribute(Fields.ACTION_MESSAGE, " ");
                if (actionCode == null) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
                    msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.GENERAL_ERROR);
                    log.error("Returned ActionCode is null");
                }

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
                    cardInfoResp.doProcess(msg,holder);
                } else if (requestType.equals(Constants.REVOKE_ELEMENT)) {
                    revokeResponse revokeResponse = new revokeResponse();
                    revokeResponse.doProcess(msg, holder);
                } else if (requestType.equals(Constants.BALANCE_ELEMENT)) {
                    balanceResponse balResp = new balanceResponse();
                    balResp.doProcess(msg,holder);
                } else if (requestType.equals(Constants.CHARGE_GIFTCARD_ELEMENT)) {
                    giftChargeResponse giftChargeResp = new giftChargeResponse();
                    giftChargeResp.doProcess(msg,holder);
                } else if (requestType.equals(Constants.DISCHARGE_GIFTCARD_ELEMENT)) {
                    giftDisChargeResponse giftDisChargeResp = new giftDisChargeResponse();
                    giftDisChargeResp.doProcess(msg,holder);
                } else if (requestType.equals(Constants.CANCELLATION_ELEMENT)) {
                    cancellationResponse cancellationResp = new cancellationResponse();
                    cancellationResp.doProcess(msg, holder);
                } else if (requestType.equals(Constants.DEPOSIT_GIFTCARD_ELEMENT)) {
                    giftDepositResponse giftDepositResp = new giftDepositResponse();
                    giftDepositResp.doProcess(msg, holder);
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
                } else
                    msg.setAttribute(CMMessage.RESPONSE, GenerateErrorXML(ActionCode.FORMAT_ERROR, "Invalid format of Request "));


            int msgId = 1;
            if (msg.getAttribute(Fields.MESSAGE_ID) != null)
                msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
            msg.setAttribute(Fields.MESSAGE_ID, msgId + "");

            CMSLogger cmsLogger = new CMSLogger();
            cmsLogger.process(msg, holder);
            connector.sendAsyncText(msg);

        } catch (Exception e) {
            log.error("Error ::: ExternalFaultHandler  for CMS >>> catching exception 1 ::: " + msg.getAttributeAsString(Fields.MESSAGE_ID) + " -- " + e.getMessage());
            log.debug("msg = " + msg);

            try {
                if (!msg.hasAttribute(CMMessage.RESPONSE)) {
                    log.error("Error ::: ExternalFaultHandler for CMS>>> catching exception 1 ::: There is no response, so it's set ");
                    msg.setAttribute(CMMessage.RESPONSE, GenerateErrorXML(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE, "Channel can not create response!!!"));
                }
                connector.sendAsyncText(msg);
            } catch (Exception e1) {
                log.error("Error ::: ExternalFaultHandler for CMS>>> catching exception 2 ::: Error in sending message :: " + e.getMessage());
            }

        }
    }

    private static String GenerateErrorXML(String actionCodeStr, String desc_str) throws Exception {
        String xmlstr = "";
        Element root = new Element("root");
        Document doc = new Document(root);
        String comment = " Generated: " + DateUtil.getSystemDate() + " " + DateUtil.getSystemTime();
        doc.getContent().add(0, new Comment(comment));

        Element actionCode = new Element("ACTIONCODE");
        actionCode.setText(actionCodeStr);
        root.addContent(actionCode);
        Element desc = new Element("DESC");
        desc.setText(desc_str);
        root.addContent(desc);

        XMLOutputter out = new XMLOutputter();

        xmlstr = out.outputString(doc);
        return xmlstr;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}
