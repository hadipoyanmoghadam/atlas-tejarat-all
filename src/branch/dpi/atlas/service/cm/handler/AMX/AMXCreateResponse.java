package branch.dpi.atlas.service.cm.handler.AMX;

import branch.dpi.atlas.service.cm.imf.amx.AmxToIMFFormater;
import branch.dpi.atlas.service.cm.imf.tourist.TouristToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import dpi.atlas.model.AtlasModel;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;


/**
 * User: R.Nasiri
 * Date: Dec 5, 2018
 * Time: 12:00 PM
 */
public class AMXCreateResponse extends CMHandlerBase implements Configurable {

    private String action_code;

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        action_code = configuration.get("action-code");
        if ((action_code == null))
            action_code = "";
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside AMXCreateResponse:process()");
        if (!action_code.equals(""))
            msg.setAttribute(Fields.ACTION_CODE, action_code);
        String pin = msg.getAttributeAsString(Fields.PIN);
        String beanName = "convertor" + pin;
        AmxToIMFFormater convertor = (AmxToIMFFormater) AtlasModel.getInstance().getBean(beanName);
        String responseStr;
        AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        amxMessage.setActionCode(msg.getAttributeAsString(Fields.ACTION_CODE));
        if (!amxMessage.getActionCode().equals(ActionCode.APPROVED)) {

            if (amxMessage.getPin().equalsIgnoreCase(Constants.PIN_CHANGE_ACCOUNT_STATUS_AMX)) {
                if (amxMessage.getActionCode() != null &&
                        (amxMessage.getActionCode().equalsIgnoreCase(ActionCode.ACCOUNT_DISABLED_BEFORE) || amxMessage.getActionCode().equalsIgnoreCase(ActionCode.ACCOUNT_HAS_BALANCE) ||
                                amxMessage.getActionCode().equalsIgnoreCase(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL) || amxMessage.getActionCode().equalsIgnoreCase(ActionCode.ACCOUNT_IS_E_BLOCKED) ||
                                amxMessage.getActionCode().equalsIgnoreCase(ActionCode.ACCOUNT_HAS_BLOCKED_AMOUNT) || amxMessage.getActionCode().equalsIgnoreCase(ActionCode.ACCOUNT_BAD_STATUS) ||
                                amxMessage.getActionCode().equalsIgnoreCase(ActionCode.DESTINATION_ACCOUNT_NOT_PERMITTED_TO_BE_DEPOSIT))) {
                    amxMessage.setActionCode(ActionCode.INVALID_OPERATION);
                } else if (amxMessage.getActionCode() != null &&
                        (amxMessage.getActionCode().equalsIgnoreCase(ActionCode.CUSTOMER_NOT_FOUND) || amxMessage.getActionCode().equalsIgnoreCase(ActionCode.ROW_NOT_FOUND))) {
                    amxMessage.setActionCode(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
                }
            }

            responseStr = amxMessage.createResponseHeader();
        } else
            responseStr = convertor.createResponse(msg, holder);
        int msgId = 1;
        if (msg.getAttribute(Fields.MESSAGE_ID) != null)
            msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
        msg.setAttribute(Fields.MESSAGE_ID, msgId + "");
        msg.setAttribute(CMMessage.RESPONSE, responseStr);
    }
}
