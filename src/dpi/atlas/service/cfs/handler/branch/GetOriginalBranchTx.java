package dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Tx;
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
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @version $Revision: 1.16 $ $Date: 2007/11/01 21:57:04 $
 */

public class GetOriginalBranchTx extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        try {
            String destAccountNo = (String) msg.getAttribute(Fields.DEST_ACCOUNT);
            String time = (String) msg.getAttribute(Fields.TIME);
            String date = (String) msg.getAttribute(Fields.DATE);
            if (date.length() > 6) {
                date = date.substring(date.length() - 6);
            }

            String amount = (String) msg.getAttribute(Fields.AMOUNT);
            String branchNo = ((String) msg.getAttribute(Fields.BRANCH_CODE)).substring(1);
            String branchDocNo = (String) msg.getAttribute(Fields.BRANCH_DOC_NO);

            Tx tx = CFSFacadeNew.getOriginalBranchTx(destAccountNo, date, amount, branchNo, branchDocNo,time);
            log.info("tx.getTxPk() = " + tx.getTxPk());

            if (tx.getIsReversed() == CFSConstants.REVERSED)
                throw new CFSFault(CFSFault.FLT_ALREADY_REVERSED, new Exception(ActionCode.ALREADY_REVERSED));
            else if (tx.getIsReversed() != CFSConstants.NOT_REVERSED)
                throw new CFSFault(CFSFault.FLT_FORMAT_NOT_SUPPORTED, new Exception(ActionCode.FORMAT_NOT_SUPPORTED));
            else
                holder.put("tx", tx);
        } catch (NotFoundException e) {
            throw new CFSFault(CFSFault.FLT_UNABLE_TO_LOCATE_RECORD_ON_FILE, new Exception(ActionCode.UNABLE_TO_LOCATE_RECORD_ON_FILE));
        } catch (SQLException e) {
            log.error("Can not run SQL Statement: " + e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        }
    }

}
