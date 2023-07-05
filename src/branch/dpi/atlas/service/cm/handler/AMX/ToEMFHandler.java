package branch.dpi.atlas.service.cm.handler.AMX;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
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
 * User: R.Nasiri
 * Date: Dec 5, 2018
 * Time: 04:05 PM
 */
public class ToEMFHandler extends TJServiceHandler {


    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CMResultSet result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String actionCode = null;
        if (result != null) actionCode = result.getHeaderField(Fields.ACTION_CODE);

        if (actionCode == null) {
            actionCode = ActionCode.GENERAL_ERROR;
            log.error("Returned ActionCode is null");
        }

        command.addHeaderParam(Fields.ACTION_CODE, actionCode);
        msg.setAttribute(Fields.ACTION_CODE, actionCode);
        if (!actionCode.equals(ActionCode.APPROVED))
            return;

        try {
            result.moveFirst();
            result.next();

            if (command.getCommandName().equals(TJCommand.CMD_FOLLOW_UP_SAFE_BOX) ||
                    command.getCommandName().equals(TJCommand.CMD_FOLLOW_UP_MARHOONAT_INSURANCE)) {
                String followactionCode = result.getHeaderField(Fields.FOLLOWUP_ACTION_CODE);
                msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, followactionCode);
            }else if (command.getCommandName().equals(TJCommand.CMD_MARHOONAT_INSURANCE_BALANCE)) {
                ArrayList ara = (ArrayList) result.getRows().get(0);
                String accountNo = ara.get(result.getMetaData().getColumnIndex(Fields.ACCOUNT_NO)).toString().trim();
                amxMessage.setAccountNo(accountNo);
                String actualBalance = getActualBalance(result);
                String availableBalance = getAvailableBalance(result);
                amxMessage.setAvailableBalance(availableBalance);
                amxMessage.setActualBalance(actualBalance);
            }

        } catch (Exception e) {
            log.error("Exception in ToEMFHandler  for Amx." + e.getMessage());
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
}
