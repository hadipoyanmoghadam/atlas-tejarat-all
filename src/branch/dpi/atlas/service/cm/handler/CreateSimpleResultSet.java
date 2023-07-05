package branch.dpi.atlas.service.cm.handler;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: mb
 * Date: Aug 20, 2007
 * Time: 7:04:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateSimpleResultSet extends CMHandlerBase implements Configurable {

    private String action_code;

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        CMCommand command = null;
        try {
            command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

            CMResultSet result = new CMResultSet();
            result.setHeaderField(Fields.ACTION_CODE, action_code);
            if (command.getHeaderParam(Fields.MESSAGE_ID) != null) {
                int msg_id = (new Integer(command.getHeaderParam(Fields.MESSAGE_ID))).intValue() + 1;
                result.setHeaderField(Fields.MESSAGE_ID, msg_id + "");
                msg.setAttribute(Fields.MESSAGE_ID, String.valueOf(msg_id));
            }
            result.setRequest(command.toString());
            msg.setAttribute(CMMessage.RESPONSE, result);

        } catch (Exception e) {
            //todo: throws appropriat CMFault
            log.error(e.getMessage());
            throw new CMFault(CMFault.FAULT_EXTERNAL_INVALID_MSG);
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        action_code = cfg.get("action_code");
        if ((action_code == null) || (action_code.trim().equals("")))
            throw new ConfigurationException("action code is not set in CreateResultSet");
    }
}
