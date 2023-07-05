package branch.dpi.atlas.service.cfs.handler.tourist;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: April 30, 2017
 * Time: 02:16 PM
 */

public class DoTransaction4TouristCard extends CFSHandlerBase implements Configurable {

    private String fromAccountField;
    private String toAccountField;
    private String amountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        HashMap extraData = new HashMap();
        //-------- Preparing destAccNo--------
        String destAccNo = msg.getAttributeAsString(toAccountField);
        String accountNo = msg.getAttributeAsString(fromAccountField);
        long amount = Long.parseLong((String) msg.getAttribute(amountField));

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
        else id1=id1.trim();

        //------------- Preparing minimum balance-------------
        long balanceMinimum=0;
        Long balanceMinimumObj = (Long) msg.getAttribute(Fields.BALANCE_MINIMUM);
        if(balanceMinimumObj!=null)
            balanceMinimum=balanceMinimumObj;

        String messageType=msg.getAttributeAsString(Fields.MESSAGE_TYPE);

        //-------- Preparing TX --------
        Tx tx = new Tx();
        tx.setTxPk(msg.getAttributeAsString(Fields.REFRENCE_NUMBER));
        tx.setAmount(amount);
        tx.setBatchPk(((Batch) holder.get(CFSConstants.CURRENT_BATCH)).getBatchPk());
        tx.setCurrency(((String) msg.getAttribute(Fields.AMOUNT_CURRENCY)));
        tx.setDestAccountNo(destAccNo);
        tx.setSessionId((String) msg.getAttribute(Fields.SESSION_ID));
        tx.setSrcAccountNo(accountNo);
        tx.setTxCode(messageType);
        tx.setMessageId(((String) msg.getAttribute(Fields.MESSAGE_ID)));
        tx.setTxSrc((String) msg.getAttribute(Fields.SERVICE_TYPE));
        tx.setCreationDate(DateUtil.getSystemDate());
        tx.setCreationTime(DateUtil.getSystemTime());
        tx.setTxDateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        tx.setTxOrigDate(txOrigDate);
        tx.setTxOrigTime(msg.getAttributeAsString(Fields.TIME));
        tx.setSgbActionCode((String) holder.get(Fields.SGB_TX_CODE));
        tx.setIsReversed(CFSConstants.NOT_REVERSED);
        tx.setIsCutovered(CFSConstants.NOT_CUTOVERED);
        tx.setSgbActionCode("" + msg.getAttribute(Fields.SGB_TX_CODE));
        if(msg.getAttributeAsString(Fields.BRANCH_CODE).trim().length()==5)
            tx.setSgbBranchId(msg.getAttributeAsString(Fields.BRANCH_CODE).trim());
        else
            tx.setSgbBranchId(msg.getAttributeAsString(Fields.BRANCH_CODE).trim().substring(1));
        tx.setBranchDocNo(msg.getAttributeAsString(Fields.BRANCH_DOC_NO));

        tx.setHostCode(Constants.HOST_ID_CFS + Constants.HOST_ID_CFS);
        tx.setCardNo(msg.getAttributeAsString(Fields.PAN));
        tx.setTotalDestAccNo(msg.getAttributeAsString(Fields.TOTAL_DEST_ACCOUNT));

        //------- Setting Null Values -------
        tx.setCardSequenceNo("");
        tx.setTxSequenceNumber("");
        tx.setDeviceCode("");
        tx.setAcquirer(CFSConstants.BRANCH_ACQUIRE);
        tx.setSrcBranchId("");
        tx.setTxDateTime(null);
        tx.setSrc_account_balance(0);
        tx.setDest_account_balance(0);
        tx.setDescription(desc);
        tx.setFeeAmount(0);
        tx.setRRN(rrn);
        tx.setMerchantTerminalId(terminalID);
        tx.setId1(id1);

        try {
            CFSFacadeNew.txnDoTransaction4TouristCard(tx, extraData, balanceMinimum);
            holder.put(Fields.SOURCE_ACCOUNT_BALANCE, tx.getSrc_account_balance());
            holder.put(Fields.DEST_ACCOUNT_BALANCE, tx.getDest_account_balance());

        } catch (NotFoundException e) {
            log.error(e);
            throw new CFSFault(CFSFault.FAULT_AUTH_GENERAL_INVALID_ACCOUNT, ActionCode.FUND_TRANSFER_HAVE_NOT_BEEN_DONE);
        } catch (ModelException e1) {
                throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, ActionCode.NOT_SUFFICIENT_FUNDS);
        } catch (SQLException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        } catch (ISOException e1) {
            log.error(e1);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, ActionCode.GENERAL_DATA_ERROR);
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
