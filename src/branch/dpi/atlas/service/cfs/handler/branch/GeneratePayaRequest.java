package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Aug 08, 2020
 * Time: 11:19 AM
 */

public class GeneratePayaRequest extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {

            String branchCode = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
            String dueDate = msg.getAttributeAsString(Fields.DUE_DATE);
            String serial = msg.getAttributeAsString(Fields.BRANCH_DOC_NO);
            String txOrigDate = (String) msg.getAttribute(Fields.DATE);
            String opCode = msg.getAttributeAsString(Fields.SGB_TX_CODE);
            List<String> list = CFSFacadeNew.generatePayaRequestFromDb(branchCode, dueDate, serial, txOrigDate, opCode);
            if (list==null || list.size() < 1) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.CAN_NOT_GENERATE_PAYA_REQUEST);
                throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.CAN_NOT_GENERATE_PAYA_REQUEST));
            }

            String xmlStr = list.get(0);

            if (xmlStr == null || xmlStr.trim().equalsIgnoreCase("")) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.CAN_NOT_GENERATE_PAYA_REQUEST);
                throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.CAN_NOT_GENERATE_PAYA_REQUEST));
            }

            msg.setAttribute(Fields.PAYA_REQUEST, xmlStr);
            msg.setAttribute(Fields.TRACK_ID, list.get(1));

        } catch (NotFoundException e1) {
            log.error(e1);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYA_REQUEST_NOT_FOUND);
            throw new CFSFault(CFSFault.FLT_PAYA_REQUEST_NOT_FOUND, ActionCode.PAYA_REQUEST_NOT_FOUND);
        } catch (SQLException e1) {
            log.error(e1);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (ISOException e1) {
            log.error(e1);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, ActionCode.GENERAL_DATA_ERROR);
        } catch (CFSFault e) {
            throw e;
        } catch (Exception e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
