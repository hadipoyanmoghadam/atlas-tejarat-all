package dpi.atlas.service.cfs.handler;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.4 $ $Date: 2007/10/29 14:04:19 $
 */

public class InitActionCode extends CFSHandlerBase implements Configurable {

    private String actionCode;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        msg.setAttribute(CFSConstants.ACTION_CODE, ActionCode.APPROVED);

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        actionCode = cfg.get(CFSConstants.ACTION_CODE);
        if (log.isInfoEnabled()) log.info(actionCode);
    }
}
