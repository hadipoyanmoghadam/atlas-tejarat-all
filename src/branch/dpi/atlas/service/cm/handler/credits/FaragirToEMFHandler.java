package branch.dpi.atlas.service.cm.handler.credits;

import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
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
 * User: SH.Behnaz
 * Date: October 04, 2017
 * Time: 9:24:03 PM
 */
public class FaragirToEMFHandler extends CMHandlerBase {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CreditsMessage creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

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
            if (!command.getCommandName().equals(TJCommand.CMD_CREDITS_FOLLOWUP) && !actionCode.startsWith("00"))
                return;

            result.moveFirst();
            result.next();

            if (commandName.equals(TJCommand.CMD_CREDITS_FOLLOWUP)) {
                if(actionCode.equalsIgnoreCase(ActionCode.TRANSACTION_NOT_FOUND))
                    actionCode=ActionCode.FLW_HAS_NO_ORIGINAL;
                msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, actionCode);
                command.addHeaderParam(Fields.ACTION_CODE, ActionCode.APPROVED);
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.APPROVED);

            }else if (commandName.equals(TJCommand.CMD_CREDITS_BALANCE)) {
                String availableBalance = result.getString("AVAILABLEBALANCE");
                availableBalance = ISOUtil.zeropad(ISOUtil.zeroUnPad(availableBalance), 17);
                String availableDebitCredit = (result.getString( Constants.AVAILBLE_BAL_SIGN.toUpperCase()).equals("0") ? "+" : "-");

                String actualBalance = result.getString("LEDGERBALANCE");
                actualBalance = ISOUtil.zeropad(ISOUtil.zeroUnPad(actualBalance), 17);
                String debitCredit = (result.getString(Constants.CR_DB).equals(Constants.CREDIT) ? "+" : "-");

                creditsMessage.setAvailableBalance(availableDebitCredit+availableBalance);
                creditsMessage.setActualBalance(debitCredit+actualBalance);
                creditsMessage.setAccountGroup(result.getString("ACCOUNTGROUP"));
                creditsMessage.setBlockedAmount(ISOUtil.zeropad(result.getString("BLOCKAMOUNT"), 18));
                creditsMessage.setAccountStatus(result.getString("ACCOUNTSTATUS"));
                creditsMessage.setIssuerBranchCode(result.getString("BRANCHCODE"));

            } else if (commandName.equals(TJCommand.CMD_CREDITS_PICHACK_CHECK_STATUS)) {

                String checkStatus = result.getString("CHEQUESTATUS");

                if (checkStatus.equalsIgnoreCase("0")) {
                    command.addHeaderParam(Fields.ACTION_CODE, ActionCode.CHEQUE_NUMBER_NOT_FOUND);
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHEQUE_NUMBER_NOT_FOUND);
                } else {
                    creditsMessage.setCheckStatus(checkStatus);
                    creditsMessage.setCheckDescription(result.getString("DESC"));
                }

            }else if (commandName.equals(TJCommand.CMD_CREDITS_STATEMENT)) {

                creditsMessage.setTransactionCount(String.valueOf(result.size()));
                StringBuilder sb = new StringBuilder();
                ArrayList ara;
                result.moveFirst();
                result.next();              // add to live  971215@@@@
                if (((ArrayList) result.getRows().get(0)).get(result.getMetaData().getColumnIndex("AMOUNT")).equals('0') &&
                        ((ArrayList) result.getRows().get(0)).get(result.getMetaData().getColumnIndex("OPCODE")).equals('0')){
                    creditsMessage.setTransactionCount("0");
            }else{

                    for (int i = 0; i < result.size(); i++) {
                        ara = (ArrayList) result.getRows().get(i);
                        sb.append(((String) ara.get(result.getMetaData().getColumnIndex("DATE"))))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("TIME")), creditsMessage.TX_TIME, '0'))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("AMOUNT")), creditsMessage.AMOUNT, '0'));
                        String branchDocNo = (String) ara.get(result.getMetaData().getColumnIndex("REF-NO"));
                        if (branchDocNo.trim().length() == 7)
                            branchDocNo = branchDocNo.substring(1);
                        sb.append(ISOUtil.padleft(branchDocNo, creditsMessage.TRANSACTION_DOCUMENT_NUMBER, '0'))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("OPCODE")), creditsMessage.OPERATION_CODE, '0'))
                                .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("DATETIME")).toString().substring(0, 8), creditsMessage.Effective_Date, '0'))
                                .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("DATETIME")).toString().substring(8, 14), creditsMessage.Effective_Time, '0'))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("CRDB")), creditsMessage.CREDIT_DEBIT, ' '))
                                .append(((String) ara.get(result.getMetaData().getColumnIndex("BRANCHNO"))).substring(1))
                                .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("DESC")).toString().trim(), creditsMessage.EXTRA_INFO, ' '))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("LASTAMOUNT")), creditsMessage.AMOUNT, '0'))
                                .append(ISOUtil.padleft("0", creditsMessage.FROM_SEQUENCE, '0'));
                        String payId1=(String) ara.get(result.getMetaData().getColumnIndex("PAYID1"));
                        payId1=ISOUtil.zeroUnPad(payId1);
                        sb.append(ISOUtil.padleft(payId1, creditsMessage.ID1, ' '));

                    } }
                    creditsMessage.setResponse(sb.toString());

            }else  {
                String balance = result.getString("ACCOUNTBALANCE");
                balance = ISOUtil.zeropad(ISOUtil.zeroUnPad(balance), 17);
                String debitCredit = (result.getString( Constants.CR_DB).equals("0") ? "+" : "-");
                creditsMessage.setActualBalance(debitCredit + balance);

            }

        } catch (Exception e) {
            log.error("ERROR :::Inside FaragirToEMFHandler.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

