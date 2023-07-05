package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.model.tj.entity.PayaRequest;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Aug 03, 2020
 * Time: 03:55 PM
 */
public class GetPayRequestInDueDate extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            List<PayaRequest> requestList=ChannelFacadeNew.getPayaRequestinDueDate(branchMsg.getBranchCode(),branchMsg.getDueDate());
            if(requestList==null || requestList.size()<0)
                throw new NotFoundException("");

            holder.put(Fields.PAYA_REQUEST,requestList);

        }catch(NotFoundException e){
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.PAYA_REQUEST_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.PAYA_REQUEST_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside RegisterPayRequest.doProcess(): " + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
