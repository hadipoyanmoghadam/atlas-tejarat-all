package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import branch.dpi.atlas.util.ImmediateCardUtil;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Sep 15, 2013
 * Time: 9:45:37 AM
 */
public class CheckAccountDigit extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder)  throws CMFault
    {
        BranchMessage branchMsg = (BranchMessage)msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            if(!ImmediateCardUtil.CardAccDigitsIsOK(branchMsg.getAccountNo().trim())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.SOURCE_ACCOUNT_INVALID);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.SOURCE_ACCOUNT_INVALID));
            }
        }catch (CMFault e){
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside CheckAccountDigit.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
