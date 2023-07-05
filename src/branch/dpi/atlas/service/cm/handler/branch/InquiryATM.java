package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.ModelException;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;


import java.sql.SQLException;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: August 27 2019
 * Time: 11:51 AM
 */
public class InquiryATM extends CMHandlerBase {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        log.debug("inside InquiryATM.doProcess()");
        BranchMessage branchMsg = null;
        branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String accountNumber = branchMsg.getAccountNo().trim();
        try {
            String[] inquiry = ChannelFacadeNew.inquiryATM(accountNumber);
            if (inquiry.length==2) {
                branchMsg.setBranchCode(inquiry[0]);
                branchMsg.setBalance(inquiry[1]);
                branchMsg.setMaxATM_Balance("000000000000000000");
                branchMsg.setAvgDeficit("000000000000000000");
            }else{
                throw new NotFoundException("EXP in InquiryATM:: Can not find elements for device");
            }

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DEVICE_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DEVICE_NOT_FOUND));
        } catch (ModelException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
        } catch (Exception e) {
            log.error("ERROR :::Inside InquiryATM.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
