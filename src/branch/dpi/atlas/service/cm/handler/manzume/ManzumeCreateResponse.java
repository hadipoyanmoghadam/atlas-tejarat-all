package branch.dpi.atlas.service.cm.handler.manzume;


import branch.dpi.atlas.service.cm.imf.manzume.ManzumeToIMFFormater;
import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.model.AtlasModel;

import java.util.Map;

import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.core.Configurable;

/**
 * User: F.Heydari
 * Date: OCT 31, 2022
 * Time: 09:22:21 AM
 */
public class ManzumeCreateResponse extends CMHandlerBase implements Configurable {

    private String action_code;

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        action_code = configuration.get("action-code");
        if ((action_code == null))
            action_code = "";
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside ManzumeCreateResponse:process()");
        if (!action_code.equals(""))
            msg.setAttribute(Fields.ACTION_CODE, action_code);
        String pin = msg.getAttributeAsString(Fields.PIN);
        String beanName = "convertor" + pin;
        ManzumeToIMFFormater convertor = (ManzumeToIMFFormater) AtlasModel.getInstance().getBean(beanName);
        String responseStr;
        ManzumeMessage manzumeMsg = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        manzumeMsg.setActionCode(msg.getAttributeAsString(Fields.ACTION_CODE));
        if (!manzumeMsg.getActionCode().equals(ActionCode.APPROVED))
            responseStr = manzumeMsg.createResponseHeader();
        else
            responseStr = convertor.createResponse(msg, holder);
        int msgId = 1;
        if (msg.getAttribute(Fields.MESSAGE_ID) != null)
            msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
        msg.setAttribute(Fields.MESSAGE_ID, msgId + "");
        msg.setAttribute(CMMessage.RESPONSE, responseStr);
    }
}
