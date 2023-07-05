package dpi.atlas.service.cfs.handler;

import dpi.atlas.model.tj.entity.*;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:Behnaz.sh@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.1 $ $Date: 2013/06/25 14:04:24 $
 */

public class PrepareResponseForCM extends CFSHandlerBase implements Configurable {

    protected String resultID;
    protected String resultType;
    protected String cardType;
    private String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        try {
            log.info(description);
            log.debug(resultID);
            log.debug(resultType);
            CMResultSet result;
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            List tranList;

            if (resultID != null && !resultID.equals("") && holder.containsKey(resultID)) {
                Object accountData;
                if (resultType != null && !resultType.equals("")) { // The result is a collection, not a single entity
                    accountData = holder.get(accountField);
                    tranList = (List) holder.get(resultID);
                    if (resultType.equals(CFSConstants.TX_CLASS))
                        result = new CMResultSet(tranList, Tx.class);
                    else
                        result = new CMResultSet(tranList, Class.forName(resultType));

                    String resultSize = "1";
                    if (holder.containsKey(Constants.RESULT_SIZE))
                        resultSize = (String) holder.get(Constants.RESULT_SIZE);
                    result.setHeaderField(Constants.RESULT_SIZE, resultSize);

                } else {// result is a single entity

                    accountData = holder.get(resultID);
                    result = new CMResultSet(accountData);

                    Object accountHolder = ((AccountData) accountData).getAccountHolder();
                    String sgbBranch_id = "";
                    if (accountHolder instanceof Account) {
                        sgbBranch_id = ((Account) accountHolder).getSgb_branch_id();
                        result.setHeaderField(Fields.ACCOUNT_STATUS, String.valueOf(((Account) accountHolder).getAccountStatus()));
                        result.setHeaderField(Fields.ACCOUNT_TYPE, String.valueOf(((Account) accountHolder).getAccountType()));
                        result.setHeaderField(Fields.ACCOUNT_TITLE, String.valueOf(((Account) accountHolder).getAccountTitle()));
                        result.setHeaderField(Fields.BRANCH_CODE, sgbBranch_id);
                    } else if (accountHolder instanceof Device){
                        sgbBranch_id = ((Device) accountHolder).getBranchId();
                    result.setHeaderField(Fields.BRANCH_CODE, sgbBranch_id);
                }}

                setAccountDataResult(result, accountData, msg.getAttributeAsString(Fields.AMOUNT), (Long) holder.get(Fields.MAX_TRANS_LIMIT));

            } else if (holder.containsKey(Fields.ACCOUNT_NO)) {
                Object accountData = holder.get(Fields.ACCOUNT_NO);
                result = new CMResultSet(accountData);
                setAccountDataResult(result, accountData, msg.getAttributeAsString(Fields.AMOUNT), (Long) holder.get(Fields.MAX_TRANS_LIMIT));
                result.setHeaderField(Constants.RESULT_SIZE, (String) holder.get(Constants.RESULT_SIZE));
            } else if (holder.containsKey(Fields.GIFT_CARD_CLASS)) {
                Object accountData = holder.get(Fields.GIFT_CARD_CLASS);
                result = new CMResultSet(accountData);
                result.setHeaderField(Fields.AMOUNT, ((GiftCard) accountData).getAmount());
                result.setHeaderField(Fields.TRANS_DATE, ((GiftCard) accountData).getDate());
                result.setHeaderField(Fields.BRANCH_DOC_NO, ((GiftCard) accountData).getDocNo());

            } else {
                AccountData accountData = new AccountData("-1", 0, 0, null);
                long amount = 0;
                accountData.setAccountBalance(amount);
                result = new CMResultSet(accountData);
                result.setHeaderField(Constants.RESULT_SIZE, "-1");
                result.setHeaderField(Constants.ACTUAL_AMOUNT, Long.toString(amount));
                result.setHeaderField(Constants.AVAILABLE_AMOUNT, Long.toString(amount));

            }

            String actionCode = msg.getAttributeAsString(CFSConstants.ACTION_CODE);
            result.setHeaderField(Params.ACTION_CODE, actionCode);
            command.getParams().put(CFSConstants.ACTION_CODE, actionCode);
            command.getParams().put("JMSCorrelationID", command.getHeaderParam(Fields.SESSION_ID));

            if (command.getHeaderParam(Fields.MESSAGE_ID) != null) {
                int msg_id = Integer.parseInt(command.getHeaderParam(Fields.MESSAGE_ID)) + 1;
                result.setHeaderField(Fields.MESSAGE_ID, msg_id + "");
            } else {
                log.error("Command does not have Message id in its header,command = " + command);
                throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
            }

            if(msg.getAttribute(Fields.FOLLOWUP_ACTION_CODE)!=null)
                result.setHeaderField(Fields.FOLLOWUP_ACTION_CODE, msg.getAttributeAsString(Fields.FOLLOWUP_ACTION_CODE));
            if(msg.getAttributeAsString(Fields.BLOCK_ROW)!=null)
                result.setHeaderField(Fields.BLOCK_ROW, msg.getAttributeAsString(Fields.BLOCK_ROW));
            if(msg.getAttributeAsString(Constants.TRAN_COUNT)!=null){
                result.setHeaderField(Constants.TRAN_COUNT, msg.getAttributeAsString(Constants.TRAN_COUNT));
            }
            if(msg.getAttribute(Fields.DEST_ACCOUNT_BALANCE)!=null)
                result.setHeaderField(Constants.DEST_ACCOUNT_BALANCE, String.valueOf(msg.getAttribute(Fields.DEST_ACCOUNT_BALANCE)));

            if(msg.getAttributeAsString(Fields.CENTER_CREDIT_ACCOUNT)!=null)
                result.setHeaderField(Fields.CENTER_CREDIT_ACCOUNT, msg.getAttributeAsString(Fields.CENTER_CREDIT_ACCOUNT));
            if(msg.getAttributeAsString(Fields.BATCH_CREDIT_ACCOUNT)!=null)
                result.setHeaderField(Fields.BATCH_CREDIT_ACCOUNT, msg.getAttributeAsString(Fields.BATCH_CREDIT_ACCOUNT));

            if(msg.getAttributeAsString(Fields.CANCELLATION_AMOUNT)!=null){
                result.setHeaderField(Fields.CANCELLATION_AMOUNT, msg.getAttributeAsString(Fields.CANCELLATION_AMOUNT));
            }
            if(msg.getAttributeAsString(Fields.DISCAHRGE_DOCNO)!=null){
                result.setHeaderField(Fields.DISCAHRGE_DOCNO, msg.getAttributeAsString(Fields.DISCAHRGE_DOCNO));
            }
            if(msg.getAttributeAsString(Constants.FIRST_NAME)!=null)
                result.setHeaderField(Constants.FIRST_NAME, msg.getAttributeAsString(Constants.FIRST_NAME));
            if(msg.getAttributeAsString(Constants.LAST_NAME)!=null)
                result.setHeaderField(Constants.LAST_NAME, msg.getAttributeAsString(Constants.LAST_NAME));

            if(msg.getAttributeAsString(Fields.TRACK_ID)!=null)
                result.setHeaderField(Fields.TRACK_ID, msg.getAttributeAsString(Fields.TRACK_ID));

             if(msg.hasAttribute(Fields.CARD_TYPE))
                result.setHeaderField(Fields.CARD_TYPE, msg.getAttributeAsString(Fields.CARD_TYPE));
            if(msg.getAttributeAsString(Fields.BLOCK_AMOUNT)!=null&& msg.getAttributeAsString(Fields.BLOCK_STATUS)!=null)
            {
                result.setHeaderField(Fields.BLOCK_AMOUNT, msg.getAttributeAsString(Fields.BLOCK_AMOUNT));
                result.setHeaderField(Fields.STATUS, msg.getAttributeAsString(Fields.BLOCK_STATUS));
            }
            if(!holder.containsKey(Fields.GIFT_CARD_CLASS) &&  msg.hasAttribute(Fields.AMOUNT))
               result.setHeaderField(Fields.AMOUNT, msg.getAttributeAsString(Fields.AMOUNT));

            if (holder.containsKey(Fields.DEST_ACCOUNT)) {
                AccountData accData = (AccountData) holder.get(Fields.DEST_ACCOUNT);
                result.setHeaderField(Fields.DEST_ACCOUNT, accData.getAccountNo());
            }
            Batch currentBatch = (Batch) holder.get(CFSConstants.CURRENT_BATCH);
            if(currentBatch!=null)
                result.setHeaderField(CFSConstants.CURRENT_BATCH, String.valueOf(currentBatch.getBatchPk().longValue()));
            Tx tx;
            if (holder.containsKey("tx")) {
                tx = (Tx) holder.get("tx");
                command.getCommandHeader().put(Fields.REFRENCE_NUMBER, tx.getTxPk());
                result.setHeaderField(Fields.REFRENCE_NUMBER, tx.getTxPk());
                result.setHeaderField(Fields.AMOUNT, String.valueOf(tx.getAmount()));

            }
            result.setRequest(command.toString());
            holder.put(CFSConstants.RESULT, result);
        } catch (Exception e) {
            throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, ActionCode.SYSTEM_MALFUNCTION);
        }

    }

    private void setAccountDataResult(CMResultSet result, Object accountData, String amountMsg, Long maxtTransLimit) {
        long actualBalance = 0;
        long availableBalance = 0;
        String isDebitCreditStr = "";
        String isDebitCreditAvailableStr = AccountData.ACCOUNT_CREDIT_STRING;
        long amount;
        try {
            amount = Long.parseLong("" + amountMsg);
            if (resultID.equals(Fields.DEST_ACCOUNT))
                amount = -amount;
        } catch (NumberFormatException e) {
            amount = 0;
        }

        if (accountData instanceof AccountData) {
            AccountData accountDataTmp = (AccountData) accountData;
            actualBalance = getActualBalance(accountDataTmp, amount, maxtTransLimit);
            isDebitCreditStr = ((actualBalance >= 0) ? AccountData.ACCOUNT_CREDIT_STRING : AccountData.ACCOUNT_DEBIT_STRING);
            availableBalance = getAvailableBalance(accountDataTmp, amount, maxtTransLimit);
            isDebitCreditAvailableStr = ((availableBalance >= 0) ? AccountData.ACCOUNT_CREDIT_STRING : AccountData.ACCOUNT_DEBIT_STRING);
        }

        result.setHeaderField(Constants.AVAILABLE_AMOUNT, Long.toString(availableBalance));
        result.setHeaderField(Constants.ACTUAL_AMOUNT, Long.toString(actualBalance));
        result.setHeaderField(Constants.DebitCredit, isDebitCreditStr);
        result.setHeaderField(Constants.AvailableDebitCredit, isDebitCreditAvailableStr);

    }

    private long getActualBalance(AccountData accountData, long amount, Long maxtTransLimit) {
        if (cardType != null && !cardType.equals("") && cardType.equals(Fields.GROUP_CARD))
            return accountData.getAccountBalance();
        else {
            if (maxtTransLimit != null && maxtTransLimit != CFSConstants.IGNORE_MAX_TRANS_LIMIT)
                return (maxtTransLimit - amount);
            else
                return (accountData.getAccountBalance() - amount);
        }
    }

    private long getAvailableBalance(AccountData accountData, long amount, Long maxtTransLimit) {
        long availableBalance;
        if (cardType != null && !cardType.equals("") && cardType.equals(Fields.GROUP_CARD))
            availableBalance = accountData.getAccountBalance() - amount - (accountData.getMinBalance() + accountData.getTotalBlockedAmount());
        else {
            if (maxtTransLimit != null && maxtTransLimit != CFSConstants.IGNORE_MAX_TRANS_LIMIT)
                availableBalance = accountData.getAccountBalance() - amount;
            else
                availableBalance = accountData.getAccountBalance() - amount - (accountData.getMinBalance() + accountData.getTotalBlockedAmount());
        }
        return availableBalance;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        resultID = cfg.get(CFSConstants.RESULT);
        resultType = cfg.get(CFSConstants.RESULT_TYPE);
        cardType = cfg.get(Fields.CARD_TYPE);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.ACCOUNT_NO;
        }

    }

}