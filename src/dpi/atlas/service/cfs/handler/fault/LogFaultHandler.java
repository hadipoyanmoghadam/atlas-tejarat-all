package dpi.atlas.service.cfs.handler.fault;

import dpi.atlas.model.tj.entity.FaultLog;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * InternalFaultHandler class
 *
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.4 $ $Date: 2007/10/29 14:04:28 $
 */
public class LogFaultHandler extends CFSHandlerBase implements Configurable {

    public LogFaultHandler() {
        super();
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        if (log.isDebugEnabled()) log.debug("In Log Fault Handler");

        String faultCode = (String) msg.getAttribute(CMMessage.FAULT_CODE);
        if (log.isDebugEnabled()) log.debug("faultCode : " + faultCode);

        try {
            FaultLog fpLog = new FaultLog();
            fpLog.setSessionId((String) msg.getAttribute(Fields.SESSION_ID));

            if (faultCode.equals(CFSFault.FLT_FROM_ACC_BAD_STATUS_LOG))
                fpLog.setActionCode(ActionCode.FROM_ACCOUNT_BAD_STATUS);
            else if (faultCode.equals(CFSFault.FLT_INVALID_OPERATION))
                fpLog.setActionCode(ActionCode.INVALID_OPERATION);
            else if (faultCode.equals(CFSFault.FLT_TO_ACC_BAD_STATUS_LOG))
                fpLog.setActionCode(ActionCode.TO_ACCOUNT_BAD_STATUS);
            else if (faultCode.equals(CFSFault.FLT_ACC_BAD_STATUS_LOG))
                fpLog.setActionCode(ActionCode.ACCOUNT_BAD_STATUS);
            else if (faultCode.equals(CFSFault.FLT_CLOSED_ACCOUNT_LOG))
                fpLog.setActionCode(ActionCode.CLOSED_ACCOUNT);
            else if (faultCode.equals(CFSFault.FLT_NOT_SUFFICIENT_FUNDS_LOG))
                fpLog.setActionCode(ActionCode.NOT_SUFFICIENT_FUNDS);
            else if (faultCode.equals(CFSFault.FLT_ENABLED_BEFORE))
                fpLog.setActionCode(ActionCode.ACCOUNT_ENABLED_BEFORE);
            else if (faultCode.equals(CFSFault.FLT_DISABLED_BEFORE))
                fpLog.setActionCode(ActionCode.ACCOUNT_DISABLED_BEFORE);
            else if (faultCode.equals(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED_LOG))
                fpLog.setActionCode(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            else if (faultCode.equals(CFSFault.FLT_DESTINATION_ACCOUNT_INVALID_LOG))
                fpLog.setActionCode(ActionCode.DESTINATION_ACCOUNT_INVALID);
            else if (faultCode.equals(CFSFault.FLT_CUSTOMER_NOT_FOUND_LOG))
                fpLog.setActionCode(ActionCode.CUSTOMER_NOT_FOUND);
            else if (faultCode.equals(CFSFault.FLT_DUPLICATE_NEW_RECORD_REJECTED_LOG))
                fpLog.setActionCode(ActionCode.DUPLICATE_NEW_RECORD_REJECTED);
            else if (faultCode.equals(CFSFault.FLT_DESTINATION_BRANCH_NOT_FOUND_LOG))
                fpLog.setActionCode(ActionCode.DESTINATION_BRANCH_NOT_FOUND);
            else if (faultCode.equals(CFSFault.FLT_NULL_VALUE_FOR_NOT_NULL_FIELD_LOG))
                fpLog.setActionCode(ActionCode.NULL_VALUE_FOR_NOT_NULL_FIELD);
            else if (faultCode.equals(CFSFault.FLT_ACCOUNT_RANGE_NOT_FOUND_LOG))
                fpLog.setActionCode(ActionCode.ACCOUNT_RANGE_NOT_FOUND);
            else if (faultCode.equals(CFSFault.FLT_DUPLICATE_LOG))
                fpLog.setActionCode(ActionCode.DUPLICATE);
            else if (faultCode.equals(CFSFault.FLT_REGION_NOT_FOUND_LOG))
                fpLog.setActionCode(ActionCode.REGION_NOT_FOUND);
            else if (faultCode.equals(CFSFault.FLT_DEVICE_NOT_FOUND_LOG))
                fpLog.setActionCode(ActionCode.DEVICE_NOT_FOUND);
            else if (faultCode.equals(CFSFault.FLT_GENERAL_DATA_ERROR_LOG))
                fpLog.setActionCode(ActionCode.GENERAL_DATA_ERROR);
            else if (faultCode.equals(CFSFault.FLT_ACCOUNT_NOT_ASSIGNED_TO_CARD))
                fpLog.setActionCode(ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD);

//            getCFSFacade().putFaultLog(fpLog);

        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(e);
        }
    }

}