package dpi.atlas.service.cm.handler.host;

import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.host.HostResultSet;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:30 $
 */

public interface Host {
    public HostResultSet post(CMMessage msg);
}
