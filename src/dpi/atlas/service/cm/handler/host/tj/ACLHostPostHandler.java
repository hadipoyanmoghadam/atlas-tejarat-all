package dpi.atlas.service.cm.handler.host.tj;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostAgent;
import dpi.atlas.service.cm.host.handlers.HostHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.model.AtlasModel;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

public class ACLHostPostHandler extends TJServiceHandler implements Configurable {
    private HostInterface hostInterface;
    public static final String handlertName = "SparrowLoggerNew";

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        CMCommand command = null;
        HostResultSet hrs = null;
        try {
            command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            String beanNameExtractor = (String) msg.getAttribute(Fields.COMMAND);
            String beanName = "tj.host.post." + beanNameExtractor.toUpperCase();
            HostHandler host = (HostHandler) AtlasModel.getInstance().getBean(beanName);
            hrs = host.post(msg, holder, hostInterface);
            if (log.isDebugEnabled()) log.debug("Pass the host");
        } catch (CMFault e) {
            CMResultSet result = new CMResultSet();
            msg.setAttribute(Fields.APPROVED, Constants.NOTAUTHORISED);
            msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
            result.setHeaderField(Params.ACTION_CODE, ActionCode.UNABLE_TO_GET_ACCOUNT_LIST_FROM_FARAGIR);
            result.setHeaderField(Fields.ACTION_MESSAGE, ActionCodeMsg.GENERAL_ERROR);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNABLE_TO_GET_ACCOUNT_LIST_FROM_FARAGIR);
            msg.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
            hrs = new HostResultSet();
            hrs.setDataHeaderField(Fields.ACTION_CODE, result.getHeaderField(Params.ACTION_CODE));
        }  catch (Exception ef) {
            hrs = new HostResultSet();
            hrs.setDataHeaderField(Fields.ACTION_CODE, ActionCode.UNABLE_TO_GET_ACCOUNT_LIST_FROM_FARAGIR);
        }

        String actionCode = hrs.getDataHeaderField(Fields.ACTION_CODE);
        if (actionCode.charAt(1) != '0') {
            CMResultSet result = new CMResultSet();
            log.error(actionCode);
            msg.setAttribute(Fields.APPROVED, Constants.NOTAUTHORISED);
            msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
            result.setHeaderField(Params.ACTION_CODE, actionCode);
            result.setHeaderField(Fields.ACTION_MESSAGE, actionCode);
            msg.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
            msg.setAttribute(Fields.RESPONSE, result);
            msg.setAttribute(Fields.ACTION_CODE, actionCode);
        } else {
            CMResultSet rs = new CMResultSet(hrs);
            //TODO It seems one of 'rs.setHeaderField(Fields.ACTION_CODE)' or 'rs.setHeaderField(Params.ACTION_CODE)' should be used
            rs.setHeaderField(Fields.ACTION_CODE, hrs.getDataHeaderField(Fields.ACTION_CODE));
            rs.setHeaderField(Fields.ACTION_MESSAGE, ActionCodeMsg.APPROVED);
            rs.setRequest(command.toString());
            msg.setAttribute(Fields.APPROVED, Constants.AUTHORISED);
            msg.setAttribute(Fields.RESPONSE, rs);
            msg.setAttribute(Fields.RESPONSE_STRING, hrs);
        }
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
