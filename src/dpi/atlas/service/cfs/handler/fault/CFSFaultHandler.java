package dpi.atlas.service.cfs.handler.fault;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;


public abstract class CFSFaultHandler extends CFSHandlerBase implements Configurable {

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        try {

            String actionCode = getActionCode(msg, holder);
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            CMResultSet result = new CMResultSet();
            result.setHeaderField(Params.ACTION_CODE, actionCode);
            result.setRequest(command.toString());

            if (command.getHeaderParam(Fields.MESSAGE_ID) != null) {
                int msg_id = (new Integer(command.getHeaderParam(Fields.MESSAGE_ID))) + 1;
                result.setHeaderField(Fields.MESSAGE_ID, msg_id + "");
            }

            CFSlog(result, command, (dpi.atlas.model.tj.entity.Batch) holder.get(CFSConstants.CURRENT_BATCH));

            if ("CUTOVR".equalsIgnoreCase(command.getCommandName())) {
                msg.getAttributesMap().put("response-string", result.toString());
                connector.sendAsyncText(msg.getAttributesMap());
            }
            else {
                msg.setAttribute("result", result.toString());
                connector.sendAsyncText(msg);
            }
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(e);
        }
    }

    public void CFSlog(CMResultSet result, CMCommand command, dpi.atlas.model.tj.entity.Batch batch) throws CMFault {

        dpi.atlas.model.tj.entity.TxLog txlog = new dpi.atlas.model.tj.entity.TxLog();
        String strDate = DateUtil.getSystemDate();
        String strTime = DateUtil.getSystemTime();
        txlog.setSessionId(command.getHeaderParam(Fields.SESSION_ID));
        txlog.setLogDate(strDate);
        txlog.setLogTime(strTime);
        txlog.setMessageId(result.getHeaderField(Fields.MESSAGE_ID));
        txlog.setTxType(command.getCommandName());
        txlog.setServiceType(command.getHeaderParam(Fields.SERVICE_TYPE));
        txlog.setTxString(command.toString());
        txlog.setTxDateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        if (batch != null) txlog.setBatchPk(batch.getBatchPk());
        txlog.setActionCode(result.getHeaderField(Params.ACTION_CODE));
        try {
            CFSFacadeNew.insertCFSTxLog(txlog);
        }
        catch (SQLException me) {
            if (log.isDebugEnabled()) log.debug(me.getMessage());
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }
    }

    public abstract String getActionCode(Object obj, Map holder) throws CFSFault;

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}
