package dpi.atlas.service.cfs.handler;

import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.util.CMUtil;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class GetBatch extends CFSHandlerBase implements Configurable {
    protected ArrayList revTxns = new ArrayList();

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
       String commandName=msg.getAttributeAsString(Fields.COMMAND);
        Iterator it = revTxns.iterator();
        while (it.hasNext()) {
            String txnType = (String) it.next();
            if (commandName.equals(txnType))
                return;
        }

        Batch batch = null;
        String batchNo = msg.getAttributeAsString(CFSConstants.CURRENT_BATCH);
        if (batchNo != null) {
            batch = new Batch(Long.valueOf(batchNo));
            holder.put(CFSConstants.CURRENT_BATCH, batch);
            holder.put(CFSConstants.NO_ACTIVE_BATCH, CFSConstants.FALSE);
        } else {
            log.error("Batch No is Empty, Please specify BatchNo from Channel...");
            throw new CFSFault(CFSFault.FLT_ACTIVE_BATCH_NOT_FOUND, new Exception(ActionCode.ACTIVE_BATCH_NOT_FOUND));
        }
        //TODO what happens if batch for EBanking become NULL??? if it's managed by CM?(not by CFS)??
        //TODO batch should be mad as a Batch object not a simple string ????
        return;
    }
    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        revTxns= CMUtil.tokenizString( cfg.get("not-for"), ",");
    }
}
