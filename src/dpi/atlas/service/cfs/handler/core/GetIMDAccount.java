package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Imd;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.AccountData;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;


public class GetIMDAccount extends CFSHandlerBase implements Configurable {

    private String accountField;
    private String imdAccount;
    private String imdType;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {


        int creditDebit;
        try {
            if (log.isDebugEnabled()) log.debug(msg.getAttribute(imdType));
            Imd imd = CFSFacadeNew.getIMD(msg.getAttributeAsString(imdType));
            holder.put(CFSConstants.IMD, imd);

            String accountNo = null;
            if (imdAccount.equals(CFSConstants.DEBIT)) {
                accountNo = imd.getDebitAccountNo();
                creditDebit = dpi.atlas.service.cfs.common.AccountData.ACCOUNT_DEBIT;
            } else {
                accountNo = imd.getCreditAccountNo();
                creditDebit = AccountData.ACCOUNT_CREDIT;
            }

            if (accountNo == null || accountNo.equals("")) {
                if (log.isInfoEnabled()) log.info("IMD " + imd + " Does Not Have Cash Account");
                throw new CFSFault(CFSFault.FLT_NO_ACCOUNT_OF_TYPE_REQUESTED, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
            } else {
                holder.put(accountField, imd.getAccountData(creditDebit));
            }

        } catch (SQLException me) {
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            if (log.isInfoEnabled()) log.info("IMD " + msg.getAttribute(imdType) + " Does Not Exist");
            throw new CFSFault(CFSFault.FLT_BANK_NOT_FOUND, new Exception(ActionCode.BANK_NOT_FOUND));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        imdAccount = cfg.get(CFSConstants.IMD_ACCOUNT_FIELD);
        imdType = cfg.get(CFSConstants.IMD_TYPE);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            log.fatal("Account field is not Specified");
            throw new ConfigurationException("Account field is not Specified");
        }
        
        if ((imdAccount == null) || (imdAccount.trim().equals(""))) {
            log.fatal("IMD Account Type is not Specified");
            throw new ConfigurationException("IMD Account Type is not Specified");

        }
        if ((imdType == null) || (imdType.trim().equals(""))) {
            log.fatal("IMD Type is not Specified");
            throw new ConfigurationException("IMD Type is not Specified");
        }
    }
}