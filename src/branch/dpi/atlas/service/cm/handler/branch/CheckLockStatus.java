package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Sep 15, 2013
 * Time: 3:31:14 PM
 */
public class CheckLockStatus extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String accountNo = branchMsg.getAccountNo();
        int lockStatus = 0;
        try {
            lockStatus = ChannelFacadeNew.checkLockStatus(accountNo);
            if (lockStatus != 9) { // 9 means  NAC is  created but  account is not  created yet (is not onlined yet)
                msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_HAS_BEEN_ONLINED_BEFORE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_HAS_BEEN_ONLINED_BEFORE));
            }
        }catch (CMFault e){
            throw e;
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_BALANCE_INFORMATION_NOT_AVAILABLE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_BALANCE_INFORMATION_NOT_AVAILABLE));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside checkLockStatus.doProcess(): " + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}