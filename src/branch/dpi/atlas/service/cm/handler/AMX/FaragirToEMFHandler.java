package branch.dpi.atlas.service.cm.handler.AMX;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.iso.ISOUtil;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: July 27, 2019
 * Time: 11:37 PM
 */

public class FaragirToEMFHandler extends CMHandlerBase {
    private static Log log = LogFactory.getLog(FaragirToEMFHandler.class);

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        log.debug("Inside ToEMFHandler:process()");
        try {
            String actionCode = null;
            CMCommand command = null;
            CMResultSet result = null;
            AmxMessage amxMessage=null;
            try {
                amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
                command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
                result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);
            } catch (Exception e) {
                System.out.println("==In FaragirToEMFHandler - after making command & result object  - e = " + e);
            }
            try {
                if (result != null)
                    actionCode = result.getHeaderField(Fields.ACTION_CODE);
                else
                    actionCode = msg.getAttributeAsString(Fields.ACTION_CODE);
            } catch (Exception e) {
                System.out.println("Inside FaragirToEMFHandler after get action c- e = " + e);
            }
            log.debug("actionCode:" + actionCode);

            if (actionCode == null)
                actionCode = ActionCode.GENERAL_ERROR;


            try {
                command.addHeaderParam(Fields.ACTION_CODE, actionCode);
                msg.setAttribute(Fields.ACTION_CODE, actionCode);
            } catch (Exception e) {
                System.out.println("In FaragirToEMFHandler - after setting action code on msg & command object- e = " + e);
            }
            try {
                if ((!command.getCommandName().equals(TJCommand.CMD_FOLLOW_UP_SAFE_BOX) ||
                        !command.getCommandName().equals(TJCommand.CMD_FOLLOW_UP_MARHOONAT_INSURANCE)) && actionCode.charAt(0) != '0') {
                    msg.setAttribute(CMMessage.RESPONSE, command);
                    return;
                }
            } catch (Exception e) {
                System.out.println("In FaragirToEMFHandler - after setting non authorize response on msg object - e = " + e);
            }
            try {
                result.moveFirst();
                result.next();
                String requestType= command.getParam(Fields.REQUEST_TYPE);
                if(requestType==null || requestType.trim().equalsIgnoreCase(""))
                    requestType="";
                if ((command.getCommandName().equals(TJCommand.CMD_FUND_TRANSFER_SAFE_BOX) ||
                        command.getCommandName().equals(TJCommand.CMD_FUND_TRANSFER_MARHOONAT_INSURANCE)) &&
                        !requestType.equalsIgnoreCase(Constants.STAMP_DOCUMENT)) {
                    if ((msg.getAttribute(Constants.SRC_HOST_ID).equals(Constants.HOST_ID_CFS) ||
                            msg.getAttribute(Constants.SRC_HOST_ID).equals(Constants.HOST_ID_FARAGIR)) &&
                            msg.getAttribute(Constants.DEST_HOST_ID).equals(Constants.HOST_ID_SGB)) {
                        command.addHeaderParam(Fields.ACTION_CODE, ActionCode.TRANSACTION_PENDING);
                        command.addHeaderParam(Fields.ACTION_MESSAGE, ActionCodeMsg.PENDING);
                        msg.setAttribute(Fields.ACTION_CODE, ActionCode.TRANSACTION_PENDING);
                    }
                }else  if (command.getCommandName().equals(TJCommand.CMD_FOLLOW_UP_SAFE_BOX) ||
                        command.getCommandName().equals(TJCommand.CMD_FOLLOW_UP_MARHOONAT_INSURANCE)) {
                    if(actionCode.equalsIgnoreCase(ActionCode.TRANSACTION_NOT_FOUND))
                        actionCode=ActionCode.FLW_HAS_NO_ORIGINAL;
                    msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, actionCode);
                    command.addHeaderParam(Fields.ACTION_CODE, ActionCode.APPROVED);
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.APPROVED);

                }else if (command.getCommandName().equals(TJCommand.CMD_MARHOONAT_INSURANCE_BALANCE)) {
                    String availableBalance = result.getString("AVAILABLEBALANCE");
                    availableBalance = ISOUtil.zeropad(ISOUtil.zeroUnPad(availableBalance), 17);
                    String availableDebitCredit = (result.getString( Constants.AVAILBLE_BAL_SIGN.toUpperCase()).equals("0") ? "+" : "-");

                    String actualBalance = result.getString("LEDGERBALANCE");
                    actualBalance = ISOUtil.zeropad(ISOUtil.zeroUnPad(actualBalance), 17);
                    String debitCredit = (result.getString(Constants.CR_DB).equals(Constants.CREDIT) ? "+" : "-");

                    amxMessage.setAvailableBalance(availableDebitCredit + availableBalance);
                    amxMessage.setActualBalance(debitCredit + actualBalance);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Result = " + result);
                System.out.println("Result.Metadata = " + result.getMetaData());
                System.out.println("FaragirToEMFHandler -- e.getMessage() = " + e.getMessage() + " --- Result :: " + result);
                System.out.println("Command = " + command.getCommandName() + " while result = " + result);
                throw new CMFault(e.getMessage());
            }
            try {
                msg.setAttribute(CMMessage.RESPONSE, command);
            } catch (Exception e) {
                System.out.println(" *****6666 - FaragirToEMFHandler - e = " + e);
            }
        } catch (Exception ex) {
            log.error(ex);
            ex.printStackTrace();
        }
    }
}
