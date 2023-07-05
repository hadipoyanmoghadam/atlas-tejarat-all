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
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Jun 28, 2021
 * Time: 1:30 PM
 */

public class DoTransactionForBlockWage extends CFSHandlerBase implements Configurable {

    private String fromAccountField;
    private String toAccountField;
    private String amountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        //-------- Preparing destAccNo--------
        String destAccNo = msg.getAttributeAsString(toAccountField);

        if (destAccNo != null) {
            try {
                destAccNo = ISOUtil.zeropad(destAccNo, 13);
            } catch (ISOException e) {
                e.toString();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        String accountNo = msg.getAttributeAsString(fromAccountField);
        try {
            accountNo = ISOUtil.zeropad(accountNo, 13);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + accountNo + "' in DoTransactionFromBranch : " + e.getMessage());
        }


        long amount = Long.parseLong(msg.getAttributeAsString(Fields.WAGE_AMOUNT));

        //------------- Preparing Description -------------
           String desc = msg.getAttributeAsString(Fields.WAGE_DESCRIPTION);
           if (desc == null) desc = "";

        //------------- Preparing RRN -------------
           String rrn = msg.getAttributeAsString(Fields.MN_RRN);
           if (rrn == null) rrn = "";
        //------------- Preparing Terminal Id-------------
        String terminalID = msg.getAttributeAsString(Fields.TERMINAL_ID);
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

        String txSrc=(String) msg.getAttribute(Fields.SERVICE_TYPE);
        if(txSrc!=null && txSrc.trim().equalsIgnoreCase(Constants.CREDITS_SERVICE)){
            if(rrn!=null && rrn.trim().startsWith(Constants.NC_SERVICE))
                txSrc=Constants.NC_SERVICE;
        }


        //-------- Preparing TX --------
        Tx tx = new Tx();
        tx.setTxPk(msg.getAttributeAsString(Fields.REFRENCE_NUMBER));
        tx.setAmount(amount);
        tx.setBatchPk(((Batch) holder.get(CFSConstants.CURRENT_BATCH)).getBatchPk());
        tx.setCurrency(((String) msg.getAttribute(Fields.AMOUNT_CURRENCY)));
        tx.setDestAccountNo(destAccNo);
        tx.setTotalDestAccNo(destAccNo);
        tx.setSessionId((String) msg.getAttribute(Fields.SESSION_ID));
        tx.setSrcAccountNo(accountNo);
        tx.setTxCode(messageType);
        tx.setMessageId(((String) msg.getAttribute(Fields.MESSAGE_ID)));
        tx.setTxSrc(txSrc);
        tx.setCreationDate(DateUtil.getSystemDate());
        tx.setCreationTime(DateUtil.getSystemTime());
        //TODO: perhaps we must add source branch ID (or InBranch)
        tx.setTxDateTime(new java.sql.Timestamp(System.currentTimeMillis()));
        tx.setTxOrigDate(DateUtil.getSystemDate());
        tx.setTxOrigTime(DateUtil.getSystemTime());
        tx.setIsReversed(CFSConstants.NOT_REVERSED);
        tx.setIsCutovered(CFSConstants.NOT_CUTOVERED);
        tx.setSgbActionCode(msg.getAttributeAsString(Fields.SGB_TX_CODE));
        if(msg.getAttributeAsString(Fields.BRANCH_CODE).trim().length()==5)
            tx.setSgbBranchId(msg.getAttributeAsString(Fields.BRANCH_CODE).trim());
        else
            tx.setSgbBranchId(msg.getAttributeAsString(Fields.BRANCH_CODE).trim().substring(1));
        tx.setCardNo(CFSConstants.BRANCH_ACQUIRE);
        tx.setBranchDocNo(rrn.substring(rrn.length()-6));
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
        tx.setHostCode(Constants.CFS_HOSTID + Constants.SGB_HOSTID);
        tx.setMessageSeq(rrn);

        msg.setAttribute(Constants.WAGE_TX, tx);

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
