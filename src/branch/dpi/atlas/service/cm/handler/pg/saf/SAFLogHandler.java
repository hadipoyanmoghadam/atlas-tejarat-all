package branch.dpi.atlas.service.cm.handler.pg.saf;

import dpi.atlas.model.AtlasModel;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.entity.log.SAFLog;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.host.HostAgent;
import dpi.atlas.service.cm.host.HostException;
import dpi.atlas.service.cm.host.HostInterface;
import dpi.atlas.service.cm.host.HostResultSet;
import dpi.atlas.service.cm.host.handlers.HostHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.service.cm.util.CMUtil;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Nasiri .
 * Date: April 3, 2022
 * Time: 04:18 PM
 */
public class SAFLogHandler implements Configurable {
    private static Log log = LogFactory.getLog(SAFLogHandler.class);

    boolean getFromCash;
    HostInterface hostInterface;
    private Connector connector;


    public String handleCFS(SAFLog safLog) throws Exception {

        try {
            CMCommand command = new CMCommand(safLog.getMsgString());

            Batch batch = ChannelFacadeNew.getBatch(new Integer(Constants.ACTIVE_BATCH), Constants.OPERATOR_AND, getFromCash);
            command.addParam(CFSConstants.CURRENT_BATCH, batch.getBatchPk().toString());

            String strReply = (String) connector.sendSyncText(command);

            if (strReply == null) {
                log.error("Reply :: null (TimeOut) , command = " + command.toString());
                return ActionCode.TIME_OUT;
            }

            // **** reply != null ******

            CMMessage msg = new CMMessage();

            CMResultSet result = new CMResultSet(strReply);
            msg.setAttribute(CMMessage.RESPONSE, result);
            CMCommand command1 = new CMCommand(result.getRequest());
            msg.setAttribute(CMMessage.REQUEST, command1);
            String action_code = result.getHeaderField(Fields.ACTION_CODE);


            if (action_code == null) {
                return ActionCode.TIME_OUT;
            }

            if (log.isDebugEnabled())
                log.debug("************   action_code = " + action_code);

            if (action_code.startsWith("00")) {
                msg.setAttribute(Constants.SRC_HOST_ID, Constants.HOST_ID_CFS);
                String destAccount = result.getHeaderField(Fields.DEST_ACCOUNT);

                if (destAccount != null && !destAccount.equals(""))
                    msg.setAttribute(Fields.DEST_ACCOUNT, result.getHeaderField(Fields.DEST_ACCOUNT));
                else
                    msg.setAttribute(Fields.DEST_ACCOUNT, ISOUtil.zeropad("0", 13));
                msg.setAttribute(CFSConstants.CURRENT_BATCH, result.getHeaderField(CFSConstants.CURRENT_BATCH));
                return action_code;
            } else {
                return action_code;
            }
        } catch (NotFoundException e) {
            log.error("BatchPk= NOT FOUND!!!!!! ");
            return ActionCode.ACTIVE_BATCH_NOT_FOUND;
        } catch (Exception e) {
            log.error(e);
            return  ActionCode.TIME_OUT;
        }

    }

    public String handleFaragir(CMMessage msg) throws CMFault {
        initInputParams(msg);
        String beanNameExtractor = msg.getAttributeAsString(Fields.PIN);
        String beanName = "tj.host.post.branch." + beanNameExtractor;
        HostResultSet hrs;
        String actionCode;
        try {
            HostHandler host = (HostHandler) AtlasModel.getInstance().getBean(beanName);
            Map holder = new HashMap();
            holder.put("hostInterface", hostInterface);
            hrs = host.post(msg, holder, hostInterface);

        } catch (CMFault e) {
            int errorCode = Integer.parseInt(e.getMessage());
            actionCode = getActionCode(errorCode);
            hrs = new HostResultSet();
            hrs.setDataHeaderField(Fields.ACTION_CODE, actionCode);

        } catch (Exception ef) {
            log.error(ef);
            hrs = new HostResultSet();
            hrs.setDataHeaderField(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
        }


        actionCode = (hrs.getDataHeaderField(Fields.ACTION_CODE) != null ? hrs.getDataHeaderField(Fields.ACTION_CODE) : ActionCode.TIME_OUT);


        if (!actionCode.startsWith("00")) {
            CMResultSet result = new CMResultSet();
            log.debug(actionCode);
            result.setHeaderField(Params.ACTION_CODE, actionCode);
            result.setHeaderField(Fields.ACTION_MESSAGE, actionCode);
            msg.setAttribute(CMMessage.RESPONSE_STRING, result.toString());
            msg.setAttribute(Fields.ACTION_CODE, actionCode);
        } else {

            CMResultSet rs = new CMResultSet(hrs);
            rs.setHeaderField(Fields.ACTION_CODE, hrs.getDataHeaderField(Fields.ACTION_CODE));
            rs.setHeaderField(Fields.ACTION_MESSAGE, ActionCodeMsg.APPROVED);
            msg.setAttribute(Fields.RESPONSE, rs);
            msg.setAttribute(Fields.RESPONSE_STRING, hrs);
            msg.setAttribute(Fields.ACTION_CODE, actionCode);
            msg.setAttribute(Constants.SRC_HOST_ID, Constants.HOST_ID_FARAGIR);
            String destAccount = rs.getHeaderField(Fields.DEST_ACCOUNT);
            if (destAccount != null && !destAccount.equals(""))
                msg.setAttribute(Fields.DEST_ACCOUNT, rs.getHeaderField(Fields.DEST_ACCOUNT));
            else
                msg.setAttribute(Fields.DEST_ACCOUNT, "0000000000000");
        }
        return actionCode;

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

    public String getActionCode(int errorCode) throws CMFault {
        String actionCode = ActionCode.TIME_OUT;

        if (errorCode == HostException.CONNECTION_ERROR)
            actionCode = ActionCode.GENERAL_DATA_ERROR;
        else if (errorCode == HostException.EMPTY_RESULTSET)
            actionCode = ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED;
         else if (errorCode == HostException.UNKNOWN_ERROR)
            actionCode = ActionCode.SYSTEM_NOT_AVAILABLE;
        else if (errorCode == HostException.CONNECTION_TIMEOUT)
            actionCode = ActionCode.TIME_OUT;
        else if (errorCode == HostException.ACCOUNT_MISSING)
            actionCode = ActionCode.ACCOUNT_HAS_NO_CUST_INFO;

        return actionCode;

    }

    public String getActionMessage(String actionCode) throws CMFault {
        String actionMsg = ActionCodeMsg.CONNECTION_TIMEOUT;

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


        String host_agent_name = cfg.get("host-agent");
        HostAgent hostAgent = ChannelManagerEngine.getInstance().getHostAgent(host_agent_name);
        hostInterface = hostAgent.getHostInterface();

        String connector_name = cfg.get("cfs-connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);

        String getBatchFromCash = cfg.get("getBatchFromCash");
        getFromCash = getBatchFromCash.equals("yes");

    }

}
