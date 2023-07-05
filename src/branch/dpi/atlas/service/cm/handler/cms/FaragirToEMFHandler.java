package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.jpos.iso.ISOUtil;

import java.util.ArrayList;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: June 29, 2015
 * Time: 10:04:03 PM
 */
public class FaragirToEMFHandler extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

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
            if (!actionCode.startsWith("00"))
                return;

            result.moveFirst();
            result.next();

           CMCommand command=(CMCommand) msg.getAttribute(CMMessage.REQUEST);
            String commandName = command.getCommandName();

            if (commandName.equals(TJCommand.CMD_SIMIN_CHANGE_ACCOUNT_STATUS)) {
            }else if (commandName.equals(TJCommand.CMD_SIMIN_BALANCE)) {

                String availableBalance = result.getString("BAL");
                availableBalance = ISOUtil.zeropad(ISOUtil.zeroUnPad(availableBalance), 17);
                String availableDebitCredit = (result.getString(Constants.AVAILBLE_BAL_SIGN.toUpperCase()).equals("0") ? "+" : "-");

                String actualBalance = result.getString("LEDGERBALANCE");
                actualBalance = ISOUtil.zeropad(ISOUtil.zeroUnPad(actualBalance), 17);
                String debitCredit = (result.getString(Constants.CR_DB).equals(Constants.CREDIT) ? "+" : "-");

                branchMsg.setAvailableBalance(availableDebitCredit+availableBalance);
                branchMsg.setActualBalance(debitCredit+actualBalance);
                branchMsg.setAccountGroup(result.getString("ACCOUNTGROUP"));
                branchMsg.setAccountType(result.getString("ACCTYPE"));
                branchMsg.setFirstName(result.getString("CUSTOMERNAME"));
                branchMsg.setLastName(result.getString("CUSTOMERFAMILY"));
                branchMsg.setAccountStatus(result.getString("ACCOUNTSTATUS"));
                branchMsg.setIssuerBranchCode(result.getString("BRANCHCODE"));

            }else if (commandName.equals(TJCommand.CMD_SIMIN_STATEMENT)) {

               branchMsg.setTransactionCount(String.valueOf(result.size()));
                StringBuilder sb = new StringBuilder();
                ArrayList ara;
                result.moveFirst();
                result.next();
                if (((ArrayList) result.getRows().get(0)).get(result.getMetaData().getColumnIndex("AMOUNT")).equals("0") &&
                        ((ArrayList) result.getRows().get(0)).get(result.getMetaData().getColumnIndex("OPCODE")).equals("000")){
                    branchMsg.setTransactionCount("0");
                }else{

                    for (int i = 0; i < result.size(); i++) {
                        ara = (ArrayList) result.getRows().get(i);
                        String branchDocNo = (String) ara.get(result.getMetaData().getColumnIndex("REF-NO"));
                        if (branchDocNo.trim().length() == 7)
                            branchDocNo = branchDocNo.substring(1);


                             sb.append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("DATETIME")).toString().substring(0, 8), branchMsg.Effective_Date, '0'))
                                .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("DESC")).toString().trim(), branchMsg.EXTRA_INFO, ' '))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("AMOUNT")), branchMsg.AMOUNT, '0'))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("CRDB")), branchMsg.CREDIT_DEBIT, ' '))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("LASTAMOUNT")), branchMsg.AMOUNT, '0'))
                                .append(ISOUtil.padleft(branchDocNo, branchMsg.TRANSACTION_DOCUMENT_NUMBER, '0'))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("OPCODE")), branchMsg.OPERATION_CODE, '0'))
                                .append(((String) ara.get(result.getMetaData().getColumnIndex("BRANCHNO"))).substring(1))
                                .append(((String) ara.get(result.getMetaData().getColumnIndex("DATE"))))
                                .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("TIME")), branchMsg.TX_TIME, '0'))
                          ;

                    } }
                branchMsg.setResponse(sb.toString());

            }else if(commandName.equals(TJCommand.CMD_SIMIN_CHANGE_CBI_STATUS)){
                String blockNo = result.getString("BLCKNO");
                blockNo = ISOUtil.zeropad(ISOUtil.zeroUnPad(blockNo), 13);
                String blockAmount=result.getString("BLOCKAMOUNT");
                blockAmount=ISOUtil.zeropad(ISOUtil.zeroUnPad(blockAmount), 18);
                String status=result.getString("STATUS");

                branchMsg.setBlockRow(blockNo);
                branchMsg.setBlockedAmount(blockAmount);
                branchMsg.setAccountStatus(status);
            }

        } catch (Exception e) {
            log.error("ERROR :::Inside FaragirToEMFHandler.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

