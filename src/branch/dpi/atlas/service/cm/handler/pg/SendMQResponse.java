package branch.dpi.atlas.service.cm.handler.pg;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

public class SendMQResponse extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SendMQResponse.class);

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            log.debug("result:" + msg.getAttribute(Constants.RESULT));
            CMMessage responseMsg = new CMMessage();

          responseMsg.setAttribute("result", msg.getAttribute(Constants.RESULT));

            log.debug("msg.hasAttribute(\"JMSReplyTo\") = " + msg.hasAttribute("JMSReplyTo"));
            if (msg.hasAttribute("JMSReplyTo")) {
                responseMsg.setAttribute("JMSReplyTo", msg.getAttribute("JMSReplyTo"));
                if (log.isDebugEnabled())
                    log.debug("msg.getAttribute(\"JMSReplyTo\")" + msg.getAttribute("JMSReplyTo"));
            }
            if (msg.hasAttribute("JMSCorrelationID")) {
                responseMsg.setAttribute("JMSCorrelationID", msg.getAttribute("JMSCorrelationID"));

                log.debug("msg.getAttribute(\"JMSCorrelationID\")" + msg.getAttribute("JMSCorrelationID"));
            }
            connector.sendAsync(responseMsg);

        } catch (Exception e) {
            log.error("Exception !!! - MESSAGE_SEQUENCE_FOR_tblog = " + msg.getAttributeAsString(Constants.MESSAGE_SEQUENCE_FOR_tblog) + " -- e :: " + e.getMessage());
            e.printStackTrace();
            throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.SYSTEM_MALFUNCTION));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}
