package branch.dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.AccountData;
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
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class ReverseTransaction extends CFSHandlerBase implements Configurable {


    public void doProcess(CMMessage msg, Map holder) throws CFSFault {


        HashMap extraData = new HashMap();
        Tx original_tx = null;

        try {
            original_tx = (Tx) holder.get("tx");

            String docNo = "FR" + original_tx.getTxPk().substring(2);

            //------------- Preparing RRN -------------
            String rrn = (String) msg.getAttribute(Fields.MN_RRN);
            if (rrn == null) rrn = "";
            String terminalID = (String) msg.getAttribute(Fields.TERMINAL_ID);
            if (terminalID == null) terminalID = "";

            //---------- Preparing reversal_tx----------
            Tx reversal_tx = new Tx();
            reversal_tx.setTxPk((docNo));
            reversal_tx.setAmount(original_tx.getAmount());
            reversal_tx.setFeeAmount(original_tx.getFeeAmount());
            reversal_tx.setBatchPk(((Batch) holder.get(CFSConstants.CURRENT_BATCH)).getBatchPk().longValue());
            reversal_tx.setCardNo(original_tx.getCardNo());
            reversal_tx.setCardSequenceNo(original_tx.getCardSequenceNo());
            reversal_tx.setTxSequenceNumber(original_tx.getTxSequenceNumber());
            //TODO: Check if the following field (AMOUNT_CURRENCY) is appropriate
            reversal_tx.setCurrency(original_tx.getCurrency());
            reversal_tx.setSessionId(original_tx.getSessionId());
            reversal_tx.setSrcAccountNo(original_tx.getSrcAccountNo());
            reversal_tx.setDestAccountNo(original_tx.getDestAccountNo());
            reversal_tx.setTotalDestAccNo(original_tx.getTotalDestAccNo());
            reversal_tx.setTxCode((String) msg.getAttribute(Fields.MESSAGE_TYPE));
            reversal_tx.setMessageId(ISOUtil.zeroUnPad((String) msg.getAttribute(Fields.MESSAGE_ID)));
            reversal_tx.setTxSrc(original_tx.getTxSrc());
            reversal_tx.setCreationDate(DateUtil.getSystemDate());
            reversal_tx.setCreationTime(DateUtil.getSystemTime());
            reversal_tx.setAcquirer(original_tx.getAcquirer());
            reversal_tx.setDeviceCode(original_tx.getDeviceCode());
            reversal_tx.setSrcBranchId(original_tx.getSrcBranchId());
            reversal_tx.setTxDateTime(new java.sql.Timestamp(System.currentTimeMillis()));
            reversal_tx.setTxOrigDate(original_tx.getTxOrigDate());
            reversal_tx.setTxOrigTime(original_tx.getTxOrigTime());
            reversal_tx.setSgbActionCode(msg.getAttributeAsString(Fields.SGB_TX_CODE));
            reversal_tx.setSgbBranchId(original_tx.getSgbBranchId());
            reversal_tx.setOrigTxPk(original_tx.getTxPk());
            reversal_tx.setIsReversalTxn('1');
            reversal_tx.setDescription(original_tx.getDescription().trim());
            //TODO Add new items
            reversal_tx.setRRN(rrn);
            reversal_tx.setMerchantTerminalId(terminalID);
            reversal_tx.setHostCode(original_tx.getHostCode());
            reversal_tx.setId1(original_tx.getId1());
            reversal_tx.setBranchDocNo(msg.getAttributeAsString(Fields.BRANCH_DOC_NO));
            reversal_tx.setDeviceCode("");

            Long balanceMinimum = (Long) msg.getAttribute(Fields.BALANCE_MINIMUM);
            if (balanceMinimum == null) balanceMinimum = new Long(0);

            extraData.put(Constants.SRC_HOST_ID, msg.getAttributeAsString(Constants.SRC_HOST_ID));
            extraData.put(Constants.DEST_HOST_ID, msg.getAttributeAsString(Constants.DEST_HOST_ID));
            extraData.put(Fields.BALANCE_MINIMUM, balanceMinimum);
            extraData.put(CFSConstants.REVERSE_ORIG_DATE, original_tx.getCreationDate());
            extraData.put(Fields.MESSAGE_TYPE, msg.getAttributeAsString(Fields.MESSAGE_TYPE));
            extraData.put(Fields.SESSION_ID, original_tx.getSessionId());
            extraData.put(Constants.REF_NO, original_tx.getTxPk());


            CFSFacadeNew.txnDoFTReverse(original_tx, reversal_tx, extraData);
            holder.put(Fields.SOURCE_ACCOUNT_BALANCE, reversal_tx.getSrc_account_balance());
            holder.put(Fields.DEST_ACCOUNT_BALANCE, reversal_tx.getDest_account_balance());
            holder.put("tx", reversal_tx);

        } catch (ModelException e) {
            log.debug(e);
            log.debug("ReverseTransaction::NotSufficient Funds");
            throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, new Exception(ActionCode.NOT_SUFFICIENT_FUNDS));

        } catch (SQLException me) {
            log.error(me);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));

        } catch (Exception me) {
            log.error(me);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }


    }
}
