package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.0 $ $Date: 2018/09/17 10:17:49 $
 */

public class GetGroupCardLastCharge extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {

            String chargeType = msg.getAttributeAsString(Fields.DCHARGE_TYPE).trim();
            if (!chargeType.equalsIgnoreCase(Constants.DCHARGE_LAST_CHARGE))
                return;

            String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);
            String pan = msg.getAttributeAsString(Fields.PAN).trim();

            String amount = CFSFacadeNew.getLastChargeAmount(accountNo, pan);
            msg.setAttribute(Fields.AMOUNT, amount);

        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_CHARGE_RECORDS_NOT_FOUND, ActionCode.CHARGE_RECORDS_NOT_FOUND);
        } catch (SQLException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        }

    }
}
