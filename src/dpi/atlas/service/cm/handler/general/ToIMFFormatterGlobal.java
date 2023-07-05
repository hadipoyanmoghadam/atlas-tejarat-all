package dpi.atlas.service.cm.handler.general;

import dpi.atlas.model.AtlasModel;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.imf.ToIMFFormatter;
import org.jpos.core.Configurable;
import org.jpos.iso.ISOMsg;

import java.util.Map;

/**
 * ToIMFFormatterGlobal class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.3 $ $Date: 2007/10/29 14:04:24 $
 */
public class ToIMFFormatterGlobal extends CMHandlerBase implements Configurable {

    public void doProcess(CMMessage cmMessage, Map map) throws CMFault {

        ISOMsg isoMsg = (ISOMsg) cmMessage.getAttribute(CMMessage.REQUEST);

        String service_type = null;
        String request_type = null;
        try {
            service_type = (String) cmMessage.getAttribute(CMMessage.SERVICE_TYPE);
            request_type = (String) cmMessage.getAttribute(CMMessage.REQUEST_TYPE);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        String beanName = service_type + ".convertor." + request_type;
        if (log.isDebugEnabled()) log.debug("beanName = " + beanName);

        CMCommand command = null;
        ToIMFFormatter convertor = (ToIMFFormatter) AtlasModel.getInstance().getBean(beanName);
        command = convertor.format(isoMsg);
        command.addHeaderParam(Fields.SESSION_ID, cmMessage.getAttributeAsString(Fields.SESSION_ID));
        command.addHeaderParam(Fields.MESSAGE_ID, cmMessage.getMessageId() + "");
        //Todo : uncomment this later.
        cmMessage.setAttribute(CMMessage.REQUEST, command);
    }
}
