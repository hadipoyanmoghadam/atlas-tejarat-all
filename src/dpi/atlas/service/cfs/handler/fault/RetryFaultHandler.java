package dpi.atlas.service.cfs.handler.fault;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

public class RetryFaultHandler extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(RetryFaultHandler.class);

    Connector connector;
    Connector ext_connector;

    public RetryFaultHandler() {
        super();
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        if (log.isDebugEnabled()) log.debug("In Retry Fault Handler");
        String faultCode = (String) msg.getAttribute(CMMessage.FAULT_CODE);
        if (log.isDebugEnabled()) log.debug("faultCode : " + faultCode);

        if (faultCode.equals(CFSFault.FLT_GENERAL_DATA_ERROR_RETRY))
            msg.setAttribute(CFSConstants.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        try {
            ext_connector.sendAsyncText(command.toString());
        } catch (Exception e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(e);
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