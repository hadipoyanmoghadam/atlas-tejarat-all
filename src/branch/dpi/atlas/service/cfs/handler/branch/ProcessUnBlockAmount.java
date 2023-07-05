package branch.dpi.atlas.service.cfs.handler.branch;

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
 * User: R.Nasiri
 * Date: Nov 16, 2013
 * Time: 2:33:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessUnBlockAmount extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(ProcessUnBlockAmount.class);
    protected String accountField;
    protected String amountField;

    public ProcessUnBlockAmount() {
        accountField = Fields.SRC_ACCOUNT;
        amountField = Fields.AMOUNT;
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String serviceType = (String) msg.getAttribute(Fields.SERVICE_TYPE);
        String acc = msg.getAttributeAsString(accountField);
        try {
            acc = ISOUtil.zeropad(acc, 13);
            if (log.isDebugEnabled()) log.debug(accountField + "=" + acc);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + acc + "' in CheckAccountNew : " + e.getMessage());
        }

        long blockedAmount = Long.parseLong((String) msg.getAttribute(amountField));
        String branchId = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
        String blockNo;
        String blockDesc;
        String unBlockDesc;
        String blockDate;
        String blockTime;
        String brokerId="";
        String proverId="";
        String chnUser="";
        String createDate = DateUtil.getSystemDate();
        String createTime = DateUtil.getSystemTime();
        try {
            blockNo = msg.getAttributeAsString(Fields.BLOCK_ROW);
            unBlockDesc = msg.getAttributeAsString(Fields.DOCUMENT_DESCRIPTION).trim();
            blockDesc = msg.getAttributeAsString(Fields.BLOCK_DESC);
            blockDate = msg.getAttributeAsString(Constants.BLCK_DATE);
            blockTime = msg.getAttributeAsString(Constants.BLCK_TIME);
            Tx tx= (Tx) msg.getAttribute(Constants.WAGE_TX);
            if(serviceType.equals(Constants.BRANCH_SERVICE)){
                brokerId=Constants.BROKER_ID_BRANCH;
                proverId=Constants.PROVIDER_ID_BRANCH;
                chnUser=Constants.CHN_USER_BRANCH;
            }else{ //Simin
                brokerId=Constants.BROKER_ID_SIMIN;
                proverId=Constants.PROVIDER_ID_SIMIN;
                chnUser=Constants.CHN_USER_SIMIN;

                branchId = msg.getAttributeAsString(Fields.USER_ID);
                if (branchId == null) branchId = "";
            }

            String documentStatus=msg.getAttributeAsString(Fields.WAGE_WITH_DOCUMENT);
            if(documentStatus==null)
                documentStatus="";

            boolean havingDocument=CFSFacadeNew.doUnBlockAmountTransactional(acc, blockNo, brokerId,proverId, blockedAmount, branchId, chnUser,
                    "", blockDesc, createDate, createTime,blockDate,blockTime,unBlockDesc,tx,documentStatus);
            if (havingDocument==true) {
                holder.put(Fields.SOURCE_ACCOUNT_BALANCE, tx.getSrc_account_balance());
                holder.put(Fields.DEST_ACCOUNT_BALANCE, tx.getDest_account_balance());
                msg.setAttribute(Fields.HAVING_DOCUMENT, Constants.TRUE);
            } else
                msg.setAttribute(Fields.HAVING_DOCUMENT, Constants.FALSE);

        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
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
