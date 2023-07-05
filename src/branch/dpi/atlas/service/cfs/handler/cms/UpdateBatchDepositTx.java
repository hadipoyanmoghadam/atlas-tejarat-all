package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.entity.Tx;
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
 * @author <a href="mailto:behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.0 $ $Date: 2015/12/15 13:17:49 $
 */

public class UpdateBatchDepositTx extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String branchCode = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
        String requestNo = msg.getAttributeAsString(Fields.REQUEST_NO);
        String documentNo = msg.getAttributeAsString(Fields.BRANCH_DOC_NO);
        String transDate = msg.getAttributeAsString(Fields.DATE);
        String accountNo=msg.getAttributeAsString(Fields.BATCH_CREDIT_ACCOUNT);
        long amount;
        try {
            if (log.isDebugEnabled()) log.debug("branchCode=" + branchCode);
            amount = CFSFacadeNew.getBatchDepositTx(branchCode, requestNo, documentNo, transDate,accountNo);

        } catch (ModelException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_ALREADY_REVERSED, ActionCode.ALREADY_REVERSED);
        } catch (ServerAuthenticationException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_REQUEST_NUMBER_HAS_BEEN_ASSIGNED_TO_DOCUMENT_BEFORE, ActionCode.CMS_REQUEST_NUMBER_HAS_BEEN_ASSIGNED_TO_DOCUMENT_BEFORE);
        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_TRANSACTION_NOT_FOUND, ActionCode.TRANSACTION_NOT_FOUND);
        } catch (SQLException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        }
        Tx tx = new Tx();
        tx.setAmount(amount);
        holder.put("tx", tx);

    }
}
