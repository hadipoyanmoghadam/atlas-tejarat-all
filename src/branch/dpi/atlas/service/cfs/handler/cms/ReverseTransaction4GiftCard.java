package branch.dpi.atlas.service.cfs.handler.cms;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:Behnaz.sh@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.1 $ $Date: 2015/6/20 10:15:04 $
 */

public class ReverseTransaction4GiftCard extends CFSHandlerBase implements Configurable {


    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        try {
            Tx original_tx = (Tx) holder.get("tx");

            //------------- Preparing RRN -------------
            String rrn = (String) msg.getAttribute(Fields.MN_RRN);
            if (rrn == null) rrn = "";
            String terminalID = (String) msg.getAttribute(Fields.TERMINAL_ID);
            if (terminalID == null) terminalID = "";

            //------- Prepare TX -------

            Tx reversal_tx = new Tx();
            reversal_tx.setTxPk(("FR" + original_tx.getTxPk().substring(2)));
            reversal_tx.setAmount(original_tx.getAmount());
            reversal_tx.setBatchPk(((Batch) holder.get(CFSConstants.CURRENT_BATCH)).getBatchPk());
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
//            reversal_tx.setMessageId(((String) msg.getAttribute(Fields.MESSAGE_ID)));
            reversal_tx.setMessageId("3");
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
            reversal_tx.setBranchDocNo(original_tx.getBranchDocNo());
            reversal_tx.setHostCode(original_tx.getHostCode());
            reversal_tx.setCardSequenceNo("");
            reversal_tx.setTxSequenceNumber("");
            reversal_tx.setDeviceCode("");
            reversal_tx.setSrcBranchId("");
            reversal_tx.setTxDateTime(null);
            reversal_tx.setSrc_account_balance(0);
            reversal_tx.setDescription(original_tx.getDescription());
            reversal_tx.setFeeAmount(0);
            reversal_tx.setRRN(rrn);
            reversal_tx.setMerchantTerminalId(terminalID);
            reversal_tx.setId1(original_tx.getId1());
            reversal_tx.setMessageSeq(original_tx.getMessageSeq());


            CFSFacadeNew.txnDoReverse4GiftCard(original_tx, reversal_tx);

        } catch (ModelException e) {
            log.debug(e);
            log.debug("ReverseTransaction4GiftCard::NotSufficient Funds");
            throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, new Exception(ActionCode.NOT_SUFFICIENT_FUNDS));
        } catch (Exception me) {
            log.error(me);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, ActionCode.GENERAL_DATA_ERROR);
        }


    }
}
