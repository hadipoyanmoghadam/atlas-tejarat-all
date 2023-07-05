package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
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
 * Date: July 12, 2015
 * Time: 14:38 PM
 */
public class CheckGiftDocNo extends CFSHandlerBase implements Configurable {
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String nationalCode = msg.getAttributeAsString(Fields.NATIONAL_CODE).trim();
        String branchDocNo = msg.getAttributeAsString(Fields.BRANCH_DOC_NO).trim();
        String branchCode = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
        String origDate = (String) msg.getAttribute(Fields.DATE);
        if (origDate.length() > 6)
            origDate = origDate.substring(origDate.length() - 6);
        try {
            if (CFSFacadeNew.checkGiftDocNo(branchCode, nationalCode, branchDocNo, origDate))
                throw new CFSFault(CFSFault.FLT_DUPLICATE, ActionCode.DUPLICATE);
        } catch (SQLException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        }
    }
}