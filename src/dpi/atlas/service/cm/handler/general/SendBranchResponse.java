package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

public class SendBranchResponse extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SendBranchResponse.class);

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            CMMessage responseMsg = new CMMessage();
            //todo: change response later
            responseMsg.setAttribute(CMMessage.RESPONSE_STRING, msg.getAttribute(CMMessage.RESPONSE_STRING));
//            System.out.println("response message is:" + responseMsg);
            connector.sendAsyncByte(responseMsg);

        } catch (Exception e) {
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.SYSTEM_MALFUNCTION));        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}
