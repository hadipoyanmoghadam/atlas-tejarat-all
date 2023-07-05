package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
import dpi.atlas.model.tj.entity.Tx;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: DEC 23, 2020
 * Time: 1:21:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessBlockAmountCBI extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(ProcessBlockAmountCBI.class);
    protected String accountField;
    protected String amountField;

    public ProcessBlockAmountCBI() {
        accountField = Fields.SRC_ACCOUNT;
        amountField = Fields.AMOUNT;
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String acc = msg.getAttributeAsString(accountField);
        String cbiFlag=msg.getAttributeAsString(Fields.CBI_FLAG);
        try {
            acc = ISOUtil.zeropad(acc, 13);
            if (log.isDebugEnabled()) log.debug(accountField + "=" + acc);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + acc + "' in ProcessBlockAmountCBI : " + e.getMessage());
        }
        try {
            long minBlance = 0;
            Long balanceMinimumObj = (Long) msg.getAttribute(Fields.BALANCE_MINIMUM);
            if (balanceMinimumObj != null) {
                minBlance = balanceMinimumObj;
            }

            Tx tx= (Tx) msg.getAttribute(Constants.WAGE_TX);

            long blockedAmount = Long.parseLong((String) msg.getAttribute(amountField));
            String blockNo = msg.getAttributeAsString(Fields.BLOCK_ROW);

            if (blockNo == null || blockNo.equals(""))
                throw new Exception("Error in get blockNo");

            String blockDate;
            String blockTime;
            String brokerId = "";
            String proverId = "";
            String chnUser = "";
            String createDate = DateUtil.getSystemDate();
            String createTime = DateUtil.getSystemTime();
            String documentDescribtion = msg.getAttributeAsString(Fields.DOCUMENT_DESCRIPTION).trim();
            String accountStatus = msg.getAttributeAsString(Fields.ACCOUNT_STATUS);

            blockDate = msg.getAttributeAsString(Constants.BLCK_DATE);
            blockTime = msg.getAttributeAsString(Constants.BLCK_TIME);

            brokerId = Constants.BROKER_ID_SIMIN_CBI;
            proverId = Constants.PROVIDER_ID_SIMIN_CBI;
            chnUser = Constants.CHN_USER_SIMIN;

           String branchId = msg.getAttributeAsString(Fields.USER_ID);
            if (branchId == null) branchId = "";
            String blockStatus = msg.getAttributeAsString(Fields.BLOCK_STATUS);


            if (!blockStatus.equals(Constants.DUPLICATE_BLOCK_STATUS)) {
                long[] result = CFSFacadeNew.doBlockAmountTransactional4CBI(acc, minBlance, blockedAmount, blockNo, brokerId, proverId, chnUser, branchId,
                        documentDescribtion, createDate, createTime, accountStatus, blockDate, blockTime,cbiFlag,tx);
                msg.setAttribute(Fields.BLOCK_AMOUNT, String.valueOf(result[0]));
                if (result[1] == 1) {
                    holder.put(Fields.SOURCE_ACCOUNT_BALANCE, tx.getSrc_account_balance());
                    holder.put(Fields.DEST_ACCOUNT_BALANCE, tx.getDest_account_balance());
                    msg.setAttribute(Fields.HAVING_DOCUMENT, Constants.TRUE);
                } else
                    msg.setAttribute(Fields.HAVING_DOCUMENT, Constants.FALSE);
                try {
                    String organization = msg.getAttributeAsString(Fields.ORGANIZATION);
                    if (cbiFlag.equals(Constants.CBI_FLAG_ORG) && organization.equals(Constants.MAHCHECK_STATUS)) {
                        String requestType = Constants.MAHCHECK_BLOCK_SMS_TYPE;
                        //insert in tbmsgtrn
                        long chequeCode= Long.parseLong(documentDescribtion.substring(documentDescribtion.trim().length() - 10).trim());
                        CFSFacadeNew.insertMahcheckSMS(acc, chnUser, chequeCode, requestType);
                    }
                } catch (Exception e) {
                    log.error("ProcessBlockAmountCBI::Error:: "+e.getMessage());
                    log.error("ProcessBlockAmountCBI::Error in insertMahcheckSMS:: accountNo=" + acc + " BlockNo=" + blockNo);
                }
            }
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } catch (ServerAuthenticationException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
            throw new CFSFault(CFSFault.FLT_TRANSACTION_NOT_PERMITTED_TO_TERMINAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        } catch (ModelException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.NOT_SUFFICIENT_FUNDS);
            throw new CFSFault(CFSFault.FLT_NOT_SUFFICIENT_FUNDS, new Exception(ActionCode.NOT_SUFFICIENT_FUNDS));
        } catch (Exception e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }
        msg.setAttribute(CMMessage.RESPONSE, command);
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.SRC_ACCOUNT;
        }

        amountField = cfg.get(CFSConstants.AMOUNT_FIELD);
        if ((amountField == null) || (amountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Amount Field is not Specified, set to default value(amount)");
            amountField = Fields.AMOUNT;
        }

    }
}
