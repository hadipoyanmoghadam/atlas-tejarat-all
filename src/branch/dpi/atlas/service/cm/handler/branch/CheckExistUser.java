package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: Feb 17 2021
 * Time: 12:38 PM
 */
public class CheckExistUser extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map map) throws CMFault {

        String accountNo = "";
        String nationalCode = "";
        String externalId = "";
        String accountStatus="";

        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {

            if (branchMsg.getAccountStatus().equals(Constants.CBI_UNBLOCK_STATUS)) {
                return;
            }

            nationalCode = branchMsg.getNationalCode().trim();
            externalId = branchMsg.getExt_IdNumber().trim();
            accountNo = branchMsg.getAccountNo().trim();
            accountStatus=branchMsg.getAccountStatus().trim();
            try {
                accountNo = ISOUtil.zeropad(accountNo, 13);
            } catch (ISOException e) {
                log.error("Can not zeropad accountNo = '" + accountNo + "' in CheckExistUser : " + e.getMessage());
            }
            if (accountStatus.equals(Constants.CBI_BLOCK_STATUS)) {
                if (ChannelFacadeNew.checkUserExist(accountNo, nationalCode, externalId) == false) {

                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.CUSTOMER_NOT_RELATED_TO_ACCOUNT);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_RELATED_TO_ACCOUNT));
                }
            }

        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside CheckExistUser.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

}
