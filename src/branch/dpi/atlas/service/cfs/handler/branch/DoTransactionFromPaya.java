package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: July 28, 2020
 * Time: 01:06 PM
 */

public class DoTransactionFromPaya extends CFSHandlerBase implements Configurable {

    private String fromAccountField;
    private String toAccountField;
    private String amountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        //-------- Preparing destAccNo--------
        String destAccNo = msg.getAttributeAsString(toAccountField);

        if (destAccNo != null) {
            try {
                destAccNo = ISOUtil.zeropad(destAccNo.trim(), 13);
            } catch (ISOException e) {
                e.toString();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        String accountNo = msg.getAttributeAsString(fromAccountField);
        try {
            accountNo = ISOUtil.zeropad(accountNo, 13);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + accountNo + "' in DoTransactionFromPaya : " + e.getMessage());
        }


        //-------- Preparing txOrigDate--------
        String txOrigDate = (String) msg.getAttribute(Fields.DATE);

        //------------- Preparing Description -------------
        String desc = (String) msg.getAttribute(Fields.DOCUMENT_DESCRIPTION);
        if (desc == null) desc = "";

        //------------- Preparing RRN -------------
        String rrn = (String) msg.getAttribute(Fields.MN_RRN);
        if (rrn == null) rrn = "";
        //------------- Preparing Terminal Id-------------
        String terminalID = (String) msg.getAttribute(Fields.TERMINAL_ID);
        if (terminalID == null) terminalID = "";
        //------------- Preparing ID1 -------------
        String id1 = msg.getAttributeAsString(Fields.MN_ID);
        if (id1 == null || id1.trim().equals("")) id1 = "";
        else id1 = id1.trim();

        //------------- Preparing minimum balance-------------
        long balanceMinimum = 0;
        Long balanceMinimumObj = (Long) msg.getAttribute(Fields.BALANCE_MINIMUM);
        if (balanceMinimumObj != null)
            balanceMinimum = balanceMinimumObj;

        String messageType = msg.getAttributeAsString(Fields.MESSAGE_TYPE);

        String txSrc = (String) msg.getAttribute(Fields.SERVICE_TYPE);

        //-------- Preparing TX --------
        Tx tx = new Tx();
        tx.setTxPk(msg.getAttributeAsString(Fields.REFRENCE_NUMBER));
        tx.setAmount(0);
        tx.setBatchPk(((Batch) holder.get(CFSConstants.CURRENT_BATCH)).getBatchPk());
        tx.setCurrency(((String) msg.getAttribute(Fields.AMOUNT_CURRENCY)));
        tx.setDestAccountNo(destAccNo);
        tx.setTotalDestAccNo(msg.getAttributeAsString(Fields.TOTAL_DEST_ACCOUNT));
        tx.setSessionId((String) msg.getAttribute(Fields.SESSION_ID));
        tx.setSrcAccountNo(accountNo);
        tx.setTxCode(messageType);
        tx.setMessageId(((String) msg.getAttribute(Fields.MESSAGE_ID)));
        tx.setTxSrc(txSrc);
        tx.setCreationDate(DateUtil.getSystemDate());
        tx.setCreationTime(DateUtil.getSystemTime());
        //TODO: perhaps we must add source branch ID (or InBranch)
        tx.setTxDateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        tx.setTxOrigDate(txOrigDate);
        tx.setTxOrigTime(msg.getAttributeAsString(Fields.TIME));
        tx.setSgbActionCode((String) holder.get(Fields.SGB_TX_CODE));
        tx.setIsReversed(CFSConstants.NOT_REVERSED);
        tx.setIsCutovered(CFSConstants.NOT_CUTOVERED);
        tx.setSgbActionCode("" + msg.getAttribute(Fields.SGB_TX_CODE));
        if (msg.getAttributeAsString(Fields.BRANCH_CODE).trim().length() == 5)
            tx.setSgbBranchId(msg.getAttributeAsString(Fields.BRANCH_CODE).trim());
        else
            tx.setSgbBranchId(msg.getAttributeAsString(Fields.BRANCH_CODE).trim().substring(1));
        tx.setCardNo(msg.getAttributeAsString(Fields.PAN));
        tx.setBranchDocNo(msg.getAttributeAsString(Fields.BRANCH_DOC_NO));
        //------- Setting Null Values -------
        tx.setCardSequenceNo("");
        tx.setTxSequenceNumber("");
        tx.setDeviceCode("");
        tx.setAcquirer(CFSConstants.BRANCH_ACQUIRE);
        tx.setSrcBranchId("");
        tx.setTxDateTime(null);
        tx.setSrc_account_balance(0);
        tx.setDest_account_balance(0);
        tx.setDescription(desc.trim());
        tx.setFeeAmount(0);
        tx.setRRN(rrn);
        tx.setMerchantTerminalId(terminalID);
        tx.setId1(id1);
        tx.setHostCode(msg.getAttributeAsString(Constants.SRC_HOST_ID) + msg.getAttributeAsString(Constants.DEST_HOST_ID));

        HashMap extraData = new HashMap();
        extraData.put(Fields.REFRENCE_NUMBER, msg.getAttribute(Fields.REFRENCE_NUMBER));
        extraData.put(Fields.USER_ID, msg.getAttribute(Fields.USER_ID));
        extraData.put(Fields.DUE_DATE, msg.getAttribute(Fields.DUE_DATE));
        extraData.put(Fields.PAYA_REQUEST,msg.getAttributeAsString(Fields.PAYA_REQUEST));

        try {
            int result = CFSFacadeNew.txnDoPayaTransaction(tx, extraData);

            switch (result) {
                case 0: //Approve
                    holder.put(Fields.SOURCE_ACCOUNT_BALANCE, tx.getSrc_account_balance());
                    holder.put(Fields.DEST_ACCOUNT_BALANCE, tx.getDest_account_balance());
                    msg.setAttribute(Fields.AMOUNT,extraData.get(Fields.AMOUNT));
                    break;
                case 1: // PAYA is not Active
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYA_IS_INACTIVE);
                    throw new CFSFault(CFSFault.FLT_PAYA_IS_INACTIVE, new Exception(ActionCode.PAYA_IS_INACTIVE));
                case 2: // Request not found in TBBRACH
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.PAYA_REQUEST_NOT_FOUND);
                    throw new CFSFault(CFSFault.FLT_PAYA_REQUEST_NOT_FOUND, new Exception(ActionCode.PAYA_REQUEST_NOT_FOUND));
                case 3:  //Account Not Found in customeraccount
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
                    throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
                case 4: // TRANSACTION_NOT_PERMITTED_TO_TERMINAL
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                    throw new CFSFault(CFSFault.FLT_TRANSACTION_NOT_PERMITTED_TO_TERMINAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                case 5: //can not sed message to ACH system
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.CAN_NOT_SEND_TO_PAYA);
                    throw new CFSFault(CFSFault.FLT_SYSTEM_MALFUNCTION, new Exception(ActionCode.CAN_NOT_SEND_TO_PAYA));
                 default:
                    log.error("ERROR:: invalid result");
                    throw new Exception();
            }

        } catch (SQLException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (ISOException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, ActionCode.GENERAL_DATA_ERROR);
        } catch (CFSFault e) {
            throw e;
        } catch (Exception e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_ERROR, ActionCode.GENERAL_ERROR);
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        fromAccountField = cfg.get(CFSConstants.FROM_ACCOUNT_FIELD);
        toAccountField = cfg.get(CFSConstants.TO_ACCOUNT_FIELD);
        amountField = cfg.get(CFSConstants.AMOUNT_FIELD);
        if ((fromAccountField == null) || (fromAccountField.equals(""))) {
            log.fatal("From Account is not Specified");
            throw new ConfigurationException("From Account is not Specified");
        }
        if ((toAccountField == null) || (toAccountField.equals(""))) {
            log.fatal("To Account is not Specified");
            throw new ConfigurationException("To Account is not Specified");
        }
        if ((amountField == null) || (amountField.trim().equals(""))) {
            log.fatal("Amount Field is not Specified");
            throw new ConfigurationException("Amount Field is not Specified");
        }
    }
}
