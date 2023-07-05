package branch.dpi.atlas.service.cm.handler.credits;

import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Sep 4, 2017
 * Time: 04:17 PM
 */
public class ExternalFaultHandler extends CMHandlerBase implements Configurable {
     private static Log log = LogFactory.getLog(ExternalFaultHandler.class);
    Connector connector;

    public void doProcess(CMMessage msg, Map holder)
    {
      if (log.isInfoEnabled()) log.info("Inside ExternalFaultHandler:process()");
        CreditsMessage creditsMessage;

        try {
            creditsMessage = (CreditsMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
            String actionCode = msg.getAttributeAsString(Fields.ACTION_CODE);
            if (actionCode == null) {
                actionCode = ActionCode.GENERAL_ERROR;
                log.error("Returned ActionCode is null");
            }
            creditsMessage.setActionCode(actionCode);
            int msgId = 1;
            if (msg.getAttribute(Fields.MESSAGE_ID) != null)
                msgId = Integer.parseInt((String) msg.getAttribute(Fields.MESSAGE_ID)) + 1;
            msg.setAttribute(Fields.MESSAGE_ID, msgId + "");
            msg.setAttribute(CMMessage.RESPONSE, creditsMessage.createResponseHeader());
            ServiceLogger srvLogger = new ServiceLogger();
            srvLogger.process(msg, holder);
            connector.sendAsyncText(msg);

        } catch (Exception e) {
            log.error("Error ::: ExternalFaultHandler  for Tourist >>> catching exception 1 ::: " + msg.getAttributeAsString(Fields.MESSAGE_ID) + " -- " + e.getMessage());
            log.debug("msg = " + msg);

            try {
                if (!msg.hasAttribute(CMMessage.RESPONSE)) {
                    log.error("Error ::: ExternalFaultHandler for Tourist>>> catching exception 1 ::: There is no response, so it's set ");
                    creditsMessage= (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
                    creditsMessage.setActionCode(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
                    msg.setAttribute(CMMessage.RESPONSE, creditsMessage.createResponseHeader());
                }
                connector.sendAsyncText(msg);
            } catch (Exception e1) {
                log.error("Error ::: ExternalFaultHandler for Tourist>>> catching exception 2 ::: Error in sending message :: " + e.getMessage());
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
