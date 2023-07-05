package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: May 05, 2018
 * Time: 13:40 PM
 */

public class InquiryTransaction extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

//        String serviceType = msg.getAttributeAsString(Fields.SERVICE_TYPE);
        String accountNo = msg.getAttributeAsString(Fields.SRC_ACCOUNT);

        String docNo = msg.getAttributeAsString(Fields.BRANCH_DOC_NO);
        String log_id=msg.getAttributeAsString(Fields.LOG_ID);
        String opertionCode=msg.getAttributeAsString(Fields.OPERATION_CODE);
        String terminal_id=msg.getAttributeAsString(Fields.TERMINAL_CODE);
        String branchId=msg.getAttributeAsString(Fields.ISSUER_BRANCH_CODE);
        String date = (String) msg.getAttribute(Fields.DATE);
        String time = msg.getAttributeAsString(Fields.TIME);
        String requestType = msg.getAttributeAsString(Fields.REQUEST_TYPE);

        try {

            accountNo = ISOUtil.zeropad(accountNo, 13);

            Tx tx = CFSFacadeNew.getInquiryTx(accountNo, docNo,log_id,requestType);

            if(((Batch) holder.get(CFSConstants.CURRENT_BATCH)).getBatchPk()!=tx.getBatchPk())
                throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, ActionCode.INVALID_OPERATION);

            if (tx.getIsReversed() == CFSConstants.REVERSED)
                throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, ActionCode.INVALID_OPERATION);

            holder.put("tx", tx);
            holder.put(Constants.RESULT_SIZE, "1");

        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + accountNo + "' in InquiryTransaction : " + e.getMessage());
        } catch (NotFoundException e) {
            throw new CFSFault(CFSFault.FLT_UNABLE_TO_LOCATE_RECORD_ON_FILE, new Exception(ActionCode.UNABLE_TO_LOCATE_RECORD_ON_FILE));
        } catch (SQLException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}

