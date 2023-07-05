package branch.dpi.atlas.service.cm.handler.pg;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: April 20, 2020
 * Time: 4:40 PM
 */

public class FaragirToEMFHandler extends CMHandlerBase {
    private static Log log = LogFactory.getLog(FaragirToEMFHandler.class);

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        log.debug("Inside FaragirToEMFHandler:process()");
        try {
            String actionCode = null;
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            CMResultSet result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);
            if (result != null)
                actionCode = result.getHeaderField(Fields.ACTION_CODE);
            else
                actionCode = msg.getAttributeAsString(Fields.ACTION_CODE);
            log.debug("actionCode:" + actionCode);

            if (actionCode == null)
                actionCode = ActionCode.GENERAL_ERROR;

            command.addHeaderParam(Fields.ACTION_CODE, actionCode);
            msg.setAttribute(Fields.ACTION_CODE, actionCode);
            if (actionCode.charAt(0) != '0' && !command.getCommandName().equals(TJCommand.CMD_FOLLOW_UP_PG)) {
                msg.setAttribute(CMMessage.RESPONSE, command);
                return;
            }
            result.moveFirst();
            result.next();
            if (command.getCommandName().equals(TJCommand.CMD_FOLLOW_UP_PG)) {
                if (actionCode.equalsIgnoreCase(ActionCode.TRANSACTION_NOT_FOUND) ||
                        actionCode.equalsIgnoreCase(ActionCode.ALREADY_REVERSED))
                    actionCode = ActionCode.FLW_HAS_NO_ORIGINAL;
                msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, actionCode);
                command.addHeaderParam(Fields.ACTION_CODE, actionCode);
                msg.setAttribute(Fields.ACTION_CODE, actionCode);
            }
            msg.setAttribute(CMMessage.RESPONSE, command);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new CMFault(CMFault.FAULT_EXTERNAL);
        }
    }
}
