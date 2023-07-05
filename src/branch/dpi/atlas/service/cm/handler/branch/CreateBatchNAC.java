package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;

import java.sql.SQLException;
import java.util.Map;

/**
 * User:R.Nasiri
 * Date: April 4, 2015
 * Time: 4:22 PM
 */
public class CreateBatchNAC extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            String origAccountTitle="";
            if(msg.hasAttribute(Fields.ORIG_ACCOUNT_TYPE))
                origAccountTitle=msg.getAttributeAsString(Fields.ORIG_ACCOUNT_TYPE);

            String sessionId=msg.getAttributeAsString(Fields.SESSION_ID);
            String channelType=msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
            ChannelFacadeNew.createBatchNAC(branchMsg,origAccountTitle,sessionId,channelType);
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
