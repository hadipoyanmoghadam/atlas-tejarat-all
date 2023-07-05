package dpi.atlas.service.cm.handler.general;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.ServiceHandler;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mb
 * Date: Aug 20, 2007
 * Time: 6:30:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetMessageFields extends ServiceHandler {
    public void _process(CMMessage msg, Map holder) throws CMFault {
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        if (command.getHeaderParam(Fields.MESSAGE_ID) != null)
            msg.setAttribute(Fields.MESSAGE_ID, command.getHeaderParam(Fields.MESSAGE_ID));
    }
}
