package branch.dpi.atlas.service.cm.handler;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by R.Nasiri .
 * Date: May 1, 2020
 * Time: 11:29 AM
 */
public class SetActionCode extends CMHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {
            //Sometimes the destination account cannot withdraw the fee amount due to not having enough money.
            // So we need to set the action code using the pending error that the client should follow to
            // understand the final result.
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.TRANSACTION_PENDING);
            CMResultSet result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);
            result.setHeaderField(Fields.ACTION_CODE, ActionCode.TRANSACTION_PENDING);
            msg.setAttribute(CMMessage.RESPONSE, result);

        } catch (Exception e) {
            log.error("Exception ::" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
    }
}
