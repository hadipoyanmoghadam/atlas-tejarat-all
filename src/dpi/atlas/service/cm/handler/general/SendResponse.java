package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

public class SendResponse extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SendResponse.class);

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
// Removed because of SAF
//        if (((String)msg.getAttribute(Fields.FORCE_POST)).equals(CFSConstants.FP_AUTHORIZE_REQUEST)) {
        // If forcepost != 0, no outgoing message exists
        if (log.isInfoEnabled()) log.info("Inside SendResponse:process()  -- " + this.getClass().getName());
        try {

//                if (log.isDebugEnabled())             log.debug(msg.toString());
//                connector.sendMsg(msg);

            if (log.isDebugEnabled()) log.debug("result:" + ((CMResultSet) holder.get(CFSConstants.RESULT)).toString());

            CMMessage responseMsg = new CMMessage();
            if (log.isDebugEnabled()) log.debug("here0");
            responseMsg.setAttribute("result", ((CMResultSet) holder.get(CFSConstants.RESULT)).toString());
            //Ahmadi added this to use Temporary Queue in 86/02/13
            if (log.isDebugEnabled())             log.debug("msg.hasAttribute(\"JMSReplyTo\") = " + msg.hasAttribute("JMSReplyTo"));
            if (msg.hasAttribute("JMSReplyTo")) {
                if (log.isDebugEnabled()) log.debug("here1");
                responseMsg.setAttribute("JMSReplyTo", msg.getAttribute("JMSReplyTo"));
                if (log.isDebugEnabled())
                    log.debug("msg.getAttribute(\"JMSReplyTo\")" + msg.getAttribute("JMSReplyTo"));
            }
            if (log.isDebugEnabled()) log.debug("here2");

//                connector.sendMsg((CMResultSet)holder.get(CFSConstants.RESULT));


            connector.sendAsyncText(responseMsg.getAttributesMap());
            if (log.isDebugEnabled()) log.debug("here3");
//                connector.sendMsg(((CMResultSet)holder.get(CFSConstants.RESULT)).toString());

//                CMCommand cmCommand = (CMCommand) holder.get(CMMessage.REQUEST);
//                cmCommand.getParams().put(CFSConstants.ACTION_CODE, (String)msg.getAttribute(CFSConstants.ACTION_CODE));
//                cmCommand.getParams().put("JMSCorrelationID", cmCommand.getHeaderParam(Fields.SESSION_ID));
//                if (log.isDebugEnabled())             log.debug(cmCommand);
//                if (log.isDebugEnabled())             log.debug(cmCommand.toString());
//                connector.sendMsg(cmCommand);
        } catch (Exception e) {
            throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.SYSTEM_MALFUNCTION));
        }
// Removed because of SAF
//        }
//        else
//            if (log.isDebugEnabled())             log.debug("Force_Post != 0, Ignoring SendResponse");
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}