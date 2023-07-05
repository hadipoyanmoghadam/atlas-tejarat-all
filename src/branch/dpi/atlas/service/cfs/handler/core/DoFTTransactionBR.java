package branch.dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
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
 * User:R.Nasiri
 * Date: July 13, 2019
 * Time: 04:23 PM
 */

public class DoFTTransactionBR extends CFSHandlerBase implements Configurable {

    private String fromAccountField;
    private String toAccountField;
    private String amountField;
    private String notChecked;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String messageType = (String) msg.getAttribute(Fields.MESSAGE_TYPE);

        Long balanceMinimum = (Long) msg.getAttribute(Fields.BALANCE_MINIMUM);
        if (balanceMinimum == null) {
            balanceMinimum = new Long(0);
        }

        //------------- Preparing Wage Amount -------------
        long amount = Long.parseLong((String) msg.getAttribute(amountField));

        //------------- Preparing src/dest account -------------

        String destAccNo = msg.getAttributeAsString(toAccountField);

        if (destAccNo != null) {
            try {
                destAccNo = ISOUtil.zeropad(destAccNo, 13);
            } catch (ISOException e) {
                e.toString();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


        String srcAccountNo = msg.getAttributeAsString(fromAccountField);
        try {
            srcAccountNo = ISOUtil.zeropad(srcAccountNo, 13);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + srcAccountNo + "' in DoFTTransactionBR : " + e.getMessage());
        }

        //------------- Preparing Terminal Id-------------

        String terminalID = (String) msg.getAttribute(Fields.TERMINAL_ID);
        if (terminalID == null) terminalID = "";

        //------------- Preparing TxOrigDate-------------
        String TxOrigDate = (String) msg.getAttribute(Fields.DATE);

        //------------- Preparing Description -------------

        String desc = (String) msg.getAttribute(Fields.DOCUMENT_DESCRIPTION);
        if (desc == null) desc = "";

        //------------- Preparing RRN -------------
        String rrn = (String) msg.getAttribute(Fields.MN_RRN);
        if (rrn == null) rrn = "";

        //------------- Preparing TX -------------

        Tx tx = new Tx();
        tx.setTxPk(msg.getAttributeAsString(Fields.REFRENCE_NUMBER));
        tx.setFeeAmount(0);
        tx.setAmount(((amount)));
        tx.setBatchPk(((Batch) holder.get(CFSConstants.CURRENT_BATCH)).getBatchPk().longValue());
        tx.setCurrency(((String) msg.getAttribute(Fields.AMOUNT_CURRENCY)));
        tx.setDestAccountNo(destAccNo);
        tx.setTotalDestAccNo(msg.getAttributeAsString(Fields.TOTAL_DEST_ACCOUNT));
        tx.setSessionId((String) msg.getAttribute(Fields.SESSION_ID));
        tx.setSrcAccountNo(srcAccountNo);
        tx.setTxCode((String) msg.getAttribute(Fields.MESSAGE_TYPE));
        tx.setMessageId(((String) msg.getAttribute(Fields.MESSAGE_ID)));
        tx.setTxSrc((String) msg.getAttribute(Fields.SERVICE_TYPE));
        tx.setCreationDate(DateUtil.getSystemDate());
        tx.setCreationTime(DateUtil.getSystemTime());
        tx.setTxDateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        tx.setTxOrigDate(TxOrigDate);
        tx.setTxOrigTime((String) msg.getAttribute(Fields.TIME));
        tx.setSgbActionCode((String) holder.get(Fields.SGB_TX_CODE));
        tx.setIsReversed(CFSConstants.NOT_REVERSED);
        tx.setIsCutovered(CFSConstants.NOT_CUTOVERED);
        String branchCode=msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
        if (branchCode.length() > 5)
            tx.setSgbBranchId(branchCode.substring(1));
        else
            tx.setSgbBranchId(branchCode);
        tx.setCardNo(msg.getAttributeAsString(Fields.PAN));
        tx.setBranchDocNo(msg.getAttributeAsString(Fields.BRANCH_DOC_NO));

        //------- Setting Null Values -------
        tx.setCardSequenceNo("");
        tx.setTxSequenceNumber("");
        tx.setAcquirer(CFSConstants.BRANCH_ACQUIRE);
        tx.setDeviceCode("");
        tx.setSrcBranchId("");
        tx.setDescription(desc);
        tx.setRRN(rrn);
        tx.setMerchantTerminalId(terminalID);
        tx.setId1("");

        if((msg.getAttributeAsString(Constants.SRC_HOST_ID) + msg.getAttributeAsString(Constants.DEST_HOST_ID)).equalsIgnoreCase(Constants.F2C) ||
                (msg.getAttributeAsString(Constants.SRC_HOST_ID) + msg.getAttributeAsString(Constants.DEST_HOST_ID)).equalsIgnoreCase(Constants.C2F)){
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            tx.setTotalDestAccNo(command.getParam(Fields.TOTAL_DEST_ACCOUNT));
        }


        try {
            if (msg.getAttributeAsString(Fields.MESSAGE_TYPE).startsWith(TJCommand.CMD_TRANSFER_FUND))
                tx.setHostCode(msg.getAttributeAsString(Constants.SRC_HOST_ID) + msg.getAttributeAsString(Constants.DEST_HOST_ID));

            HashMap extraData = new HashMap();
            extraData.put(Constants.SRC_HOST_ID, msg.getAttributeAsString(Constants.SRC_HOST_ID));
            extraData.put(Constants.DEST_HOST_ID, msg.getAttributeAsString(Constants.DEST_HOST_ID));
            extraData.put(Constants.SESSION_ID, msg.getAttributeAsString(Constants.SESSION_ID));
            extraData.put(Constants.REF_NO, msg.getAttributeAsString(Constants.REF_NO));


            CFSFacadeNew.txnDoFTTransaction(tx, balanceMinimum.longValue(), extraData);
            holder.put(Fields.SOURCE_ACCOUNT_BALANCE, tx.getSrc_account_balance());
            holder.put(Fields.DEST_ACCOUNT_BALANCE, tx.getDest_account_balance());

        } catch (NotFoundException e) {
            log.error(e);
            throw new CFSFault(CFSFault.FAULT_AUTH_GENERAL_INVALID_ACCOUNT, new Exception(ActionCode.FUND_TRANSFER_HAVE_NOT_BEEN_DONE));
        } catch (SQLException e) {
            log.error(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR)); /// TODO another error
        } catch (ModelException e) {
            throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, new Exception(ActionCode.NOT_SUFFICIENT_FUNDS));
        } catch (Exception e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        fromAccountField = cfg.get(CFSConstants.FROM_ACCOUNT_FIELD);
        toAccountField = cfg.get(CFSConstants.TO_ACCOUNT_FIELD);
        notChecked = cfg.get("blpypayment-not-checked");
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
