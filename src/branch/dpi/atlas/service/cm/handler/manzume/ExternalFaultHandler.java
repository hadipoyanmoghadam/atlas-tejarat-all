package branch.dpi.atlas.service.cm.handler.manzume;

import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.connector.Connector;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * User: F.Heydari
 * Date: OCT 30, 2022
 * Time: 1:54:53 PM
 */
public class ExternalFaultHandler extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(ExternalFaultHandler.class);
    Connector connector;

    public void doProcess(CMMessage msg, Map holder)
    {
        if (log.isInfoEnabled()) log.info("Inside ExternalFaultHandler:process()");
        ManzumeMessage manzumeMsg;

        try {
            manzumeMsg = (ManzumeMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
            String pin=manzumeMsg.getPin().trim();
            String actionCode = msg.getAttributeAsString(Fields.ACTION_CODE);
            if (actionCode == null) {
                actionCode = ActionCode.GENERAL_ERROR;
                log.error("Returned ActionCode is null");
            }
            manzumeMsg.setActionCode(actionCode);
            int msgId = 1;
            if (msg.getAttribute(Fields.MESSAGE_ID) != null)
                msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
            msg.setAttribute(Fields.MESSAGE_ID, msgId + "");

                msg.setAttribute(CMMessage.RESPONSE, manzumeMsg.createResponseHeader());

            ManzumeLogger manzumeLogger = new ManzumeLogger();
            manzumeLogger.process(msg, holder);
            connector.sendAsyncText(msg);

        } catch (Exception e) {
            log.error("Error ::: ExternalFaultHandler  for Branch >>> catching exception 1 ::: " + msg.getAttributeAsString(Fields.MESSAGE_ID) + " -- " + e.getMessage());
            log.debug("msg = " + msg);

            try {
                if (!msg.hasAttribute(CMMessage.RESPONSE)) {
                    log.error("Error ::: ExternalFaultHandler for Branch>>> catching exception 1 ::: There is no response, so it's set ");
                    manzumeMsg= (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
                    manzumeMsg.setActionCode(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
                    msg.setAttribute(CMMessage.RESPONSE, manzumeMsg.createResponseHeader());
                }
                connector.sendAsyncText(msg);
            } catch (Exception e1) {
                log.error("Error ::: ExternalFaultHandler for Branch>>> catching exception 2 ::: Error in sending message :: " + e.getMessage());
            }

        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}
