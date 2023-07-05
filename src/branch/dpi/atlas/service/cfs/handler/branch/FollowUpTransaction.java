package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author <a href="mailto:Behnaz.sh@dpi.ir">SH.Behnaz</a>
 * @version $Revision: 1.1 $ $Date: 2013/9/09 8:57:04 $
  */

public class FollowUpTransaction extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        //-------- Preparing destAccNo--------
        String destAccNo = msg.getAttributeAsString(Fields.DEST_ACCOUNT);

        if (destAccNo != null) {
            try {
                destAccNo = ISOUtil.zeropad(destAccNo, 13);
            } catch (ISOException e) {
                e.toString();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        String accountNo = msg.getAttributeAsString(Fields.SRC_ACCOUNT);
        try {
            accountNo = ISOUtil.zeropad(accountNo, 13);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + accountNo + "' in FollowUpTransaction : " + e.getMessage());
        }

        //-------- Preparing txOrigDate--------
        String txOrigDate = (String) msg.getAttribute(Fields.DATE);
        if (txOrigDate.length() > 6) {
            txOrigDate = txOrigDate.substring(txOrigDate.length() - 6);
        }

        String serviceType=msg.getAttributeAsString(Fields.SERVICE_TYPE);
        String txOrigTime=msg.getAttributeAsString(Fields.TIME);
        String sgbBranchId=msg.getAttributeAsString(Fields.BRANCH_CODE).trim().substring(1);
        String cardNo=msg.getAttributeAsString(Fields.PAN);
        String branchDocNo=msg.getAttributeAsString(Fields.BRANCH_DOC_NO);

        try {
           long cardBalance=CFSFacadeNew.getFollowTx(cardNo,accountNo,destAccNo,txOrigDate,txOrigTime,sgbBranchId,branchDocNo,serviceType);
            Tx tx=new Tx();
            tx.setAmount(cardBalance);
            holder.put("tx", tx);
            msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, ActionCode.APPROVED);

        } catch (NotFoundException e) {
            log.error(e);
            msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, ActionCode.FLW_HAS_NO_ORIGINAL);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.FLW_HAS_NO_ORIGINAL);

        } catch (SQLException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}