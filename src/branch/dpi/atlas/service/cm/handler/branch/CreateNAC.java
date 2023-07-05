package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: H.Ghayoumi
 * Modified by user R.Nasiri
 * Date: May 21, 2013
 * Time: 9:45:37 AM
 */
public class CreateNAC extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            String origAccountTitle = "";
            if (msg.hasAttribute(Fields.ORIG_ACCOUNT_TYPE))
                origAccountTitle = msg.getAttributeAsString(Fields.ORIG_ACCOUNT_TYPE);
            String sessionId=msg.getAttributeAsString(Fields.SESSION_ID);
            String channelType=msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
            ChannelFacadeNew.createNAC(branchMsg, origAccountTitle,sessionId,channelType);
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CreateNAC.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
