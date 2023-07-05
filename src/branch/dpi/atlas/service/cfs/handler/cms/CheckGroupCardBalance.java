package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.util.Map;


public class CheckGroupCardBalance extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {

            if (!msg.getAttributeAsString(Fields.CARD_TYPE).equalsIgnoreCase(CFSConstants.GROUP_CARD_CHILD))
                throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));
            long amount;
            String chargeType = msg.getAttributeAsString(Fields.DCHARGE_TYPE).trim();
            Long maxtTransLimit = (Long) holder.get(Fields.MAX_TRANS_LIMIT);

            if (chargeType.equalsIgnoreCase(Constants.DCHARGE_AMOUNT) || chargeType.equalsIgnoreCase(Constants.DCHARGE_LAST_CHARGE))
                amount = Long.parseLong(msg.getAttributeAsString(Fields.AMOUNT));
            else
                amount = maxtTransLimit;

            if (maxtTransLimit == 0 || maxtTransLimit < amount)
                throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, new Exception(ActionCode.NOT_SUFFICIENT_FUNDS));

        } catch (CFSFault cfsFault) {
            throw cfsFault;
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, new Exception(ActionCode.GENERAL_ERROR));

        }
    }
}
