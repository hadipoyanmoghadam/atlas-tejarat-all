package branch.dpi.atlas.service.cm.handler.credits;

import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
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
 * Date: Sep 4, 2017
 * Time: 03:01 PM
 */
public class ToEMFHandler extends TJServiceHandler {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        CreditsMessage creditsMessage = (CreditsMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
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
            if (command.getCommandName().equals(TJCommand.CMD_CREDITS_BALANCE)) {
                ArrayList ara = (ArrayList) result.getRows().get(0);
                String accountNo = ara.get(result.getMetaData().getColumnIndex(Fields.ACCOUNT_NO)).toString().trim();
                creditsMessage.setAccountNo(accountNo);
                String actualBalance = getActualBalance(result);
                String availableBalance = getAvailableBalance(result);
                // Get Blocked Amount
                String blockedAmount = result.getString(Fields.TOTAL_BLOCKED_AMOUNT);
                blockedAmount = ISOUtil.zeropad(blockedAmount, 18);
                //Get Account Status
                String accountStatus = result.getHeaderField(Fields.ACCOUNT_STATUS);
                creditsMessage.setAvailableBalance(availableBalance);
                creditsMessage.setActualBalance(actualBalance);
                creditsMessage.setBlockedAmount(blockedAmount);
                creditsMessage.setAccountStatus(accountStatus);
            } else if (command.getCommandName().equals(TJCommand.CMD_CREDITS_DEPOSIT) ||
                    command.getCommandName().equals(TJCommand.CMD_CREDITS_WITHDRAW)) {
                // Get Actual Balance
                String actualBalance = getActualBalance(result);
                creditsMessage.setActualBalance(actualBalance);
                // Get Tx_pk
                creditsMessage.setTx_pk(command.getHeaderParam(Fields.REFRENCE_NUMBER));

            } else if (command.getCommandName().equals(TJCommand.CMD_CREDITS_DEPOSIT_REVERSAL) ||
                    command.getCommandName().equals(TJCommand.CMD_CREDITS_WITHDRAW_REVERSAL)) {
                // Get Tx_pk
                creditsMessage.setTx_pk(result.getHeaderField(Fields.REFRENCE_NUMBER));

            } else if (command.getCommandName().equals(TJCommand.CMD_CREDITS_FOLLOWUP)) {
                String followactionCode = result.getHeaderField(Fields.FOLLOWUP_ACTION_CODE);
                msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, followactionCode);
            } else if (command.getCommandName().equals(TJCommand.CMD_CREDITS_STATEMENT) && command.getParam(Fields.REQUEST_TYPE).equalsIgnoreCase(Constants.FULL_STATEMENT)) {
//                result.dump(System.out);
                creditsMessage.setTransactionCount(String.valueOf(result.size()));
                StringBuilder sb = new StringBuilder();
                ArrayList ara;
                result.moveFirst();
                result.next();
                for (int i = 0; i < result.size(); i++) {
                    ara = (ArrayList) result.getRows().get(i);
                    sb.append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigDate")), creditsMessage.TX_DATE, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigTime")), creditsMessage.TX_TIME, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("amount")), creditsMessage.AMOUNT, '0'));
                    String branchDocNo = (String) ara.get(result.getMetaData().getColumnIndex("branchDocNo"));
                    if (branchDocNo.trim().length() == 7)
                        branchDocNo = branchDocNo.substring(1);
                    sb.append(ISOUtil.padleft(branchDocNo, creditsMessage.TRANSACTION_DOCUMENT_NUMBER, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("sgbActionCode")), creditsMessage.OPERATION_CODE, '0'))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationDate")).toString(), creditsMessage.Effective_Date, '0'))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationTime")).toString(), creditsMessage.Effective_Time, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("cRDB")), creditsMessage.CREDIT_DEBIT, ' '))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("sgbBranchId")), creditsMessage.ISSUER_BRANCH_CODE, '0'))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("description")).toString().trim(), creditsMessage.EXTRA_INFO, ' '))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("src_account_balance")), creditsMessage.AMOUNT, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("rRN")), creditsMessage.FROM_SEQUENCE, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("id1")), creditsMessage.ID1, ' '));

                }
                creditsMessage.setResponse(sb.toString());
            }
        } catch (ISOException e) {
            log.error("Exception in ToEMFHandler  for Branch." + e.getMessage());
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
