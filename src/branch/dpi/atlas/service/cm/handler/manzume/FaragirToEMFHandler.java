package branch.dpi.atlas.service.cm.handler.manzume;

import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * User:F.Heydari
 * Date:3 NOV 2022
 * Time:14:41 PM
 */


public class FaragirToEMFHandler extends CMHandlerBase {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        ManzumeMessage manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        CMResultSet result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);
        String actionCode = null;
        if (result != null)
            actionCode = result.getHeaderField(Fields.ACTION_CODE);

        if (log.isDebugEnabled()) log.debug("actionCode:" + actionCode);

        if (actionCode == null) {
            actionCode = ActionCode.GENERAL_ERROR;
            log.error("Returned ActionCode is null");
        }
        msg.setAttribute(Fields.ACTION_CODE, actionCode);

        try {
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            String commandName = command.getCommandName();
            if (!command.getCommandName().equals(TJCommand.CMD_MANZUME_FOLLOWUP) && !actionCode.startsWith("00"))
                return;

            result.moveFirst();
            result.next();

            if (commandName.equals(TJCommand.CMD_MANZUME_FOLLOWUP)) {
                if (actionCode.equalsIgnoreCase(ActionCode.TRANSACTION_NOT_FOUND))
                    actionCode = ActionCode.FLW_HAS_NO_ORIGINAL;
                msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, actionCode);
                command.addHeaderParam(Fields.ACTION_CODE, ActionCode.APPROVED);
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.APPROVED);

            }
            if (commandName.equals(TJCommand.CMD_MANZUME_DEPOSIT_REVERSAL)) {
            } else if (commandName.equals(TJCommand.CMD_MANZUME_DEPOSIT)) {
                String balance = result.getString("DESTBALANCE");
                balance = ISOUtil.zeropad(ISOUtil.zeroUnPad(balance), 17);
                String debitCredit = (result.getString("SIGNDESTBALANCE").equals("-") ? "-" : "+");
                manzumeMessage.setActualBalance(debitCredit + balance);

            }

        } catch (Exception e) {
            log.error("ERROR :::Inside FaragirToEMFHandler.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}