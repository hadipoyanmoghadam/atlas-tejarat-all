package dpi.atlas.service.cfs.handler;

import dpi.atlas.model.AtlasModel;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

public abstract class CFSHandlerBase extends CMHandlerBase implements Configurable {
    protected Log log;
    protected String description;


    protected CFSHandlerBase() {
        log = LogFactory.getLog(this.getClass());
    }

    public void process(Object obj, Map holder) throws CFSFault {
        doProcess((CMMessage) obj, holder);
    }

    public abstract void doProcess(CMMessage msg, Map holder) throws CFSFault;


    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        description = cfg.get(CFSConstants.HANDLER_DESCRIPTION);
        if (description == null)
            description = "";
    }
}
