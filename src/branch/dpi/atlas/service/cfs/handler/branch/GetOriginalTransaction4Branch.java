package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:Behnaz.sh@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.1 $ $Date: 2013/6/11 10:57:04 $
 */

public class GetOriginalTransaction4Branch extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        try {

            String origMessageData = msg.getAttributeAsString(Fields.ORIG_MESSAGE_DATA);

            String origmsgSeq = origMessageData.substring(0, 12);

            String date = origMessageData.substring(12, 20);
            String time = origMessageData.substring(20, 26);

            String serviceType = (String) msg.getAttribute(Fields.SERVICE_TYPE);
            if(serviceType!=null && serviceType.trim().equalsIgnoreCase(Constants.CREDITS_SERVICE)){
                if(origmsgSeq!=null && origmsgSeq.trim().startsWith(Constants.NC_SERVICE))
                    serviceType=Constants.NC_SERVICE;
            }

            String messageType = msg.getAttributeAsString(Fields.MESSAGE_TYPE);
            if(messageType!=null && messageType.trim().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_WAGE_REVERSAL))
                serviceType=Constants.BRANCH_WAGE_SERVICE;

            Tx tx = CFSFacadeNew.getOriginalBranchTx(date, origmsgSeq, time,serviceType);
            log.info("tx.getTxPk() = " + tx.getTxPk());

            if (tx.getIsReversed() == CFSConstants.REVERSED)
                throw new CFSFault(CFSFault.FLT_ALREADY_REVERSED, ActionCode.ALREADY_REVERSED);
            else if (tx.getIsReversed() != CFSConstants.NOT_REVERSED)
                throw new CFSFault(CFSFault.FLT_FORMAT_NOT_SUPPORTED, ActionCode.FORMAT_NOT_SUPPORTED);
            else
                holder.put("tx", tx);
        } catch (NotFoundException e) {
            throw new CFSFault(CFSFault.FLT_UNABLE_TO_LOCATE_RECORD_ON_FILE, ActionCode.UNABLE_TO_LOCATE_RECORD_ON_FILE);
        } catch (SQLException e) {
            log.error("Can not run SQL Statement: " + e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (CFSFault e) {
            throw e;
        } catch (Exception e) {
            log.error("Error " + e.getMessage());
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
        }
    }

}
