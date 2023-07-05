package branch.dpi.atlas.service.cm.handler.cms;

import dpi.atlas.model.AtlasModel;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.host.HostAgent;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.handlers.HostHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.HashMap;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: Feb 23, 2015
 * Time: 14:04:03 PM
 */
public class SendToFaragir extends TJServiceHandler implements Configurable {
    private HostInterface hostInterface;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        HostResultSet hrs;
        String actionCode;
        String beanName;
        try {
            initInputParams(msg);
            String beanNameExtractor;
            if (msg.getAttributeAsString(CMMessage.SERVICE_TYPE).equals(Fields.SERVICE_CMS)) {
                beanNameExtractor = msg.getAttributeAsString(Fields.MESSAGE_TYPE);
                beanName = "tj.host.post.cms." + beanNameExtractor;
            } else {
                beanNameExtractor = msg.getAttributeAsString(Fields.PIN);
                beanName = "tj.host.post.branch." + beanNameExtractor;
            }
            HostHandler host = (HostHandler) AtlasModel.getInstance().getBean(beanName);
            hrs = host.post(msg, holder, hostInterface);
            if (hrs == null) {
                hrs = new HostResultSet();
                hrs.setDataHeaderField(Fields.ACTION_CODE, ActionCode.TRANSACTION_NOT_FOUND);
            }
        } catch (CMFault e) {
            CMResultSet result = new CMResultSet();
            actionCode = getActionCode(Integer.parseInt(e.getMessage()));
            result.setHeaderField(Params.ACTION_CODE, actionCode);
            result.setHeaderField(Fields.ACTION_MESSAGE, getActionMessage(actionCode));
            msg.setAttribute(Params.ACTION_CODE, actionCode);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(actionCode));
        } catch (Exception ef) {
            log.error(ef);
            hrs = new HostResultSet();
            hrs.setDataHeaderField(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
        }
        actionCode = hrs.getDataHeaderField(Fields.ACTION_CODE);

        if (!actionCode.startsWith("00")) {
            CMResultSet result = new CMResultSet();
            result.setHeaderField(Params.ACTION_CODE, actionCode);
            result.setHeaderField(Fields.ACTION_MESSAGE, actionCode);
            msg.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
            msg.setAttribute(Fields.RESPONSE, result);
            msg.setAttribute(Fields.ACTION_CODE, actionCode);

        } else {

            CMResultSet rs = new CMResultSet(hrs);
            rs.setHeaderField(Fields.ACTION_CODE, hrs.getDataHeaderField(Fields.ACTION_CODE));
            rs.setHeaderField(Fields.ACTION_MESSAGE, ActionCodeMsg.APPROVED);
            msg.setAttribute(Fields.APPROVED, Constants.AUTHORISED);
            msg.setAttribute(Fields.RESPONSE, rs);
            msg.setAttribute(Fields.RESPONSE_STRING, hrs);
        }

    }

    private void initInputParams(CMMessage msg) {
        HashMap inputParams = new HashMap();
        String sessionID = msg.getAttributeAsString(Fields.SESSION_ID);
        if (sessionID != null && !"".equals(sessionID.trim()))
            inputParams.put(Constants.SESSION_ID, sessionID);
        else
            inputParams.put(Constants.SESSION_ID, "000000000000");

        inputParams.put(Constants.SERVICE, msg.getAttribute(CMMessage.SERVICE_TYPE));
        inputParams.put(Constants.CUSTOMER_ID, "");
        inputParams.put(Constants.CUSTOMER_PASS, "");
        inputParams.put(Constants.CLIENT_ID, "");

        msg.setAttribute(Constants.INPUT_PARAMS, inputParams);
    }

    private String getActionCode(int errorCode) throws CMFault {
        String actionCode = ActionCode.GENERAL_ERROR;

        if (errorCode == HostException.CONNECTION_ERROR)
            actionCode = ActionCode.GENERAL_DATA_ERROR;
        else if (errorCode == HostException.EMPTY_RESULTSET) {
            actionCode = ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED;
        } else if (errorCode == HostException.UNKNOWN_ERROR)
            actionCode = ActionCode.SYSTEM_NOT_AVAILABLE;
        else if (errorCode == HostException.CONNECTION_TIMEOUT)
            actionCode = ActionCode.TIME_OUT;
        else if (errorCode == HostException.ACCOUNT_MISSING)
            actionCode = ActionCode.ACCOUNT_HAS_NO_CUST_INFO;

        return actionCode;

    }

    private String getActionMessage(String actionCode) throws CMFault {
        String actionMsg = ActionCodeMsg.GENERAL_ERROR;

        if (actionCode.equals(ActionCode.GENERAL_DATA_ERROR))
            actionMsg = ActionCodeMsg.GENERAL_DATA_ERROR;
        else if (actionCode.equals(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED))
            actionMsg = ActionCodeMsg.ACCOUNT_HAS_NO_CUST_INFO;
        else if (actionCode.equals(ActionCode.SYSTEM_NOT_AVAILABLE))
            actionMsg = "No Response from Host  whithin timeout";
        else if (actionCode.equals(ActionCode.TIME_OUT))
            actionMsg = ActionCodeMsg.CONNECTION_TIMEOUT;
        else if (actionCode.equals(ActionCode.ACCOUNT_HAS_NO_CUST_INFO))
            actionMsg = ActionCodeMsg.ACCOUNT_HAS_NO_CUST_INFO;

        return actionMsg;
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

