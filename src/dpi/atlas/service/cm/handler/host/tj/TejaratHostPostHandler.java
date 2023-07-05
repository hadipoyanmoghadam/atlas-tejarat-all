package dpi.atlas.service.cm.handler.host.tj;

import dpi.atlas.model.AtlasModel;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.host.*;
import dpi.atlas.service.cm.host.handlers.HostHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * TejaratHostPostHandler class
 * with Ahamdi
 *
 * @version $Revision: 1.15 $ $Date: 2007/10/29 14:04:29 $
 */
public class TejaratHostPostHandler extends TJServiceHandler implements Configurable {
    private HostInterface hostInterface;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        String actionMsgStr = "ACTION-MSG";
        CMCommand command = null;
        String commandName = null;
        HostResultSet hrs;
        String actionCode;
        String actionMsg = "APPROVED";
        String service = msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
        try {
            command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            commandName = command.getCommandName();
            String beanNameExtractor = (String) msg.getAttribute(Fields.COMMAND);

            if (commandName.equals(TJCommand.RTGS_FUND_TRANSFER_CMD) && msg.getAttribute(Constants.SRC_HOST_ID).equals(Constants.CFS_HOSTID)) {
                if (command.getParam("PRE-COMMAND-NAME").equals(TJCommand.IS_SATNA_OPEN_CMD))
                    beanNameExtractor = TJCommand.IS_SATNA_OPEN_CMD;
                else if ((msg.getAttribute(CMMessage.RESPONSE)) instanceof CMResultSet) {
                    String cfsActionCode = ((CMResultSet) msg.getAttribute(CMMessage.RESPONSE)).getHeaderField("action_code");
                    if (!cfsActionCode.equals(ActionCode.APPROVED)) {
                        actionMsg = "NOT-APPROVED";
                        CMResultSet result = new CMResultSet();
                        msg.setAttribute(Fields.APPROVED, Constants.NOTAUTHORISED);
                        msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
                        actionCode = cfsActionCode;
                        result.setHeaderField(Params.ACTION_CODE, actionCode);
                        result.setHeaderField(Fields.ACTION_MESSAGE, actionCode);
                        msg.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
                        msg.setAttribute(Fields.RESPONSE, result);
                        msg.setAttribute(Fields.ACTION_CODE, actionCode);
                        return;
                    }
                }
            }
            String beanName = "tj.host.post." + beanNameExtractor.toUpperCase();
            HostHandler host = (HostHandler) AtlasModel.getInstance().getBean(beanName);
            hrs = host.post(msg, holder, hostInterface);
            if (msg.hasAttribute(CMMessage.SERVICE_TYPE) && msg.getAttributeAsString(CMMessage.SERVICE_TYPE).equalsIgnoreCase(Constants.ISO_SERVICE)) {
                msg.setAttribute(Fields.MESSAGE_ID, (Integer.valueOf(msg.getAttributeAsString(Fields.MESSAGE_ID)) + 1) + "");
            }
            log.debug("Pass the host");
        } catch (CMFault e) {
            int errorCode = Integer.parseInt(e.getMessage());
            CMResultSet result = new CMResultSet();
            msg.setAttribute(Fields.APPROVED, Constants.NOTAUTHORISED);
            msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
            actionCode = getActionCode(errorCode, msg.getAttributeAsString(Fields.COMMAND_NAME));
            result.setHeaderField(Params.ACTION_CODE, actionCode);
            result.setHeaderField(Fields.ACTION_MESSAGE, getActionMessage(actionCode));
            msg.setAttribute(Params.ACTION_CODE, actionCode);
            if (!commandName.equals("FT") && !commandName.equals("BLPY") && !commandName.equals("FCSH") && !commandName.equals("CSHW"))
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(actionCode));

            msg.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
            hrs = new HostResultSet();
            hrs.setDataHeaderField(Fields.ACTION_CODE, result.getHeaderField(Params.ACTION_CODE));
        } catch (Exception ef) {
            log.error(ef);
            hrs = new HostResultSet();
            hrs.setDataHeaderField(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
        }
        actionCode = hrs.getDataHeaderField(Fields.ACTION_CODE);

        if (!actionCode.startsWith("00") && !(msg.getAttributeAsString(Fields.COMMAND).startsWith(TJCommand.CMD_NETWORK_FUND_TRANSFER_TEJ2TEJ) && actionCode.equals(ActionCode.REVERSE_HAS_NO_ORIGINAL))) {
            actionMsg = "NOT-APPROVED";
            CMResultSet result = new CMResultSet();
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
        msg.setAttribute(actionMsgStr, actionMsg);
    }

    public String getActionCode(int errorCode, String commandName) throws CMFault {
        String actionCode = ActionCode.GENERAL_ERROR;

        if (errorCode == HostException.CONNECTION_ERROR)
            actionCode = ActionCode.GENERAL_DATA_ERROR;
        else if (errorCode == HostException.EMPTY_RESULTSET) {
            if (commandName.equals(Constants.CHEQUE_STATUS_SHORT_NAME))
                actionCode = ActionCode.CHEQUE_NUMBER_NOT_FOUND;
            else if (commandName.equals(Constants.CURRENCY_RATE_SHORT_NAME))
                actionCode = ActionCode.INVALID_CURERNCY_CODE;
            else
                actionCode = ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED;

        } else if (errorCode == HostException.UNKNOWN_ERROR)
            actionCode = ActionCode.SYSTEM_NOT_AVAILABLE;
        else if (errorCode == HostException.CONNECTION_TIMEOUT)
            actionCode = ActionCode.TIME_OUT;
        else if (errorCode == HostException.ACCOUNT_MISSING)
            actionCode = ActionCode.ACCOUNT_HAS_NO_CUST_INFO;

        return actionCode;

    }

    public String getActionMessage(String actionCode) throws CMFault {
        String actionMsg = ActionCodeMsg.GENERAL_ERROR;

        if (actionCode.equals(ActionCode.GENERAL_DATA_ERROR))
            actionMsg = ActionCodeMsg.GENERAL_DATA_ERROR;
        else if (actionCode.equals(ActionCode.CHEQUE_NUMBER_NOT_FOUND))
            actionMsg = ActionCodeMsg.INVALID_CHEQUE_NO;
        else if (actionCode.equals(ActionCode.INVALID_CURERNCY_CODE))
            actionMsg = ActionCodeMsg.INVALID_CURERNCY_CODE;

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

