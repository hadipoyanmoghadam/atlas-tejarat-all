package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: July 21, 2015
 * Time: 04:23 PM
 */
public class CancellationInquiry extends CFSHandlerBase implements Configurable {
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String nationalCode = msg.getAttributeAsString(Fields.NATIONAL_CODE).trim();
        String branchDocNo = msg.getAttributeAsString(Fields.BRANCH_DOC_NO).trim();
        String branchCode = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
        String origDate = (String) msg.getAttribute(Fields.DATE);
        long amount = 0;

        try {
            amount = CFSFacadeNew.getGiftCardTx(branchCode, branchDocNo, nationalCode, TJCommand.CMD_CMS_CANCELLATION, 0, origDate);
            msg.setAttribute(Fields.CANCELLATION_AMOUNT, String.valueOf(amount));
        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.error(e);
            throw new CFSFault(CFSFault.FLT_DOCUMENT_NOT_FOUND, ActionCode.DOCUMENT_NOT_FOUND);
        } catch (SQLException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (Exception e) {
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }
    }
}
