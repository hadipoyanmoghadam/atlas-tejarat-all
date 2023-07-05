package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;

import java.util.Map;

public class SetReversTxCode extends CFSHandlerBase implements Configurable {


    public void doProcess(CMMessage msg, Map holder) throws CFSFault {


        try {
            Tx original_tx = (Tx) holder.get("tx");
            String txCode=original_tx.getTxCode();
            if(txCode!=null && txCode.trim().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE))
                msg.setAttribute(Fields.MESSAGE_TYPE,TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE_REVERSAL);
            else if(txCode!=null && txCode.trim().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE))
                msg.setAttribute(Fields.MESSAGE_TYPE,TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE_REVERSAL);

            original_tx=null;
        } catch (Exception me) {
            log.error(me);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, ActionCode.GENERAL_DATA_ERROR);
        }
    }
}
