package dpi.atlas.service.cfs.handler.fault;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSLoggerNew;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

public class InternalCFSFaultHandler extends CFSFaultHandler {
    private static Log log = LogFactory.getLog(InternalCFSFaultHandler.class);

    Connector ext_connector;

    public InternalCFSFaultHandler() {
        super();
    }

    public void process(Object obj, Map holder) throws CFSFault {
        CMMessage msg = ((CMMessage) obj);

        msg.setAttribute(CFSConstants.ACTION_CODE, getActionCode(msg, holder));

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        CMResultSet result = new CMResultSet();
        result.setHeaderField(Params.ACTION_CODE, (String) msg.getAttribute(CFSConstants.ACTION_CODE));
        command.getParams().put(CFSConstants.ACTION_CODE, (String) msg.getAttribute(CFSConstants.ACTION_CODE));
        command.getParams().put("JMSCorrelationID", command.getHeaderParam(Fields.SESSION_ID));
        result.setRequest(command.toString());

        int msg_id = (new Integer(command.getHeaderParam(Fields.MESSAGE_ID))).intValue() + 1;
        result.setHeaderField(Fields.MESSAGE_ID, msg_id + "");

        holder.put(CFSConstants.RESULT, result);

        CMMessage responseMsg = new CMMessage();
        responseMsg.setAttribute("result", ((CMResultSet) holder.get(CFSConstants.RESULT)).toString());

        msg.setAttribute(CFSConstants.DIRECTION, CFSConstants.OUT_GOING);
        CFSLoggerNew cfsLogger = new CFSLoggerNew();
        cfsLogger.process(msg, holder);

        try {
            ext_connector.sendAsyncText(responseMsg);
        } catch (Exception e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(e);
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        String ext_connector_name = cfg.get("external-connector");
        ext_connector = ChannelManagerEngine.getInstance().getConnector(ext_connector_name);
        if (ext_connector == null)
            throw new ConfigurationException("Cannot find external connector " + ext_connector_name);
    }

    public String getActionCode(Object obj, Map holder) throws CFSFault {
        CMMessage msg = ((CMMessage) obj);
        String faultCode = (String) msg.getAttribute(CMMessage.FAULT_CODE);
        String resp;
        if (faultCode.equals(CFSFault.FLT_GENERAL_DATA_ERROR))
            resp = ActionCode.GENERAL_DATA_ERROR;
        else if (faultCode.equals(CFSFault.FLT_GENERAL_ERROR))
            resp = ActionCode.GENERAL_ERROR;
        else if (faultCode.equals(CFSFault.FLT_ACTIVE_BATCH_NOT_FOUND))
            resp = ActionCode.ACTIVE_BATCH_NOT_FOUND;
        else if (faultCode.equals(CFSFault.FLT_SYSTEM_MALFUNCTION))
            resp = ActionCode.SYSTEM_MALFUNCTION;
        else
            resp = ActionCode.GENERAL_ERROR;

        return resp;
    }

}