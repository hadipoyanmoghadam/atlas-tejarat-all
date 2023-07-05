package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Jul 12 2021
 * Time: 02:04 PM
 */
public class GetBlockWageInfo extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        try {

            String pin = msg.getAttributeAsString(Fields.PIN);

            if (!pin.equals(Constants.PIN_CHANGE_ACCOUNT_STATUS_SIMIN) && !pin.equals(Constants.PIN_CHANGE_STATUS_CBI))
                return;

            Map<Long, String> infoMap = ChannelFacadeNew.fillCMParamMap(Constants.BLOCK_WAGE_INFO);
            if (infoMap.size() == 0 || infoMap.size() != 4) {
                throw new NotFoundException("");
            }
            //ID=0 is Operation Code
            msg.setAttribute(Fields.SGB_TX_CODE, infoMap.get(0L));
            //ID=1 is AccountNo
            msg.setAttribute(Fields.DEST_ACCOUNT, infoMap.get(1L));
            //ID=2 wage Amount
            msg.setAttribute(Fields.WAGE_AMOUNT, infoMap.get(2L));

            //ID=3 description
            String description = infoMap.get(3L);
            if (description != null)
                description = description.trim();
            else
                description = "";
            msg.setAttribute(Fields.WAGE_DESCRIPTION, description);

            Map<Long, String> userList = ChannelFacadeNew.fillCMParamMap(Constants.USER_LIST);
            String userID = (String) msg.getAttribute(Fields.SIMIN_USER_ID);
            if (userID != null && userList.containsValue(userID)) {
                msg.setAttribute(Fields.WAGE_WITH_DOCUMENT, Constants.FALSE);
            } else
                msg.setAttribute(Fields.WAGE_WITH_DOCUMENT, Constants.TRUE);

        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.WAGE_INFORMATION_IS_NOT_DEFINED);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.WAGE_INFORMATION_IS_NOT_DEFINED));
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside Channel.GetBlockWageInfo.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
