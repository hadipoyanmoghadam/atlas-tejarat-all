package branch.dpi.atlas.service.cm.handler.SMS;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 User: F.Heydari
 Date: NOV 17, 2019
 Time: 1:44 PM
 *
 */
public class InitActionCode extends CMHandlerBase implements Configurable {
    private String actionCode;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        msg.setAttribute(Fields.ACTION_CODE, ActionCode.APPROVED);

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        actionCode = cfg.get(Fields.ACTION_CODE);
        if (log.isInfoEnabled()) log.info(actionCode);
    }
}
