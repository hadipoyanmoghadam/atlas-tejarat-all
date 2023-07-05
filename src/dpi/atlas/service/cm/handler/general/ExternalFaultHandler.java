package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class ExternalFaultHandler extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(ExternalFaultHandler.class);

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {
            msg.setAttribute(CMMessage.RESPONSE_STRING, "0/" + msg.getAttribute(CMMessage.FAULT_CODE) + " : " + "");
            if (msg.getAttributeAsString(Fields.SERVICE_TYPE).equals(CFSConstants.TXN_SRC_PRG))
                connector.sendAsyncText(msg.getAttributesMap());
            else
                connector.sendAsyncText(msg);
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
