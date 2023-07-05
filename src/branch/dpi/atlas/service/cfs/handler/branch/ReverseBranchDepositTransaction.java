package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * @author <a href="mailto:rampalo@yahoo.com">Ramin Abdollahi</a>
 * @author <a href="mailto:alireza@taherkordi.com">Alireza Taherkordi</a>
 * @version $Revision: 1.6 $ $Date: 2007/11/04 12:04:24 $
 */

public class ReverseBranchDepositTransaction extends CFSHandlerBase implements Configurable {


    public void doProcess(CMMessage msg, Map holder) throws CFSFault {


        try {
            dpi.atlas.model.tj.entity.Tx original_tx = (dpi.atlas.model.tj.entity.Tx) holder.get("tx");

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
            reversal_tx.setMessageId(((String) msg.getAttribute(Fields.MESSAGE_ID)));
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
            reversal_tx.setBranchDocNo(msg.getAttributeAsString(Fields.BRANCH_DOC_NO));
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

            if (reversal_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD_REVERSAL)){
                String requestType= msg.getAttributeAsString(Fields.REQUEST_TYPE);
                if(requestType!=null &&  !requestType.trim().equals("") )
                    reversal_tx.setCardSequenceNo(requestType.trim());
            }

            if (reversal_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT_REVERSAL) ||
                    reversal_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_CREDITS_DEPOSIT_REVERSAL)||
                    reversal_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_MANZUME_DEPOSIT_REVERSAL)){
                String accountNature = (String) msg.getAttribute(Constants.SRC_ACCOUNT_NATURE);
                if (accountNature == null) accountNature = "";
                reversal_tx.setDeviceCode(accountNature);
            }

            if (reversal_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE_REVERSAL) ||
                    reversal_tx.getTxCode().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE_REVERSAL)){
                //------------- Preparing Description -------------
                String desc = (String) msg.getAttribute(Fields.DOCUMENT_DESCRIPTION);
                reversal_tx.setDescription((desc == null) ? "" : desc.trim());

                //------------- Preparing Extra_Info -------------
                String extraInfo = (String) msg.getAttribute(Fields.EXTRA_INFO);
                reversal_tx.setExtraInfo((extraInfo == null) ? "" : extraInfo.trim());
            }

            CFSFacadeNew.txnDoOneWayReverse(original_tx, reversal_tx);
            holder.put("tx", reversal_tx);
            holder.put(Fields.SOURCE_ACCOUNT_BALANCE, reversal_tx.getSrc_account_balance());
            holder.put(Fields.DEST_ACCOUNT_BALANCE, reversal_tx.getDest_account_balance());

        } catch (ModelException e) {
            log.debug(e);
            String messageType = (String) msg.getAttribute(Fields.MESSAGE_TYPE);
            String requestType = msg.getAttributeAsString(Fields.REQUEST_TYPE);
            if (messageType.equalsIgnoreCase(TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD_REVERSAL) &&
                    requestType != null && requestType.trim().equals("1")) {
                log.debug("ReverseBranchGiftCardDepositTransaction:: invalid operation");
                throw new CFSFault(CFSFault.FLT_INVALID_OPERATION, new Exception(ActionCode.INVALID_OPERATION));
            } else {
                log.debug("ReverseBranchDepositTransaction::NotSufficient Funds");
                throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, new Exception(ActionCode.NOT_SUFFICIENT_FUNDS));
            }
        } catch (ServerAuthenticationException e1) {
            if (log.isDebugEnabled()) log.error(e1);
            throw new CFSFault(CFSFault.FLT_DOCUMENT_NOT_FOUND, ActionCode.DOCUMENT_NOT_FOUND);
        } catch (Exception me) {
            log.error(me);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, ActionCode.GENERAL_DATA_ERROR);
        }


    }
}
