package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

public class SendTextResponse extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SendTextResponse.class);

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside SendResponse:process()  -- " + this.getClass().getName());
        try {
            CMMessage responseMsg = new CMMessage();
            responseMsg.setAttribute("result", ((CMResultSet) holder.get(CFSConstants.RESULT)).toString());
            if (msg.hasAttribute("JMSReplyTo")) {
                responseMsg.setAttribute("JMSReplyTo", msg.getAttribute("JMSReplyTo"));
                if (log.isDebugEnabled())
                    log.debug("msg.getAttribute(\"JMSReplyTo\")" + msg.getAttribute("JMSReplyTo"));
            }
//            System.out.println("<<<<<<< cfs 2 cm sessionId: " +
//                    msg.getAttributeAsString("sessionId") +
//                    " date/time: " + DateUtil.getSystemDate() + DateUtil.getSystemTime());
            connector.sendAsyncText(responseMsg);
//            if (responseMsg != null)
//                System.out.println("<<<<<<< cfs 2 cm reply " + responseMsg.toString() +
//                        "  date/time: " + dpi.atlas.util.DateUtil.getSystemDate() + dpi.atlas.util.DateUtil.getSystemTime());

        } catch (Exception e) {
            msg.setAttribute(CMMessage.FAULT_CODE, CFSFault.FAULT_AUTH_SERVER);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.SYSTEM_MALFUNCTION));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}