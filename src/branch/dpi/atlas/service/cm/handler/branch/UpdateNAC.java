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
 * Time: 1:20:37 PM
 */
public class UpdateNAC extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String customerId = "";
        String origAccountTitle="";
        try {
            if (!holder.containsKey(Fields.BRANCH_CUSTOMER_ID)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
            }
            customerId = (String) holder.get(Fields.BRANCH_CUSTOMER_ID);

            if(msg.hasAttribute(Fields.ORIG_ACCOUNT_TYPE))
                origAccountTitle=msg.getAttributeAsString(Fields.ORIG_ACCOUNT_TYPE);

            String sessionId = msg.getAttributeAsString(Fields.SESSION_ID);
            ChannelFacadeNew.updateNAC(branchMsg, customerId,sessionId,msg.getAttributeAsString(CMMessage.SERVICE_TYPE),origAccountTitle);
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside UpdateNac.doProcess(): " + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
