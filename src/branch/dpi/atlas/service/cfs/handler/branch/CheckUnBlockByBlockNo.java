package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.facade.cm.ServerAuthenticationException;
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
 * Time: 01:53:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class CheckUnBlockByBlockNo extends CFSHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(CheckUnBlockByBlockNo.class);
    protected String accountField;
    protected String amountField;

    public CheckUnBlockByBlockNo() {
        accountField = Fields.SRC_ACCOUNT;
        amountField = Fields.AMOUNT;
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {
        String brokerId="";
        String proverId="";
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String serviceType = (String) msg.getAttribute(Fields.SERVICE_TYPE);
        String acc = msg.getAttributeAsString(accountField);
        try {
            acc = ISOUtil.zeropad(acc, 13);
            if (log.isDebugEnabled()) log.debug(accountField + "=" + acc);
        } catch (ISOException e) {
            log.error("Can not zeropad account number = '" + acc + "' in CheckAccountNew : " + e.getMessage());
        }
        try {
        long amount = Long.parseLong((String) msg.getAttribute(amountField));
        String blockNo;

            blockNo = msg.getAttributeAsString(Fields.BLOCK_ROW);
            String branchId = msg.getAttributeAsString(Fields.BRANCH_CODE).trim();
            if(serviceType.equals(Constants.BRANCH_SERVICE)){
                brokerId=Constants.BROKER_ID_BRANCH;
                proverId=Constants.PROVIDER_ID_BRANCH;
            }else{ //Simin
                brokerId=Constants.BROKER_ID_SIMIN;
                proverId=Constants.PROVIDER_ID_SIMIN;
            }
            String[] describeAmount=new String[2];
            describeAmount=CFSFacadeNew.checkBlockStAcc(blockNo, brokerId,proverId, acc,branchId,serviceType);
            long blockAmount =Long.valueOf(describeAmount[0]);
            if (amount != blockAmount) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_AMOUNT);
                throw new CFSFault(CFSFault.FLT_INVALID_AMOUNT, new Exception(ActionCode.INVALID_AMOUNT));
            }
            msg.setAttribute(Fields.BLOCK_DESC,describeAmount[1].trim());
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.NO_REALTED_BLCK_EXIST);
            throw new CFSFault(CFSFault.NO_REALTED_BLCK_EXIST, new Exception(ActionCode.NO_REALTED_BLCK_EXIST));
        } catch (ServerAuthenticationException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
            throw new CFSFault(CFSFault.FLT_TRANSACTION_NOT_PERMITTED_TO_TERMINAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        } catch (CFSFault e) {
            throw e;
        } catch (Exception e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }
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
