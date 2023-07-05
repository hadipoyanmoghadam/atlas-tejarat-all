package dpi.atlas.service.cm.source.handler;

import java.util.Map;

/**
 * AgentHandler interface
 *
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:28 $
 */
public interface AgentHandler {
    public void process(Object obj, Map holder) throws Exception;
}
