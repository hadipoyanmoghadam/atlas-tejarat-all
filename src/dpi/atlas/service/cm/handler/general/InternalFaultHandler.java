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

/**
 * InternalFaultHandler class
 *
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.5 $ $Date: 2007/10/29 14:04:23 $
 */
public class InternalFaultHandler extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(InternalFaultHandler.class);

    Connector connector;
    Connector ext_connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {
            msg.setAttribute(CMMessage.RESPONSE_STRING, "0/Internal Error");
            if (msg.getAttributeAsString(Fields.SERVICE_TYPE).equals(CFSConstants.TXN_SRC_PRG))
                ext_connector.sendAsyncText(msg.getAttributesMap());
            else
                ext_connector.sendAsyncText(msg);
        } catch (Exception e) {
            log.error(e);
            throw new CMFault(e);
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);


        String ext_connector_name = cfg.get("external-connector");
        ext_connector = ChannelManagerEngine.getInstance().getConnector(ext_connector_name);
        if (ext_connector == null)
            throw new ConfigurationException("Cannot find external connector " + ext_connector_name);
    }
}