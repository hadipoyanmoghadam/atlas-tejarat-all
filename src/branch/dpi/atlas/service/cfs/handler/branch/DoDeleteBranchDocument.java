package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: May 13, 2018
 * Time: 9:30 AM
 */


public class DoDeleteBranchDocument extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        HashMap<String, Object> extraData = new HashMap<String, Object>();
        Tx tx =(Tx) holder.get("tx");
        try {
            String accountNo = msg.getAttributeAsString(Fields.SRC_ACCOUNT).trim();
            String requestType = msg.getAttributeAsString(Fields.REQUEST_TYPE);
            CFSFacadeNew.txnDoDeleteTransaction(tx,accountNo,requestType);
        } catch (NotFoundException e) {
            log.error(e);
            throw new CFSFault(CFSFault.FAULT_AUTH_GENERAL_INVALID_ACCOUNT, ActionCode.FUND_TRANSFER_HAVE_NOT_BEEN_DONE);
        } catch (ModelException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, ActionCode.NOT_SUFFICIENT_FUNDS);
        } catch (SQLException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
