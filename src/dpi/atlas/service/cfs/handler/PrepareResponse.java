package dpi.atlas.service.cfs.handler;

import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.entity.Device;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.client.messages.StockAccountMessage;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.math.BigInteger;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.10 $ $Date: 2007/11/04 12:04:24 $
 */

public class PrepareResponse extends CFSHandlerBase implements Configurable {

    protected String resultID;
    protected String resultType;
    private String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        try {
            if (log.isInfoEnabled()) log.info(description);
            CMResultSet result = null;
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            if (log.isDebugEnabled()) log.debug(resultID);
            if (log.isDebugEnabled()) log.debug(resultType);
            List tranList;
            Vector stTransLs = new Vector();
            if (resultID != null && !resultID.equals("") && holder.containsKey(resultID)) {
                if (resultType != null && !resultType.equals("")) { // The result is a collection, not a single entity
                    if (resultType.equals(CFSConstants.TX_CLASS)) {
                        tranList = (List) holder.get(resultID);
                        result = new CMResultSet(tranList, Tx.class);
                    } else if (resultType.equals("StockAccountMessage")) {
                        stTransLs = (Vector) holder.get(resultID);
                        result = new CMResultSet(stTransLs, StockAccountMessage.class);                        
                    } else {
                        tranList = (List) holder.get(resultID);
                        result = new CMResultSet(tranList, Class.forName(resultType));
                    }

                    Object accountData = holder.get(accountField);
                    if (msg.getAttributeAsString("command").equals(TJCommand.SH_STCK_TRANS_LIST_CMD)) {
                        result.setHeaderField(Constants.TRANS_COUNT, command.getHeaderParam(Fields.TRANS_COUNT));
                        result.setHeaderField(Constants.RESULT_SIZE, (String) holder.get(Constants.RESULT_SIZE));
                    }
                    long amount = 0;
                    try {
                        amount = Long.parseLong("" + msg.getAttribute(Fields.AMOUNT));
                    } catch (NumberFormatException e) {
                        amount = 0;
                    }

                    long actualBalance = 0;
                    long availableBalance = 0;
                    String accountNo = "";
                    String isDebitCreditStr = AccountData.ACCOUNT_CREDIT_STRING;
                    String isDebitCreditAvailableStr = AccountData.ACCOUNT_CREDIT_STRING;
                    if (accountData instanceof AccountData) {
                        actualBalance = ((AccountData) accountData).getAccountBalance() - amount;
                        isDebitCreditStr = ((actualBalance >= 0) ? AccountData.ACCOUNT_CREDIT_STRING : AccountData.ACCOUNT_DEBIT_STRING);

                        availableBalance = actualBalance - ((AccountData) accountData).getMinBalance() - ((AccountData) accountData).getTotalBlockedAmount();
                        isDebitCreditAvailableStr = ((availableBalance>= 0) ? AccountData.ACCOUNT_CREDIT_STRING : AccountData.ACCOUNT_DEBIT_STRING);

                        accountNo = ((AccountData) accountData).getAccountNo();
                    }

                    result.setHeaderField(Constants.ACTUAL_AMOUNT, new Long(actualBalance).toString());
                    result.setHeaderField(Constants.AVAILABLE_AMOUNT, new Long(availableBalance).toString());

                    result.setHeaderField(Constants.DebitCredit, isDebitCreditStr);
                    result.setHeaderField(Constants.AvailableDebitCredit, isDebitCreditAvailableStr);

                    String resultSize = "1";
                    if (holder.containsKey(Constants.RESULT_SIZE)) {
                        resultSize = (String) holder.get(Constants.RESULT_SIZE);
                    }
                    result.setHeaderField(Constants.RESULT_SIZE, resultSize);

                    if (!accountNo.equalsIgnoreCase("")) {
                        result.setHeaderField(Fields.ACCOUNT_NO, accountNo);
                    }
                } else {// result is a single entity
                    Object accountData = null;
                    accountData = holder.get(resultID);
                    long amount = 0;
                    try {
                        amount = Long.parseLong("" + msg.getAttribute(Fields.AMOUNT));
                    } catch (NumberFormatException e) {
                        amount = 0;
                    }

                    long actualBalance = 0;
                    String availableBalance = "";

                    String isDebitCreditStr = "";
                    String isDebitCreditAvailableStr = AccountData.ACCOUNT_CREDIT_STRING;

                    if (accountData instanceof AccountData) {
                       AccountData accountDataTmp=(AccountData) accountData;
                        actualBalance = accountDataTmp.getAccountBalance() - amount;
                        accountDataTmp.setAccountBalance(actualBalance);
                        isDebitCreditStr = ((actualBalance >= 0) ? AccountData.ACCOUNT_CREDIT_STRING : AccountData.ACCOUNT_DEBIT_STRING);
                        availableBalance = String.valueOf(actualBalance - (
                         accountDataTmp.getMinBalance() +
                         accountDataTmp.getTotalBlockedAmount()
                        )
                        );
                        isDebitCreditAvailableStr = ((new BigInteger(availableBalance).doubleValue() > 0) ? AccountData.ACCOUNT_CREDIT_STRING : AccountData.ACCOUNT_DEBIT_STRING);
                    }

                    result = new CMResultSet(accountData);
                    result.setHeaderField(Constants.ACTUAL_AMOUNT, new Long(actualBalance).toString());
                    result.setHeaderField(Constants.AVAILABLE_AMOUNT, availableBalance);
                    result.setHeaderField(Constants.DebitCredit, isDebitCreditStr);
                    result.setHeaderField(Constants.AvailableDebitCredit, isDebitCreditAvailableStr);

                    Object accountHolder = ((AccountData) accountData).getAccountHolder();
                    String sgbBranch_id = "";
                    if (accountHolder instanceof Account) {
                        sgbBranch_id = ((Account) accountHolder).getSgb_branch_id();
                        result.setHeaderField(Fields.ACCOUNT_STATUS, String.valueOf(((Account) accountHolder).getAccountStatus()));
                    } else if (accountHolder instanceof Device) {
                        sgbBranch_id = ((Device) accountHolder).getBranchId();
                    }
                    result.setHeaderField(Fields.BRANCH_CODE, sgbBranch_id);

                }
            } else if (holder.containsKey(Fields.ACCOUNT_NO)) {
                Object accountData = holder.get(Fields.ACCOUNT_NO);
                long amount = 0;
                try {
                    amount = Long.parseLong("" + msg.getAttribute(Fields.AMOUNT));
                } catch (NumberFormatException e) {
                    amount = 0;
                }

                long actualBalance = 0;
                long availableBalance = 0;

                String isDebitCreditStr = "";
                String isDebitCreditAvailableStr = AccountData.ACCOUNT_CREDIT_STRING;

                if (accountData instanceof AccountData) {
                    actualBalance = ((AccountData) accountData).getAccountBalance() - amount;
                    isDebitCreditStr = ((actualBalance >= 0) ? AccountData.ACCOUNT_CREDIT_STRING : AccountData.ACCOUNT_DEBIT_STRING);

                    availableBalance = actualBalance - ((AccountData) accountData).getMinBalance() - ((AccountData) accountData).getTotalBlockedAmount();
                    isDebitCreditAvailableStr = ((availableBalance>= 0) ? AccountData.ACCOUNT_CREDIT_STRING : AccountData.ACCOUNT_DEBIT_STRING);

                }
                result = new CMResultSet(accountData);

                result.setHeaderField(Constants.ACTUAL_AMOUNT, new Long(actualBalance).toString());
                result.setHeaderField(Constants.AVAILABLE_AMOUNT, new Long(availableBalance).toString());

                result.setHeaderField(Constants.DebitCredit, isDebitCreditStr);
                result.setHeaderField(Constants.AvailableDebitCredit, isDebitCreditAvailableStr);

                result.setHeaderField(Constants.RESULT_SIZE, (String) holder.get(Constants.RESULT_SIZE));


                if (msg.getAttributeAsString("command").equals(TJCommand.CMD_CARD_ACCOUNT_BLOCKING)) {
                    if (msg.getAttributeAsString(CFSConstants.ACTION_CODE).equals(ActionCode.APPROVED)) {
                        result.setHeaderField(Constants.ACCOUNT_BALANCE, msg.getAttributeAsString(Fields.ACCOUNT_BALANCE));
                        result.setHeaderField(Constants.BLOCK_AMOUNT, msg.getAttributeAsString(Fields.BLOCK_AMOUNT));
                        result.setHeaderField(Constants.BALANCE_MINIMUM, String.valueOf(msg.getAttribute(Fields.BALANCE_MINIMUM)));
                        String subcid = !msg.getAttributeAsString(Fields.SUBSID_AMOUNT).equals("") ? msg.getAttributeAsString(Fields.SUBSID_AMOUNT) : "0";
                        result.setHeaderField(Constants.SUBSID_AMOUNT, subcid);
                        result.setHeaderField(Constants.ACCOUNT_STATUS, msg.getAttributeAsString(Fields.ACCOUNT_STATUS));
                        result.setHeaderField(Constants.ACCOUNT_BRANCH_ID, msg.getAttributeAsString(Fields.ACCOUNT_BRANCH_ID));
                    }
                    result.setHeaderField(Constants.ACTUAL_AMOUNT, new Long(1000L).toString());
                    result.setHeaderField(Constants.DebitCredit, "D");
                } else if (msg.getAttributeAsString("command").equals(TJCommand.CMD_CARD_ACCOUNT_UNBLOCKING) ||
                        msg.getAttributeAsString("command").equals(TJCommand.CMD_SHOW_CARD_ACCOUNT_STATUS)) {
                    if (msg.getAttributeAsString(CFSConstants.ACTION_CODE).equals(ActionCode.APPROVED)) {
                        result.setHeaderField(Constants.ACCOUNT_STATUS, msg.getAttributeAsString(Fields.ACCOUNT_STATUS));
                        result.setHeaderField(Constants.ACTUAL_AMOUNT, new Long(1000L).toString());
                        result.setHeaderField(Constants.DebitCredit, "D");
                    } else {
                        result.setHeaderField(Constants.ACTUAL_AMOUNT, new Long(actualBalance).toString());
                        result.setHeaderField(Constants.DebitCredit, isDebitCreditStr);
                        result.setHeaderField(Constants.RESULT_SIZE, (String) holder.get(Constants.RESULT_SIZE));
                    }
                } else if (msg.getAttributeAsString("command").equals(TJCommand.SHARE_STCK_ACC_ST_CMD)) {
                    result.setHeaderField(Constants.ACC_BROKER_REL_ST, command.getParam(Fields.ACC_BROKER_REL_ST));
                    result.setHeaderField(Constants.RESULT_SIZE, (String) holder.get(Constants.RESULT_SIZE));
                } else if (msg.getAttributeAsString("command").equals(TJCommand.UNBLOCK_SHARE_STOCK_AMNT_CMD)) {
                    result.setHeaderField(Constants.BLOCK_DATE, command.getParam(Fields.BLOCK_DATE));
                } else if(msg.getAttributeAsString("command").equals(TJCommand.BLOCK_ACC_BY_BLOCK_NO_CMD)){       
                    result.setHeaderField(Fields.ACCOUNT_BALANCE, new Long(command.getParam(Fields.ACCOUNT_BALANCE)).toString() );
                    result.setHeaderField(Fields.ACCOUNT_BRANCH_ID, command.getParam(Fields.ACCOUNT_BRANCH_ID));
                } else if(msg.getAttributeAsString("command").equals(TJCommand.UNBLOCK_ACC_BY_BLOCK_NO_CMD))  {
                    result.setHeaderField(Fields.ACCOUNT_BALANCE, new Long(command.getParam(Fields.ACCOUNT_BALANCE)).toString() );
                    result.setHeaderField(Fields.BLOCK_NO, new Long(command.getParam(Fields.BLOCK_NO)).toString() );
                } else if(msg.getAttributeAsString("command").equals(TJCommand.SHOW_BLOCK_AMNT_STATUS_CMD)) {
                    result.setHeaderField(Fields.BLOCK_NO, new Long(command.getParam(Fields.BLOCK_NO)).toString() );
                    result.setHeaderField(Fields.BLOCK_STATUS, command.getParam(Fields.BLOCK_STATUS));
                    result.setHeaderField(Fields.BLCK_AMNT, command.getParam(Fields.BLCK_AMNT));
                }
            } else {
                AccountData accountData = new AccountData("-1", 0, 0, null);
                long amount = 0;
                accountData.setAccountBalance(amount);
                result = new CMResultSet(accountData);
                if (msg.getAttributeAsString("command").equals(TJCommand.MUNICIPALITY_BILL_PAYMENY_FOLLOWUP_CMD)) {
                    if (msg.getAttributeAsString(Constants.MNCPLTY_IS_FAR_CHECK_NEEDED).equals("false")) {
                      result.setHeaderField(Constants.MNCPLTY_BILL_AMNT, msg.getAttributeAsString(Constants.MNCPLTY_BILL_AMNT));
                      result.setHeaderField(Constants.MNCPLTY_PAY_BRANCH, msg.getAttributeAsString(Constants.MNCPLTY_PAY_BRANCH));
                      result.setHeaderField(Constants.MNCPLTY_PAY_DATE, msg.getAttributeAsString(Constants.MNCPLTY_PAY_DATE));
                      result.setHeaderField(Constants.MNCPLTY_PAY_TIME, msg.getAttributeAsString(Constants.MNCPLTY_PAY_TIME));
                      result.setHeaderField(Constants.MNCPLTY_IS_FAR_CHECK_NEEDED, "false");
                    } else  {
                      result.setHeaderField(Constants.MNCPLTY_BILL_AMNT, "");
                      result.setHeaderField(Constants.MNCPLTY_PAY_BRANCH, "");
                      result.setHeaderField(Constants.MNCPLTY_PAY_DATE, "");
                      result.setHeaderField(Constants.MNCPLTY_PAY_TIME, "");  
                      result.setHeaderField(Constants.MNCPLTY_IS_FAR_CHECK_NEEDED, "true");
                    }
                }
                result.setHeaderField(Constants.RESULT_SIZE, new String("-1"));

            }

            result.setHeaderField(Params.ACTION_CODE, (String) msg.getAttribute(CFSConstants.ACTION_CODE));
            command.getParams().put(CFSConstants.ACTION_CODE, (String) msg.getAttribute(CFSConstants.ACTION_CODE));
            command.getParams().put("JMSCorrelationID", command.getHeaderParam(Fields.SESSION_ID));

            if (command.getHeaderParam(Fields.MESSAGE_ID) != null) {
                int msg_id = (new Integer(command.getHeaderParam(Fields.MESSAGE_ID))).intValue() + 1;
                result.setHeaderField(Fields.MESSAGE_ID, msg_id + "");
            } else {                
                if (log.isErrorEnabled())
                    log.error("Command does not have Message id in its header,command = " + command);
                throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
            }
            if (holder.containsKey(Fields.DEST_ACCOUNT)) {
                AccountData accData = (AccountData) holder.get(Fields.DEST_ACCOUNT);
                result.setHeaderField(Fields.DEST_ACCOUNT, accData.getAccountNo());
            }
            Batch currentBatch = (Batch) holder.get(CFSConstants.CURRENT_BATCH);
            result.setHeaderField(CFSConstants.CURRENT_BATCH, String.valueOf(currentBatch.getBatchPk().longValue()));
            Tx tx = null;
            if (holder.containsKey("tx")) {
                tx = (Tx) holder.get("tx");
                command.getCommandHeader().put(Fields.REFRENCE_NUMBER, tx.getTxPk());
            }
            result.setRequest(command.toString());
            holder.put(CFSConstants.RESULT, result);
        } catch (Exception e) {
            throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.SYSTEM_MALFUNCTION));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        resultID = cfg.get(CFSConstants.RESULT);
        resultType = cfg.get(CFSConstants.RESULT_TYPE);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.ACCOUNT_NO;
        }

    }

}
