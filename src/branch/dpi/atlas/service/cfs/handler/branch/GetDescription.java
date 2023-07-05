package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Jan 27, 2021
 * Time: 02:04 PM
 */
public class GetDescription extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String sgbTxCode= msg.getAttributeAsString(Fields.SGB_TX_CODE);

        try {

            List<String> list = CFSFacadeNew.getDescription(sgbTxCode);

            msg.setAttribute(Fields.DOCUMENT_DESCRIPTION, list.get(0).trim());
            msg.setAttribute(Fields.EXTRA_INFO, list.get(1).trim());

        } catch (SQLException e) {
            log.error("Data Base error in GetDescription() with code " + sgbTxCode + " from TBsgbcode");
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (NotFoundException e) {
            throw new CFSFault(CFSFault.FLT_UNSUPPORTED_MESSAGE, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
        } catch (Exception e) {
            log.error("Exception::" + e.getMessage());
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
        }
    }
}
