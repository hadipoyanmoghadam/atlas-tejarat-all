package branch.dpi.atlas.service.cm.handler.tourist;

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
 * Date: Apr 24, 2017
 * Time: 04:08 PM
 */
public class ToEMFHandler extends TJServiceHandler {


    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        TouristMessage touristMessage = (TouristMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
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
            if (command.getCommandName().equals(TJCommand.CMD_TOURIST_BALANCE)) {
                String actualBalance = getActualBalance(result);
                String availableBalance = getAvailableBalance(result);
                touristMessage.setAvailableBalance(availableBalance);
                touristMessage.setActualBalance(actualBalance);

            } else if (command.getCommandName().equals(TJCommand.CMD_TOURIST_FOLLOWUP)) {
                String followActionCode = result.getHeaderField(Fields.FOLLOWUP_ACTION_CODE);
                msg.setAttribute(Fields.FOLLOWUP_ACTION_CODE, followActionCode);
            } else if (command.getCommandName().equals(TJCommand.CMD_TOURIST_CARD_STATEMENT)) {
//                result.dump(System.out);
                touristMessage.setTransactionCount(String.valueOf(result.size()));
                StringBuilder sb=new StringBuilder();
                ArrayList ara;
                result.moveFirst();
                result.next();
                String balance="";
                for (int i = 0; i < result.size(); i++) {
                    ara = (ArrayList) result.getRows().get(i);
                    sb.append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigDate")), touristMessage.TX_DATE, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigTime")), touristMessage.TX_TIME, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("amount")), touristMessage.AMOUNT, '0'));
                    String branchDocNo=(String) ara.get(result.getMetaData().getColumnIndex("branchDocNo"));
                    if(branchDocNo.trim().length()==7)
                        branchDocNo=branchDocNo.substring(1);
                    sb.append(ISOUtil.padleft(branchDocNo, touristMessage.TRANSACTION_DOCUMENT_NUMBER, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("sgbActionCode")), touristMessage.OPERATION_CODE, '0'))
                    .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationDate")).toString(), touristMessage.Effective_Date, '0'))
                    .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationTime")).toString(), touristMessage.Effective_Time, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("cRDB")), touristMessage.CREDIT_DEBIT, ' '))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("sgbBranchId")), touristMessage.ISSUER_BRANCH_CODE, '0'))
                    .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("description")).toString().trim(), touristMessage.EXTRA_INFO, ' '));
                    balance=(String) ara.get(result.getMetaData().getColumnIndex("src_account_balance"));
                    if(balance!=null && balance.trim().length()<=touristMessage.AMOUNT)
                        balance=ISOUtil.padleft(balance.trim(), touristMessage.AMOUNT, '0');
                    else
                        balance=ISOUtil.padleft("", touristMessage.AMOUNT, ' ');
                    sb.append(balance)
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("rRN")), touristMessage.FROM_SEQUENCE, '0'))
                    .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("id1")), touristMessage.ID1, ' '));

                }
                touristMessage.setResponse(sb.toString());
            } else if (command.getCommandName().equals(TJCommand.CMD_TOURIST_CHARGE_STATEMENT)) {
//                result.dump(System.out);
                touristMessage.setTransactionCount(String.valueOf(result.size()));
                StringBuilder sb=new StringBuilder();
                ArrayList ara;
                result.moveFirst();
                result.next();
                for (int i = 0; i < result.size(); i++) {
                    ara = (ArrayList) result.getRows().get(i);
                    sb.append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigDate")), touristMessage.TX_DATE, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("txOrigTime")), touristMessage.TX_TIME, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("amount")), touristMessage.AMOUNT, '0'));
                    String branchDocNo=(String) ara.get(result.getMetaData().getColumnIndex("branchDocNo"));
                    if(branchDocNo.trim().length()==7)
                        branchDocNo=branchDocNo.substring(1);
                    sb.append(ISOUtil.padleft(branchDocNo, touristMessage.TRANSACTION_DOCUMENT_NUMBER, '0'))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationDate")).toString(), touristMessage.Effective_Date, '0'))
                            .append(ISOUtil.padleft(ara.get(result.getMetaData().getColumnIndex("creationTime")).toString(), touristMessage.Effective_Time, '0'))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("cRDB")), touristMessage.CREDIT_DEBIT, ' '))
                            .append(ISOUtil.padleft((String) ara.get(result.getMetaData().getColumnIndex("rRN")), touristMessage.FROM_SEQUENCE, '0'));

                }
                touristMessage.setResponse(sb.toString());
            }
        } catch (ISOException e) {
            log.error("Exception in ToEMFHandler  for Toruist." + e.getMessage());
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
