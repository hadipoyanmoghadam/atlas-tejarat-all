package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

public class SendMessage extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SendMessage.class);

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside SendMessage:process()  -- " + this.getClass().getName());
        try {
            CMCommand cmCommand = (CMCommand) holder.get(CMMessage.REQUEST);
            connector.sendAsyncText(cmCommand.toString());
        } catch (Exception e) {
            throw new CMFault(e);
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}