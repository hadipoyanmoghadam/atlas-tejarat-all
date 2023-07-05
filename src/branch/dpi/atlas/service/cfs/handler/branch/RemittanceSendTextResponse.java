package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.handler.general.SendTextResponse;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Aug 11, 2019
 * Time: 10:00 AM
 * This Class Send Made String to MQ
 */
public class RemittanceSendTextResponse extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SendTextResponse.class);

    Connector connector;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside SendResponse:process()  -- " + this.getClass().getName());
        try {
            CMMessage responseMsg = new CMMessage();

            if (holder.get(CFSConstants.NOTIFICATION_STR)== null)
                return;

            responseMsg.setAttribute("result", (holder.get(CFSConstants.NOTIFICATION_STR)));

            /**Logger in TB_remittance_LOG **/
            String txString = holder.get(CFSConstants.NOTIFICATION_STR).toString();
            String sessionId = msg.getAttribute(Fields.SESSION_ID).toString();
            String messageType = msg.getAttribute(Fields.MESSAGE_TYPE).toString();
            String channelType = msg.getAttributeAsString(Fields.SERVICE_TYPE);
            String sequenceNo = msg.getAttributeAsString(Fields.REQUEST_NO);
            if (sequenceNo == null || sequenceNo.trim().equals("")) sequenceNo = "";
            else sequenceNo = sequenceNo.trim();

            String nationalCode = msg.getAttributeAsString(Fields.NATIONAL_CODE);
            if (nationalCode == null || nationalCode.trim().equals("")) nationalCode = "";
            else nationalCode = nationalCode.trim();

            String extrnalIdNumber = msg.getAttributeAsString(Fields.EXTERNAL_ID_NUMBER);
            if (extrnalIdNumber == null || extrnalIdNumber.trim().equals("")) extrnalIdNumber = "";
            else extrnalIdNumber = extrnalIdNumber.trim();

            String request_date = msg.getAttributeAsString(Fields.REMITTANCE_DATE);
            if (request_date == null || request_date.trim().equals("")) request_date = "";
            else request_date = request_date.trim();
            try {
                CFSFacadeNew.insertRemittanceLog(txString, sessionId, messageType, channelType, sequenceNo,
                        nationalCode,extrnalIdNumber,request_date);
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
