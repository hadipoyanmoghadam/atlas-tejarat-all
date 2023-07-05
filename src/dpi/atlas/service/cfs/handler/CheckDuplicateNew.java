package dpi.atlas.service.cfs.handler;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;


public class CheckDuplicateNew extends CFSHandlerBase implements Configurable {

    String direction = null;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        if (log.isDebugEnabled()) log.debug("In CheckDuplicateNew::process (Before first CFSFacade operation)");
        boolean txExists = false;
        try {
            Iterator it = revTxns.iterator();
            while (it.hasNext()) {
                String txnType = (String) it.next();
                if (msg.getAttribute(Fields.COMMAND).equals(txnType)) {
                    holder.put(CFSConstants.DUPLICATE_MESSAGE, CFSConstants.FALSE);
                    return;
                }
            }

            try {
                txExists = CFSFacadeNew.checkDuplicate((String) msg.getAttribute(Fields.SESSION_ID), (String) msg.getAttribute(Fields.MESSAGE_ID));
            } catch (SQLException e) {
                log.error(e);
                throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
            }

            if (txExists)
                holder.put(CFSConstants.DUPLICATE_MESSAGE, CFSConstants.TRUE);
            else
                holder.put(CFSConstants.DUPLICATE_MESSAGE, CFSConstants.FALSE);

            if (log.isDebugEnabled()) log.debug("Out CheckDuplicateNew::process.");
        } catch (ModelException me) {
            if (log.isErrorEnabled()) log.error("CheckDuplicate-GENERAL_DATA_ERROR");
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }
    }

    protected ArrayList revTxns = new ArrayList();

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        String txn_types = cfg.get("not-for");
        if ((txn_types == null)) {
            log.fatal("<not-for> property is not Specified");
            throw new ConfigurationException("<not-for> property is not Specified");
        }

        StringTokenizer st = new StringTokenizer(txn_types);
        while (st.hasMoreElements()) {
            String txn_type = st.nextToken(",");
            revTxns.add(txn_type);
        }
    }

}
