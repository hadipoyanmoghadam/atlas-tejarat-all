package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.connector.Connector;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.ChannelManagerEngine;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: F.Moghri < Moghri@DPI.IR >
 * Date: Apr 26, 2011
 * Time: 10:08:58 AM
 * This Class Send Made String to MQ -the Queue name is CFS2SMS_ONLINE
 */
public class SMSSendTextResponse extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(SendTextResponse.class);

    Connector connector;
    private String accountField;


    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        if (log.isInfoEnabled()) log.info("Inside SendResponse:process()  -- " + this.getClass().getName());
        try {
            CMMessage responseMsg = new CMMessage();

            if (holder.get(accountField)== null)
                return;

               responseMsg.setAttribute("result", (holder.get(accountField)));

             /**SMS Logger in TB_SMS_LOG **/
              String txString = holder.get(accountField).toString();
              String sessionId =  msg.getAttribute(Fields.SESSION_ID).toString();
              String messageType = msg.getAttribute(Fields.MESSAGE_TYPE).toString();
              String channelType = msg.getAttributeAsString(Fields.SERVICE_TYPE);
//              String accountNo = ISOUtil.zeropad(holder.get(accountField).toString().substring(44,55),13);
            String accountNo = holder.get(accountField).toString().substring(44,55);
            if (accountNo.endsWith("C"))            //capital letter 'C'
                accountNo = Constants.BANKE_TEJARAT_BIN_NEW + accountNo.substring(0, 10);
            else
                accountNo = ISOUtil.zeropad(accountNo, 13);
            try {
                CFSFacadeNew.insertSMSLog(txString ,sessionId, messageType,channelType, accountNo);
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

        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = CFSConstants.SMS_SRC_RESULT;
        } else if (accountField.equals("srcAccount"))
            accountField = CFSConstants.SMS_SRC_RESULT;
        else if (accountField.equals("destAccount"))
            accountField = CFSConstants.SMS_DEST_RESULT;
    }

}
