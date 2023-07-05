package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Aug 04, 2020
 * Time: 11:19 AM
 */

public class ResendPayaRequest extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {
            int result = CFSFacadeNew.resendPayaRequest();

            switch (result) {
                case 0: //Approve
                    break;
                case 1: // PAYA is not Active
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYA_IS_INACTIVE);
                    throw new CFSFault(CFSFault.FLT_PAYA_IS_INACTIVE, new Exception(ActionCode.PAYA_IS_INACTIVE));
                case 2: // Request not found in TBBRACH
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYA_REQUEST_NOT_FOUND);
                    throw new CFSFault(CFSFault.FLT_PAYA_REQUEST_NOT_FOUND, new Exception(ActionCode.PAYA_REQUEST_NOT_FOUND));
               case 5: //can not sed message to ACH system
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.SYSTEM_MALFUNCTION);
                    throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.SYSTEM_MALFUNCTION));
                case 6: //can not sed message to ACH system
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.CAN_NOT_GENERATE_PAYA_REQUEST);
                    throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.CAN_NOT_GENERATE_PAYA_REQUEST));
                default:
                    log.error("ERROR:: invalid result");
                    throw new Exception();
            }

        } catch (SQLException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (ISOException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, ActionCode.GENERAL_DATA_ERROR);
        } catch (CFSFault e) {
            throw e;
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
