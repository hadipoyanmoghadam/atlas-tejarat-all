package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementTx;
import branch.dpi.atlas.service.cm.handler.pg.accountStatement.AccountStatementTxList;
import branch.dpi.atlas.service.cm.handler.pg.cardStatement.StatementTx;
import branch.dpi.atlas.service.cm.handler.pg.cardStatement.StatementTxList;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.Transaction;
import branch.dpi.atlas.service.cm.handler.pg.chargeReport.TransactionList;
import branch.dpi.atlas.service.cm.handler.pg.wfp.WFPTransaction;
import branch.dpi.atlas.service.cm.handler.pg.wfp.WFPTransactionList;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: September 11, 2017
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
            if (command.getCommandName().equals(TJCommand.CMD_CARD_BALANCE)) {

                String availableAmount = result.getHeaderField(Constants.AVAILABLE_AMOUNT);
                String actualAmount = result.getHeaderField(Constants.ACTUAL_AMOUNT);

                if (availableAmount != null && !availableAmount.equals("") && availableAmount.startsWith("-"))
                    availableAmount = availableAmount.substring(1);

                if (actualAmount != null && !actualAmount.equals("") && actualAmount.startsWith("-"))
                    actualAmount = actualAmount.substring(1);

                msg.setAttribute(Fields.ACTUAL_BALANCE, actualAmount);
                msg.setAttribute(Fields.ACTUAL_BALANCE_SIGN, result.getHeaderField(Constants.DebitCredit));
                msg.setAttribute(Fields.AVAILABLE_BALANCE, availableAmount);
                msg.setAttribute(Fields.AVAILABLE_BALANCE_SIGN, result.getHeaderField(Constants.AvailableDebitCredit));
            } else if (command.getCommandName().equals(TJCommand.CMD_CARD_STATEMENT)) {

                String transCount = String.valueOf(result.size());

                StatementTx tx;
                List<StatementTx> statementTx = new ArrayList<StatementTx>();
                ArrayList ara;
                result.moveFirst();
                result.next();
                for (int i = 0; i < result.size(); i++) {
                    ara = (ArrayList) result.getRows().get(i);
                    tx = new StatementTx();
                    tx.setAcquirerId(((String) ara.get(result.getMetaData().getColumnIndex("acquirer"))).trim());
                    tx.setAmount(((String) ara.get(result.getMetaData().getColumnIndex("amount"))).trim());
                    tx.setCrdb(((String) ara.get(result.getMetaData().getColumnIndex("cRDB"))).trim());
                    tx.setDate(ara.get(result.getMetaData().getColumnIndex("creationDate")).toString());
                    tx.setDocNo(((String) ara.get(result.getMetaData().getColumnIndex("branchDocNo"))).trim());
                    tx.setOpCode(((String) ara.get(result.getMetaData().getColumnIndex("sgbActionCode"))).trim());
                    tx.setRow(String.valueOf(i));
                    tx.setTime(ara.get(result.getMetaData().getColumnIndex("creationTime")).toString());
                    statementTx.add(tx);
                }
                StatementTxList list = new StatementTxList();
                list.setTx(statementTx);
                msg.setAttribute(Fields.TRANSACTION, list);
                msg.setAttribute(Fields.TRANS_COUNT, transCount);
            } else if (command.getCommandName().equals(TJCommand.CMD_CMS_CHARGE_TRANSACTION)) {

                Transaction tx;
                List list = new ArrayList();
                TransactionList txList = new TransactionList();
                ArrayList ara;
                result.moveFirst();
                result.next();
                for (int i = 0; i < result.size(); i++) {
                    ara = (ArrayList) result.getRows().get(i);
                    tx = new Transaction();
                    tx.setAmount(((String) ara.get(result.getMetaData().getColumnIndex("amount"))).trim());
//                    tx.setCrdb(((String) ara.get(result.getMetaData().getColumnIndex("cRDB"))).trim());
                    tx.setDate(ara.get(result.getMetaData().getColumnIndex("creationDate")).toString());
                    tx.setDocNo(((String) ara.get(result.getMetaData().getColumnIndex("branchDocNo"))).trim());
                    tx.setTime(ara.get(result.getMetaData().getColumnIndex("creationTime")).toString());
                    list.add(tx);
                }
                txList.setTrans(list);
                msg.setAttribute(Fields.TRANSACTION, txList);
            } else if (command.getCommandName().equals(TJCommand.CMD_CMS_WFP_STATEMENT)) {

                WFPTransaction tx;
                List list = new ArrayList();
                WFPTransactionList txList = new WFPTransactionList();
                ArrayList ara;
                result.moveFirst();
                result.next();
                for (int i = 0; i < result.size(); i++) {
                    ara = (ArrayList) result.getRows().get(i);
                    tx = new WFPTransaction();
                    tx.setAmount(((String) ara.get(result.getMetaData().getColumnIndex("amount"))).trim());
                    tx.setDate(ara.get(result.getMetaData().getColumnIndex("creationDate")).toString());
                    tx.setDocNo(((String) ara.get(result.getMetaData().getColumnIndex("branchDocNo"))).trim());
                    tx.setTime(ara.get(result.getMetaData().getColumnIndex("creationTime")).toString());
                    tx.setOpCode(ara.get(result.getMetaData().getColumnIndex("sgbActionCode")).toString());
                    tx.setBranchCode(ara.get(result.getMetaData().getColumnIndex("sgbBranchId")).toString());
                    list.add(tx);

                }
                txList.setTrans(list);
                msg.setAttribute(Fields.TRANSACTION, txList);

            }else if (command.getCommandName().equals(TJCommand.CMD_CMS_CROUPCARD_DCHARGE) || command.getCommandName().equals(TJCommand.CMD_CMS_CROUPCARD_ALL_DCHARGE)) {
                msg.setAttribute(Fields.AMOUNT, result.getHeaderField(Fields.AMOUNT));

            } else if (command.getCommandName().equals(TJCommand.CMD_GROUP_ACCOUNT_STATEMENT)) {

                String transCount = String.valueOf(result.size());

                AccountStatementTx tx;
                List<AccountStatementTx> statementTx = new ArrayList<AccountStatementTx>();
                ArrayList ara;
                result.moveFirst();
                result.next();
                for (int i = 0; i < result.size(); i++) {
                    ara = (ArrayList) result.getRows().get(i);
                    tx = new AccountStatementTx();
                    tx.setOpCode(((String) ara.get(result.getMetaData().getColumnIndex("sgbActionCode"))).trim());
                    tx.setAmount(((String) ara.get(result.getMetaData().getColumnIndex("amount"))).trim());
                    tx.setCrdb(((String) ara.get(result.getMetaData().getColumnIndex("cRDB"))).trim());
                    tx.setDate(ara.get(result.getMetaData().getColumnIndex("creationDate")).toString());
                    tx.setTime(ara.get(result.getMetaData().getColumnIndex("creationTime")).toString());
                    tx.setDocNo(((String) ara.get(result.getMetaData().getColumnIndex("branchDocNo"))).trim());
                    tx.setBrCode(((String) ara.get(result.getMetaData().getColumnIndex("sgbBranchId"))).trim());
                    tx.setLastBal(((String) ara.get(result.getMetaData().getColumnIndex("src_account_balance"))).trim());
                    tx.setCardNo(((String) ara.get(result.getMetaData().getColumnIndex("cardNo"))).trim());
                    tx.setRow(String.valueOf(i));
                    statementTx.add(tx);
                }
                AccountStatementTxList list = new AccountStatementTxList();
                list.setTx(statementTx);
                msg.setAttribute(Fields.TRANSACTION, list);
                msg.setAttribute(Fields.TRANS_COUNT, transCount);

            }if (command.getCommandName().equals(TJCommand.CMD_FOLLOW_UP_PG)) {
                String followactionCode = result.getHeaderField(Fields.FOLLOWUP_ACTION_CODE);
                msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, followactionCode);
                msg.setAttribute(Fields.ACTION_CODE, followactionCode);
            }


        } catch (Exception e) {
            log.error("Exception in ToEMFHandler  for CMS." + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, ActionCode.GENERAL_ERROR);
        }
    }

}
