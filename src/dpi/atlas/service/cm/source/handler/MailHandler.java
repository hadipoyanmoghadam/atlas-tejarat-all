package dpi.atlas.service.cm.source.handler;

import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.mail.MailSender;

import java.util.Map;

/**
 * MailHandler class
 *
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:28 $
 */
public class MailHandler implements AgentHandler {
    public void process(Object obj, Map holder) throws Exception {
        CMMessage mailMessage = (CMMessage) obj;
        new MailSender().process(mailMessage, holder);
    }
}
