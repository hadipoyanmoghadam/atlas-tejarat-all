package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * OFSSender class
 *
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.4 $ $Date: 2007/10/29 14:04:22 $
 */
public class HTTPLoopBackSender extends CMHandlerBase {
    private static Log log = LogFactory.getLog(HTTPLoopBackSender.class);

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        OutputStream out = (OutputStream) msg.getAttribute(CMMessage.OUTPUT_STREAM);
        try {
            out.write(msg.getAttribute(CMMessage.RESPONSE).toString().getBytes());
            if (log.isDebugEnabled()) log.debug(msg.getAttribute(CMMessage.RESPONSE).toString());
            out.close();
        } catch (IOException e) {
            log.error(e);
        }
    }

}