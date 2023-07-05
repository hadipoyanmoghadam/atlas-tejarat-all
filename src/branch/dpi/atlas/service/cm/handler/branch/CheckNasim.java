package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.imf.branch.BranchToIMFFormater;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.AtlasModel;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Apr 28, 2019
 * Time: 3:28 PM
 */
public class CheckNasim extends CMHandlerBase  {

    public void doProcess(CMMessage msg, Map holder) throws CMFault{

        BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {

            if (ChannelFacadeNew.findCFSAccount(branchMsg.getAccountNo())){
                msg.setAttribute(Fields.IS_CFS, true);
            }else{
                msg.setAttribute(Fields.IS_CFS, false);
                msg.setAttribute(Fields.IS_POSHTIBAN,Constants.IS_NOT_BACKUP_ACCOUNT);
            }
        } catch (SQLException e) {
            log.error("Error1 in handler: CheckNasim.doProcess()" + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("Error2 in handler: CheckNasim.doProcess()" + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
