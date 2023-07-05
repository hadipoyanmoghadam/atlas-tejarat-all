package dpi.atlas.service.cm.handler.general;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 25, 2020
 * Time: 01:52 PM
 */
public class SMSSendTextResponsePG extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SendTextResponse.class);

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside SendResponse:process()  -- " + this.getClass().getName());
        try {
            CMMessage responseMsg = new CMMessage();

            if (holder.get(CFSConstants.SMS_RESULT)== null)
                return;

            List<String> smsList= (List<String>) holder.get(CFSConstants.SMS_RESULT);
            for (int i=0;i<smsList.size();i++) {

               responseMsg.setAttribute("result", (smsList.get(i)));

                /**SMS Logger in TB_SMS_LOG **/
                String txString = smsList.get(i);
                String sessionId = msg.getAttribute(Fields.SESSION_ID).toString();
                String messageType = msg.getAttribute(Fields.MESSAGE_TYPE).toString();
                String channelType = msg.getAttributeAsString(Fields.SERVICE_TYPE);
                String accountNo = ISOUtil.zeropad(smsList.get(i).substring(44, 55), 13);
                try {
                    CFSFacadeNew.insertSMSLog(txString, sessionId, messageType, channelType, accountNo);
                } catch (SQLException e) {
                    log.error(e);
                }
                /****/
                if (msg.hasAttribute("JMSReplyTo")) {
                    responseMsg.setAttribute("JMSReplyTo", msg.getAttribute("JMSReplyTo"));
                    if (log.isDebugEnabled())
                        log.debug("msg.getAttribute(\"JMSReplyTo\")" + msg.getAttribute("JMSReplyTo"));
                }
                connector.sendAsync(responseMsg);
            }

        } catch (Exception e) {
            msg.setAttribute(CMMessage.FAULT_CODE, CFSFault.FAULT_AUTH_SERVER);
            throw new CFSFault(CFSFault.FLT_SMS_FORMAT, new Exception(ActionCode.FORMAT_ERROR));
        }
    }


    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        String connector_name = cfg.get("connector");
        connector = ChannelManagerEngine.getInstance().getConnector(connector_name);
        if (connector == null)
            throw new ConfigurationException("Cannot find connector " + connector_name);
    }

}
