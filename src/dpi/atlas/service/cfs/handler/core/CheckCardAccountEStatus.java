package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Sahar Amjadi
 * Date: Jun 28, 2012
 * Time: 2:33:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class CheckCardAccountEStatus extends CFSHandlerBase implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        String eSt = "1";
        if (eSt.equals(Constants.E_STATUS_CLOSE))
            throw new CFSFault(CFSFault.E_BLOCKED_ACCOUNT, new Exception(ActionCode.ACCOUNT_IS_E_BLOCKED));
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
