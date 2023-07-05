package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Branch;
import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;
import java.sql.SQLException;

/**
 * @author <a href="mailto:behnaz@dpi.ir">SH Behnaz</a>
 * @version $Revision: 1.0 $ $Date: 2013/06/02 15:17:49 $
 */

public class GetBranchAccount extends CFSHandlerBase implements Configurable {

    protected String accountField;

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String branchCode=msg.getAttributeAsString(Fields.BRANCH_CODE).trim();

        Branch branch;
        try {
            if (log.isDebugEnabled()) log.debug("branchCode=" + branchCode);
            if (branchCode != null && !branchCode.equals(""))
                branch = CFSFacadeNew.getBranch(branchCode);
            else
                throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);

            String messageType = msg.getAttributeAsString(Fields.MESSAGE_TYPE);
            if (messageType != null && messageType.trim().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_REMITTANCE)){
                if (branch.getFeeAccountNo() != null && !branch.getFeeAccountNo().trim().equalsIgnoreCase(""))
                    msg.setAttribute(Fields.SRC_ACCOUNT, branch.getFeeAccountNo());
                else
                    throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);
            }
            if (messageType != null && messageType.trim().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE)) {
                if (branch.getAchAccountNo() != null && !branch.getAchAccountNo().trim().equalsIgnoreCase("")){
                    msg.setAttribute(Fields.DEST_ACCOUNT, branch.getAchAccountNo());
                    msg.setAttribute(Fields.TOTAL_DEST_ACCOUNT, branch.getAchAccountNo());
                }else
                    throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);
            } else if (messageType != null && messageType.trim().equalsIgnoreCase(TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE)){
                if (branch.getRtgsAccountNo() != null && !branch.getRtgsAccountNo().trim().equalsIgnoreCase("")){
                    msg.setAttribute(Fields.DEST_ACCOUNT, branch.getRtgsAccountNo());
                    msg.setAttribute(Fields.TOTAL_DEST_ACCOUNT, branch.getRtgsAccountNo());
                }else
                    throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);
            }else{
                msg.setAttribute(accountField, branch.getAccountNo());
                if(accountField.equals(Fields.DEST_ACCOUNT))
                    msg.setAttribute(Fields.TOTAL_DEST_ACCOUNT, branch.getAccountNo());
            }
        }
        catch (ModelException me) {
            log.error(me);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, me);
        } catch (NotFoundException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_BRANCH_NOT_FOUND, ActionCode.BRANCH_NOT_FOUND);
        } catch (SQLException e) {
            if (log.isDebugEnabled()) log.debug(e);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, ActionCode.DATABASE_ERROR);
        }

        holder.put(Fields.BRANCH, branch);
        holder.put(accountField, branch.getAccountData());

    }


    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
        accountField = cfg.get(CFSConstants.ACCOUNT_FIELD);
        if ((accountField == null) || (accountField.trim().equals(""))) {
            log.fatal("Account field is not Specified");
            throw new ConfigurationException("Account field is not Specified");
        }
    }
}
