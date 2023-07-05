package branch.dpi.atlas.service.cfs.handler.branch;


import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Aug 11, 2019
 * Time: 11:17 AM
 */
public class RemittanceFaultHandler extends CFSHandlerBase  implements Configurable{

    public void doProcess(CMMessage obj, Map holder) throws CFSFault{
        if (log.isDebugEnabled()) log.debug("In RemittanceFaultHandler Fault Handler");
        CMMessage msg = ((CMMessage) obj);
        String faultCode = (String) msg.getAttribute(CMMessage.FAULT_CODE);
        if (log.isDebugEnabled()) log.debug("faultCode : " + faultCode);

        String actionCode = ActionCode.GENERAL_ERROR;
        if (faultCode.equals(CFSFault.FLT_REMITTANCE_FORMAT))
            actionCode = ActionCode.SMS_NOTIFICATION_FORMAT_ERROR;
       /** SMS Logger in TB_SMS_LOG **/
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
            //   the TX_String field fill with the error action code
            CFSFacadeNew.insertRemittanceLog(actionCode,sessionId,messageType,channelType,sequenceNo,nationalCode,extrnalIdNumber,request_date);
        } catch (SQLException e) {
            log.error(e);
        }
        /****/
//        return actionCode;
    }

}
