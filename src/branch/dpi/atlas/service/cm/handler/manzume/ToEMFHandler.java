package branch.dpi.atlas.service.cm.handler.manzume;

import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * User: F.Heydari
 * Date:3 NOV 2022
 * Time:13:31 PM
 *
 */
public class ToEMFHandler extends TJServiceHandler {


    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        ManzumeMessage manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        CMResultSet result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);

        String actionCode = null;
        if (result != null) actionCode = result.getHeaderField(Fields.ACTION_CODE);

        if (actionCode == null) {
            actionCode = ActionCode.GENERAL_ERROR;
            log.error("Returned ActionCode is null");
        }
        msg.setAttribute(Fields.ACTION_CODE, actionCode);
        if (!actionCode.equals(ActionCode.APPROVED))
            return;

        try {
            result.moveFirst();
            result.next();
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            if (command.getCommandName().equals(TJCommand.CMD_MANZUME_DEPOSIT)) {
                ArrayList ara = (ArrayList) result.getRows().get(0);
               // String actualBalance = getActualBalance(result);
               String actualBalance = getActualBalance4Manzume(result);
                manzumeMessage.setActualBalance(actualBalance);

            } else if (command.getCommandName().equals(TJCommand.CMD_MANZUME_FOLLOWUP)) {
                String followactionCode = result.getHeaderField(Fields.FOLLOWUP_ACTION_CODE);
                msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, followactionCode);
            }


        } catch (ISOException e) {
            log.error("Exception in ToEMFHandler  for SMS." + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, ActionCode.GENERAL_ERROR);
        }
    }

    private String getActualBalance(CMResultSet result) throws ISOException {
        String actualBalance = result.getHeaderField(Constants.ACTUAL_AMOUNT);
        if (actualBalance.startsWith("-"))
            actualBalance = actualBalance.substring(1);
        actualBalance = ISOUtil.zeropad(actualBalance, 17);
        String debitCredit = result.getHeaderField(Constants.DebitCredit);
        actualBalance = (debitCredit.equals("C")) ? "+" + actualBalance : "-" + actualBalance;
        return actualBalance;
    }

    private String getAvailableBalance(CMResultSet result) throws ISOException {
        String availableBalance = result.getHeaderField(Constants.AVAILABLE_AMOUNT);
        if (availableBalance.startsWith("-"))
            availableBalance = availableBalance.substring(1);
        availableBalance = ISOUtil.zeropad(availableBalance, 17);
        String availableDebitCredit = result.getHeaderField(Constants.AvailableDebitCredit);
        availableBalance = (availableDebitCredit.equals("C")) ? "+" + availableBalance : "-" + availableBalance;
        return availableBalance;
    }
    private String getActualBalance4Manzume(CMResultSet result) throws ISOException {
        String actualBalance = result.getHeaderField(Fields.DEST_ACCOUNT_BALANCE);
        if (actualBalance.startsWith("-"))
            actualBalance = actualBalance.substring(1);
        actualBalance = ISOUtil.zeropad(actualBalance, 17);
        actualBalance = ((Long.parseLong(result.getHeaderField(Fields.DEST_ACCOUNT_BALANCE)) >= 0) ? "+" + actualBalance : "-" + actualBalance);
        return actualBalance;
    }


}
