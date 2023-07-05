package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.ib.format.CMCommand;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.util.Constants;

import java.util.Map;
import java.util.ArrayList;

import org.jpos.iso.ISOUtil;
import org.jpos.iso.ISOException;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: Apr 24, 2013
 * Time: 11:08:15 AM
 */
public class ToEMFHandler extends TJServiceHandler {


    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
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
            if (command.getCommandName().equals(TJCommand.CMD_BRANCH_BALANCE)) {
                ArrayList ara = (ArrayList) result.getRows().get(0);
                String accountNo = ara.get(result.getMetaData().getColumnIndex(Fields.ACCOUNT_NO)).toString().trim();
                branchMsg.setAccountNo(accountNo);
                String actualBalance = getActualBalance(result);
                String availableBalance = getAvailableBalance(result);
                // Get Blocked Amount
                String blockedAmount = result.getString(Fields.TOTAL_BLOCKED_AMOUNT);
                blockedAmount = ISOUtil.zeropad(blockedAmount, 18);
                //Get Account Status
                String accountStatus = result.getHeaderField(Fields.ACCOUNT_STATUS);
                branchMsg.setAvailableBalance(availableBalance);
                branchMsg.setActualBalance(actualBalance);
                branchMsg.setBlockedAmount(blockedAmount);
                branchMsg.setAccountStatus(accountStatus);
            } else if (command.getCommandName().equals(TJCommand.CMD_BRANCH_DEPOSIT) ||
                    command.getCommandName().equals(TJCommand.CMD_BRANCH_WITHDRAW)) {
                // Get Actual Balance
                String actualBalance = getActualBalance(result);
                branchMsg.setActualBalance(actualBalance);
            }else if (command.getCommandName().equals(TJCommand.CMD_BRANCH_DISCHARGE_GIFTCARD)) {
                String availableBalance = result.getHeaderField(Fields.AMOUNT);
                branchMsg.setAvailableBalance(availableBalance);

                String branchDocNo = result.getHeaderField(Fields.DISCAHRGE_DOCNO);
                branchMsg.setDocumentNo(branchDocNo);

            } else if (command.getCommandName().equals(TJCommand.CMD_BRANCH_FOLLOWUP)) {
                String followactionCode = result.getHeaderField(Fields.FOLLOWUP_ACTION_CODE);
                msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, followactionCode);
            } else if (command.getCommandName().equals(TJCommand.CMD_BRANCH_CHANGE_ACCOUNT_STATUS)  || command.getCommandName().equals(TJCommand.CMD_SIMIN_CHANGE_ACCOUNT_STATUS)) {
                String blockRow = result.getHeaderField(Fields.BLOCK_ROW);
                if (blockRow != null)
                    msg.setAttribute(Fields.BLOCK_ROW, blockRow);
            } else if (command.getCommandName().equals(TJCommand.CMD_BRANCH_STATEMENT) && command.getParam(Fields.REQUEST_TYPE).equalsIgnoreCase(Constants.STATEMENT_ALL)) {
//                result.dump(System.out);
                branchMsg.setTransactionCount(String.valueOf(result.size()));
                StringBuilder sb=new StringBuilder();
                ArrayList ara;
                result.moveFirst();
                result.next();
                for (int i = 0; i < result.size(); i++) {
                    ara = (ArrayList) result.getRows().get(i);
                    sb.append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigDate")), branchMsg.TX_DATE, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigTime")), branchMsg.TX_TIME, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("amount")), branchMsg.AMOUNT, '0'));
                    String branchDocNo=(String) ara.get(result.getMetaData().getColumnIndex("branchDocNo"));
                    if(branchDocNo.trim().length()==7)
                        branchDocNo=branchDocNo.substring(1);
                    sb.append(ISOUtil.padleft(branchDocNo, branchMsg.TRANSACTION_DOCUMENT_NUMBER, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("sgbActionCode")), branchMsg.OPERATION_CODE, '0'))
                    .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationDate")).toString(), branchMsg.Effective_Date, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("cRDB")), branchMsg.CREDIT_DEBIT, ' '))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("sgbBranchId")), branchMsg.ISSUER_BRANCH_CODE, '0'))
                    .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("description")).toString().trim(), branchMsg.DOCUMENT_DESCRIPTION, ' '))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("src_account_balance")), branchMsg.AMOUNT, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("rRN")), branchMsg.FROM_SEQUENCE, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("merchantTerminalId")), branchMsg.TERMINAL_ID, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("hostCode")), branchMsg.HOST_CODE, ' '))
                    .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationTime")).toString(), branchMsg.Effective_Time, '0'));

                }
                branchMsg.setResponse(sb.toString());
            }else if (command.getCommandName().equals(TJCommand.CMD_BRANCH_STATEMENT) && command.getParam(Fields.REQUEST_TYPE).equalsIgnoreCase(Constants.STATEMENT_COUNT)) {
                String tranCount = result.getHeaderField(Constants.TRAN_COUNT);
                branchMsg.setTransactionCount(tranCount.trim());
            } else if (command.getCommandName().equals(TJCommand.CMD_BRANCH_STATEMENT) && command.getParam(Fields.REQUEST_TYPE).equalsIgnoreCase(Constants.FULL_STATEMENT)) {
//                result.dump(System.out);
                branchMsg.setTransactionCount(String.valueOf(result.size()));
                StringBuilder sb=new StringBuilder();
                ArrayList ara;
                result.moveFirst();
                result.next();
                for (int i = 0; i < result.size(); i++) {
                    ara = (ArrayList) result.getRows().get(i);
                    sb.append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigDate")), branchMsg.TX_DATE, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigTime")), branchMsg.TX_TIME, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("amount")), branchMsg.AMOUNT, '0'));
                    String branchDocNo=(String) ara.get(result.getMetaData().getColumnIndex("branchDocNo"));
                    if(branchDocNo.trim().length()==7)
                        branchDocNo=branchDocNo.substring(1);
                    sb.append(ISOUtil.padleft(branchDocNo, branchMsg.TRANSACTION_DOCUMENT_NUMBER, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("sgbActionCode")), branchMsg.OPERATION_CODE, '0'))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationDate")).toString(), branchMsg.Effective_Date, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("cRDB")), branchMsg.CREDIT_DEBIT, ' '))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("sgbBranchId")), branchMsg.ISSUER_BRANCH_CODE, '0'))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("description")).toString().trim(), branchMsg.EXTRA_INFO, ' '))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("src_account_balance")), branchMsg.AMOUNT, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("rRN")), branchMsg.FROM_SEQUENCE, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("merchantTerminalId")), branchMsg.TERMINAL_ID, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("hostCode")), branchMsg.HOST_CODE, ' '))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationTime")).toString(), branchMsg.Effective_Time, '0'))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("messageSeq")).toString(), branchMsg.FOLLOW_NO, ' '))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("id1")).toString(), branchMsg.ID1, ' '))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("id2")).toString(), branchMsg.ID2, ' '));

                }
                branchMsg.setResponse(sb.toString());
            }else if (command.getCommandName().equals(TJCommand.CMD_BRANCH_CANCELLATION_GIFTCARD)){
                String cancellationAmount = result.getHeaderField(Fields.CANCELLATION_AMOUNT);
                branchMsg.setAmount(cancellationAmount);
            }else if (command.getCommandName().equals(TJCommand.CMD_SIMIN_BALANCE)) {
                ArrayList ara = (ArrayList) result.getRows().get(0);
                String accountNo = ara.get(result.getMetaData().getColumnIndex(Fields.ACCOUNT_NO)).toString().trim();
                branchMsg.setAccountNo(accountNo);
                //Get Account title
                String accountTitle = result.getHeaderField(Fields.ACCOUNT_TITLE);
                //Get Account Type
                String accountType = result.getHeaderField(Fields.ACCOUNT_TYPE);
                //Get First name
                String firstName = result.getHeaderField(Constants.FIRST_NAME);
                //Get Last Name
                String lastName = result.getHeaderField(Constants.LAST_NAME);
                //Get Account Status
                String accountStatus = result.getHeaderField(Fields.ACCOUNT_STATUS);
                //Get sgb branch id
                String sgbBranchId = result.getHeaderField(Fields.BRANCH_CODE);
                String actualBalance = getActualBalance(result);
                String availableBalance = getAvailableBalance(result);

                branchMsg.setAccountType(accountTitle);
                branchMsg.setAccountGroup(accountType);
                branchMsg.setFirstName(firstName);
                branchMsg.setLastName(lastName);
                branchMsg.setAccountStatus(accountStatus);
                branchMsg.setIssuerBranchCode(sgbBranchId);
                branchMsg.setAvailableBalance(availableBalance);
                branchMsg.setActualBalance(actualBalance);
            }
            else if (command.getCommandName().equals(TJCommand.CMD_SIMIN_STATEMENT)) {
//                result.dump(System.out);
                String count=String.valueOf(result.size());
                branchMsg.setTransactionCount(String.valueOf(result.size()));
                StringBuilder sb = new StringBuilder();
                ArrayList ara;
                result.moveFirst();
                result.next();
                for (int i = 0; i < result.size(); i++) {
                    ara = (ArrayList) result.getRows().get(i);

                    String branchDocNo = (String) ara.get(result.getMetaData().getColumnIndex("branchDocNo"));
                    if (branchDocNo.trim().length() == 7)
                        branchDocNo = branchDocNo.substring(1);


                        sb.append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationDate")).toString(), branchMsg.Effective_Date, '0'))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("description")).toString().trim(), branchMsg.EXTRA_INFO, ' '))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("amount")), branchMsg.AMOUNT, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("cRDB")), branchMsg.CREDIT_DEBIT, ' '))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("src_account_balance")), branchMsg.AMOUNT, '0'))
                            .append(ISOUtil.padleft(branchDocNo, branchMsg.TRANSACTION_DOCUMENT_NUMBER, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("sgbActionCode")), branchMsg.OPERATION_CODE, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("sgbBranchId")), branchMsg.ISSUER_BRANCH_CODE, '0'))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigDate")), branchMsg.TX_DATE, '0'))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigTime")), branchMsg.TX_TIME, '0'));


                }
               branchMsg.setResponse(sb.toString());
            }else if (command.getCommandName().equals(TJCommand.CMD_PAYA_DEPOSIT)){
            branchMsg.setTrackId(result.getHeaderField(Fields.TRACK_ID));
            }else if(command.getCommandName().equals(TJCommand.CMD_SIMIN_CHANGE_CBI_STATUS))
            {
                String blockedAmount = result.getHeaderField(Fields.BLOCK_AMOUNT);
                branchMsg.setBlockedAmount(blockedAmount);
                String blockStatus= result.getHeaderField(Fields.STATUS);
                branchMsg.setAccountStatus(blockStatus);
            }

        } catch (ISOException e) {
            log.error("Exception in ToEMFHandler  for Branch." + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE,ActionCode.GENERAL_ERROR);
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
