package branch.dpi.atlas.service.cm.handler.SMS;

import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
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

import java.util.Map;

/**
 * User:F.Heydari
 * Date:14 Dec 2019
 * Time:8:41 AM
 */


public class FaragirToEMFHandler extends CMHandlerBase {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        SMSMessage smsMessage=(SMSMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

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
            CMCommand command=(CMCommand) msg.getAttribute(CMMessage.REQUEST);
            String commandName = command.getCommandName();
            if (!command.getCommandName().equals(TJCommand.CMD_FOLLOW_UP_SMS) && !actionCode.startsWith("00"))
                return;

            result.moveFirst();
            result.next();

           if (commandName.equals(TJCommand.CMD_FOLLOW_UP_SMS)) {
               if(actionCode.equalsIgnoreCase(ActionCode.TRANSACTION_NOT_FOUND))
                   actionCode=ActionCode.FLW_HAS_NO_ORIGINAL;
               msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, actionCode);
               command.addHeaderParam(Fields.ACTION_CODE, ActionCode.APPROVED);
               msg.setAttribute(Fields.ACTION_CODE, ActionCode.APPROVED);

            }else if (commandName.equals(TJCommand.CMD_SMS_BALANCE)) {
                String availableBalance = result.getString("AVAILABLEBALANCE");
                availableBalance = ISOUtil.zeropad(ISOUtil.zeroUnPad(availableBalance), 17);
                String availableDebitCredit = (result.getString( Constants.AVAILBLE_BAL_SIGN.toUpperCase()).equals("0") ? "+" : "-");

                String actualBalance = result.getString("LEDGERBALANCE");
                actualBalance = ISOUtil.zeropad(ISOUtil.zeroUnPad(actualBalance), 17);
                String debitCredit = (result.getString(Constants.CR_DB).equals(Constants.CREDIT) ? "+" : "-");

                smsMessage.setAvailableBalance(availableDebitCredit+availableBalance);
                smsMessage.setActualBalance(debitCredit+actualBalance);
           }

        } catch (Exception e) {
            log.error("ERROR :::Inside FaragirToEMFHandler.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

