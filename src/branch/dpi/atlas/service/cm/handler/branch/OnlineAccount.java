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
 * Time: 3:31:14 PM
 */
public class OnlineAccount extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            String session_id=msg.getAttributeAsString(Fields.SESSION_ID);
            ChannelFacadeNew.onlineAccount(branchMsg,session_id);
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside onlineAccount.doProcess(): " + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
