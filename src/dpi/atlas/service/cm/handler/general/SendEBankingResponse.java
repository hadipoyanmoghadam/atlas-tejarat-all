package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class SendEBankingResponse extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SendEBankingResponse.class);

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            log.debug("result:" + msg.getAttribute(Constants.RESULT));
            CMMessage responseMsg = new CMMessage();

//*************************this is for test  TB_LOG************************************************
//            responseMsg.setAttribute("timetest", msg.getAttribute("timetest"));
//            responseMsg.setAttribute(Constants.MESSAGE_SEQUENCE_FOR_tblog, msg.getAttribute(Constants.MESSAGE_SEQUENCE_FOR_tblog));
//********************************************************************************
            responseMsg.setAttribute("result", msg.getAttribute(Constants.RESULT));

            log.debug("msg.hasAttribute(\"JMSReplyTo\") = " + msg.hasAttribute("JMSReplyTo"));
            if (msg.hasAttribute("JMSReplyTo")) {
                responseMsg.setAttribute("JMSReplyTo", msg.getAttribute("JMSReplyTo"));
                if (log.isDebugEnabled())
                    log.debug("msg.getAttribute(\"JMSReplyTo\")" + msg.getAttribute("JMSReplyTo"));
            }
            if (msg.hasAttribute("JMSCorrelationID")) {
                responseMsg.setAttribute("JMSCorrelationID", msg.getAttribute("JMSCorrelationID"));

                log.debug("msg.getAttribute(\"JMSCorrelationID\")" + msg.getAttribute("JMSCorrelationID"));
            }
            connector.sendAsync(responseMsg);

        } catch (Exception e) {
            log.error("Exception !!! - MESSAGE_SEQUENCE_FOR_tblog = " + msg.getAttributeAsString(Constants.MESSAGE_SEQUENCE_FOR_tblog) + " -- e :: " + e.getMessage());
            e.printStackTrace();
            throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.SYSTEM_MALFUNCTION));
        }
    }

    public void doProcess_old(CMMessage msg, Map holder) throws CMFault {
// Removed because of SAF
//        if (((String)msg.getAttribute(Fields.FORCE_POST)).equals(CFSConstants.FP_AUTHORIZE_REQUEST)) {
        // If forcepost != 0, no outgoing message exists
        if (log.isInfoEnabled()) log.info("Inside SendEBankingResponse:process()  -- " + this.getClass().getName());
        try {

//                if (log.isDebugEnabled())             log.debug(msg.toString());
//                connector.sendMsg(msg);

            if (log.isDebugEnabled()) log.debug("result:" + (holder.get(CFSConstants.RESULT)).toString());

            CMMessage responseMsg = new CMMessage();
            responseMsg.setAttribute("result", (holder.get(CFSConstants.RESULT)).toString());
            //Ahmadi added this to use Temporary Queue in 86/02/13
            if (log.isDebugEnabled()) log.debug("msg.hasAttribute(\"JMSReplyTo\") = " + msg.hasAttribute("JMSReplyTo"));
            if (msg.hasAttribute("JMSReplyTo")) {
                responseMsg.setAttribute("JMSReplyTo", msg.getAttribute("JMSReplyTo"));
                if (log.isDebugEnabled())
                    log.debug("msg.getAttribute(\"JMSReplyTo\")" + msg.getAttribute("JMSReplyTo"));
            }
            if (msg.hasAttribute("JMSCorrelationID")) {
                responseMsg.setAttribute("JMSCorrelationID", msg.getAttribute("JMSCorrelationID"));

//                connector.sendMsg((CMResultSet)holder.get(CFSConstants.RESULT));


                connector.sendAsyncText(responseMsg);
//                connector.sendMsg(((CMResultSet)holder.get(CFSConstants.RESULT)).toString());

//                CMCommand cmCommand = (CMCommand) holder.get(CMMessage.REQUEST);
//                cmCommand.getParams().put(CFSConstants.ACTION_CODE, (String)msg.getAttribute(CFSConstants.ACTION_CODE));
//                cmCommand.getParams().put("JMSCorrelationID", cmCommand.getHeaderParam(Fields.SESSION_ID));
//                if (log.isDebugEnabled())             log.debug(cmCommand);
//                if (log.isDebugEnabled())             log.debug(cmCommand.toString());
//                connector.sendMsg(cmCommand);

            }
        } catch (Exception e) {
            log.error("Exception !!! - MESSAGE_SEQUENCE_FOR_tblog = " + msg.getAttributeAsString(Constants.MESSAGE_SEQUENCE_FOR_tblog) + " -- e :: " + e.getMessage());
            e.printStackTrace();
            throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.SYSTEM_MALFUNCTION));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}
