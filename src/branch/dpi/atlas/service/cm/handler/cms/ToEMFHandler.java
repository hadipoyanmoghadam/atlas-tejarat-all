package branch.dpi.atlas.service.cm.handler.cms;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: June 8, 2015
 * Time: 10:08:15 AM
 */
public class ToEMFHandler extends TJServiceHandler {


    public void doProcess(CMMessage msg, Map holder) throws CMFault {


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
            if (command.getCommandName().equals(TJCommand.CMD_CMS_BALANCE)) {

                msg.setAttribute(Fields.CENTER_CREDIT_ACCOUNT, result.getHeaderField(Fields.CENTER_CREDIT_ACCOUNT));
                msg.setAttribute(Fields.ACTUAL_BALANCE, result.getHeaderField(Constants.ACTUAL_AMOUNT));
            }else if (command.getCommandName().equals(TJCommand.CMD_CMS_BALANCE_BATCH)) {
                msg.setAttribute(Fields.CENTER_CREDIT_ACCOUNT, result.getHeaderField(Fields.CENTER_CREDIT_ACCOUNT));
                msg.setAttribute(Fields.TRANS_AMOUNT, result.getHeaderField(Fields.AMOUNT));
                msg.setAttribute(Fields.TRANS_DATE, result.getHeaderField(Fields.TRANS_DATE));
                msg.setAttribute(Fields.BRANCH_DOC_NO, result.getHeaderField(Fields.BRANCH_DOC_NO));
            }else if (command.getCommandName().equals(TJCommand.CMD_CMS_CROUPCARD_DCHARGE) || command.getCommandName().equals(TJCommand.CMD_CMS_CROUPCARD_ALL_DCHARGE)) {
                msg.setAttribute(Fields.AMOUNT, result.getHeaderField(Fields.AMOUNT));

            }


        } catch (Exception e) {
            log.error("Exception in ToEMFHandler  for CMS." + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, ActionCode.GENERAL_ERROR);
        }
    }

}
