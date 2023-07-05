package dpi.atlas.service.cfs.handler;

import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.DateUtil;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:pnaeimi@dpi2.dpi.net.ir">Parisa Naeimi</a>
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.8 $ $Date: 2007/10/30 12:04:09 $
 */

public class CFSLoggerNew extends CFSHandlerBase implements Configurable {

    protected String onlineOrOffline;
    protected String direction;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        if (log.isInfoEnabled()) log.info("inside CFSLoggerNew:doProcess");

        try {
             if (direction == null) direction = (String) msg.getAttribute(CFSConstants.DIRECTION);
            String logDate = DateUtil.getSystemDate();
            String logTime = DateUtil.getSystemTime();

            String messageId = "";
            String actionCode = "";

            String sessionId = msg.getAttributeAsString(Fields.SESSION_ID);
            if (direction.equals(CFSConstants.IN_COMING)) {
                messageId = msg.getAttributeAsString(Fields.MESSAGE_ID);
            } else if (direction.equals(CFSConstants.OUT_GOING)) {
                actionCode = msg.getAttributeAsString(CFSConstants.ACTION_CODE);
                CMResultSet result = (CMResultSet) holder.get(CFSConstants.RESULT);
                messageId = result.getHeaderField(Fields.MESSAGE_ID);
            }
            String txType = msg.getAttributeAsString(Fields.MESSAGE_TYPE);
            String isTjPayaRec = msg.getAttributeAsString(Constants.IS_TJ_ACH_FILE);
            if (txType!=null  && txType.equals(TJCommand.ACH_FILE_REG_CMD) ||
                    (isTjPayaRec!=null && isTjPayaRec.equalsIgnoreCase("YES"))) 
              txType = TJCommand.GROUP_ACH_FUNDTRANSFER_CMD;
            String serviceType = msg.getAttributeAsString(Fields.SERVICE_TYPE);
            String txString = msg.getAttributeAsString(Fields.MSG_STR);

            long batchPk = 0;
            // TODO if does not contain???
            if (holder.containsKey(CFSConstants.CURRENT_BATCH))
                batchPk = ((Batch) holder.get(CFSConstants.CURRENT_BATCH)).getBatchPk();

            try {
                CFSFacadeNew.insertCFSTxLog(sessionId, messageId, logDate, logTime, txType, serviceType, actionCode, txString, batchPk);
            } catch (SQLException e) {
                log.error(e);
                if (onlineOrOffline.equals(CFSConstants.OFFLINE))
                    throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR_RETRY, e);
                else
                    throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
            }

            if (holder.containsKey(CFSConstants.NO_ACTIVE_BATCH) && (holder.get(CFSConstants.NO_ACTIVE_BATCH)).equals(CFSConstants.TRUE) && direction.equals(CFSConstants.IN_COMING))
                throw new CFSFault(CFSFault.FLT_ACTIVE_BATCH_NOT_FOUND, new Exception(ActionCode.ACTIVE_BATCH_NOT_FOUND));

            if (holder.get(CFSConstants.DUPLICATE_MESSAGE) != null)
                if ((holder.get(CFSConstants.DUPLICATE_MESSAGE)).equals(CFSConstants.TRUE) && direction.equals(CFSConstants.IN_COMING))
                    throw new CFSFault(CFSFault.FLT_DUPLICATE, new Exception(ActionCode.DUPLICATE));
        } catch (CFSFault cfsFault) {
            if (direction.equals(CFSConstants.IN_COMING))
                throw cfsFault;
            else
                throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.SYSTEM_MALFUNCTION));

        } catch (Exception e) {
            log.error(e);
            if (direction.equals(CFSConstants.IN_COMING))
                throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, new Exception(ActionCode.GENERAL_ERROR));
            else
                throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.SYSTEM_MALFUNCTION));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        direction = cfg.get(CFSConstants.DIRECTION);
        if ((direction == null) || (direction.trim().equals("")))
            log.fatal("Direction is not Specified");
        onlineOrOffline = cfg.get(CFSConstants.ONLINE_OR_OFFLINE);
    }
}
