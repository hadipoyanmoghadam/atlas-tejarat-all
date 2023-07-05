package dpi.atlas.service.cm.source.handler;

import dpi.atlas.service.cm.handler.mail.MailSender;
import dpi.atlas.service.cm.handler.transform.XML2PDFTransformer;

import java.util.HashMap;
import java.util.Map;

/**
 * ChainHandler class
 *
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:28 $
 */
public class ChainHandler implements AgentHandler {

    public void process(Object obj, Map holder) throws Exception {
        //ChainContext c = new ChainContext();
        //c.setData(((CMMessage)obj).getSubject());
        //setCurrentChainContext(c);

        if (holder == null)
            holder = new HashMap();
        new XML2PDFTransformer().process(obj, holder);
        new MailSender().process(obj, holder);
    }

    private static ThreadLocal currentChainContext = new ThreadLocal();

    protected static void setCurrentChainContext(ChainContext cc) {
        currentChainContext.set(cc);
    }

    public static ChainContext getCurrentChainContext() {
        return (ChainContext) currentChainContext.get();
    }
}
