package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.util.mq.mypool.MQTimeoutException;
import dpi.atlas.util.Constants;
import dpi.atlas.client.messages.BaseMessage;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * OutConnector class
 *
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.6 $ $Date: 2007/10/29 14:04:24 $
 */
public class OutSyncConnector extends TJServiceHandler implements Configurable {

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
//            System.out.println(">>>>>> cm 2 cfs message-seq: "
//                    + ((BaseMessage) msg.getAttribute(CMMessage.COMMAND_OBJ)).getMsgSequence()
//                    + " and sessionId: " + msg.getAttributeAsString("sessionId")
//                    + " date/time: " + dpi.atlas.util.DateUtil.getSystemDate() + dpi.atlas.util.DateUtil.getSystemTime());
            Object reply = connector.sendSyncText(msg);
//            System.out.println("<<<<<<< cfs 2 cm reply " + reply.toString() +
//                    "  date/time: " + dpi.atlas.util.DateUtil.getSystemDate() + dpi.atlas.util.DateUtil.getSystemTime());
            holder.put("connector-reply", reply);
        } catch (MQTimeoutException e) {
            msg.setAttribute(Fields.AUTHORISED, Constants.NOTAUTHORISED);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.TIME_OUT);
            log.error(e);
        } catch (Exception e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.SYSTEM_MALFUNCTION);
            throw new CMFault(CMFault.FAULT_INTERNAL_HOST_CONNECTIION, e);
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }
}
