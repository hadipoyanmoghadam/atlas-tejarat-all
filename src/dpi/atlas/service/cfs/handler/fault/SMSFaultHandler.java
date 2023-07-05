package dpi.atlas.service.cfs.handler.fault;


import java.util.Map;
import java.sql.SQLException;


import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import org.jpos.core.Configurable;

/**
 * Created by IntelliJ IDEA.
 * User: F.Moghri < Moghri@DPI.IR >
 * Date: Jun 26, 2011
 * Time: 9:25:08 AM
 * in this class if some error occured in making string for sms called this fault handler
 * the action code = 3005
 */
public class SMSFaultHandler  extends CFSHandlerBase  implements Configurable{

    public void doProcess(CMMessage obj, Map holder) throws CFSFault{
        if (log.isDebugEnabled()) log.debug("In SMSNotification Fault Handler");
        CMMessage msg = ((CMMessage) obj);
        String faultCode = (String) msg.getAttribute(CMMessage.FAULT_CODE);
        if (log.isDebugEnabled()) log.debug("faultCode : " + faultCode);

        String actionCode = ActionCode.GENERAL_ERROR;
        if (faultCode.equals(CFSFault.FLT_SMS_FORMAT))
            actionCode = ActionCode.SMS_NOTIFICATION_FORMAT_ERROR;
       /** SMS Logger in TB_SMS_LOG **/
        String sessionId =  msg.getAttribute(Fields.SESSION_ID).toString();
        String messageType = msg.getAttribute(Fields.MESSAGE_TYPE).toString();
        String channelType = msg.getAttributeAsString(Fields.SERVICE_TYPE);
        String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);
        try {
            //   the TX_String field fill with the error action code
            CFSFacadeNew.insertSMSLog(actionCode,sessionId,messageType,channelType,accountNo);
        } catch (SQLException e) {
            log.error(e);
        }
        /****/
//        return actionCode;
    }

}
