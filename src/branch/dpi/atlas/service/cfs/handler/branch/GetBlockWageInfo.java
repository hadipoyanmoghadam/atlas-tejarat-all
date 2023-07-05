package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

public class GetBlockWageInfo extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {

            Map<Long, String> map = CFSFacadeNew.fillCMParamMap(Constants.BLOCK_WAGE_INFO);
            if (map.size() == 0 || map.size() != 4) {
                throw new NotFoundException("");
            }
            //ID=0 is Operation Code
            msg.setAttribute(Fields.SGB_TX_CODE, map.get(0L));
            //ID=1 is AccountNo
            msg.setAttribute(Fields.DEST_ACCOUNT, map.get(1L));
            //ID=2 wage Amount
            msg.setAttribute(Fields.WAGE_AMOUNT, map.get(2L));

            //ID=3 description
            String description = map.get(3L);
            if (description != null)
                description = description.trim();
            else
                description = "";
            msg.setAttribute(Fields.WAGE_DESCRIPTION, description);

            Map<Long, String> userList = CFSFacadeNew.fillCMParamMap(Constants.USER_LIST);
            String userID = (String) msg.getAttribute(Fields.USER_ID);
            if (userID != null && userList.containsValue(userID)) {
                msg.setAttribute(Fields.WAGE_WITH_DOCUMENT, Constants.FALSE);
            } else
                msg.setAttribute(Fields.WAGE_WITH_DOCUMENT, Constants.TRUE);

        } catch (SQLException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (NotFoundException e1) {
            throw new CFSFault(CFSFault.FLT_WAGE_INFORMATION_IS_NOT_DEFINED, ActionCode.WAGE_INFORMATION_IS_NOT_DEFINED);
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
