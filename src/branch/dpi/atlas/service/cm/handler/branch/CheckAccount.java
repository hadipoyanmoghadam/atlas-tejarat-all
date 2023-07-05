package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: July 7 2013
 * Time: 2:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class CheckAccount extends TJServiceHandler implements Configurable {
    protected String accountField;
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        String accountNo = "";
        Account account = null;
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            accountNo=branchMsg.getAccountNo();
            account = ChannelFacadeNew.getAccount(accountNo);
            map.put(Constants.CUSTOMER_ACCOUNT, account);
            msg.setAttribute(accountField, account.getAccountNo());
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside checkAccount.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            if (log.isInfoEnabled()) log.info("Account Field is not Specified, set to default value(srcAccount)");
            accountField = Fields.SRC_ACCOUNT;
        }
    }
}
