package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;

import java.util.Map;
import java.sql.SQLException;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: May 26, 2013
 * Time: 11:22:11 AM
 */
public class UpdateAccount extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            ChannelFacadeNew.updateAccount(branchMsg);
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside UpdateAccount.doProcess(): " + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
