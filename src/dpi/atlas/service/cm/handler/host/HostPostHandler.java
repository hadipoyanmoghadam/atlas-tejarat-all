package dpi.atlas.service.cm.handler.host;

import dpi.atlas.model.AtlasModel;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.host.HostAgent;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.handlers.HostHandler;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * GeneralAuthenticator class
 *
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:30 $
 */
public class HostPostHandler extends TJServiceHandler {
    private HostInterface hostInterface;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        String beanName = "host.post." + ((String) msg.getAttribute(Fields.COMMAND)).toUpperCase();

        HostResultSet hrs = null;
        try {
            HostHandler host = (HostHandler) AtlasModel.getInstance().getBean(beanName);
            hrs = host.post(msg, holder, hostInterface);
        } catch (Exception e) {
            log.error(e);
            throw new CMFault(e);
        }
        CMResultSet rs = new CMResultSet(hrs);
        rs.setHeaderField(Fields.ACTION_CODE, ActionCode.APPROVED);
        rs.setHeaderField(Fields.ACTION_MESSAGE, ActionCodeMsg.APPROVED);
        msg.setAttribute(Fields.RESPONSE, rs);
        //TODO: check this later

        msg.setAttribute(Fields.RESPONSE_STRING, hrs);

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        String host_agent_name = cfg.get("host-agent");
        HostAgent hostAgent = getChannelManagerEngine().getHostAgent(host_agent_name);
        if (hostAgent == null) {
            ConfigurationException ce = new ConfigurationException("No Host Agent found referred by 'host-agent' property, name:" + host_agent_name);
            log.error(ce);
            throw ce;
        }
        hostInterface = hostAgent.getHostInterface();
    }

}
