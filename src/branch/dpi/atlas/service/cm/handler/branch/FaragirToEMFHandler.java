package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: April 28, 2019
 * Time: 3:56 PM
 */
public class FaragirToEMFHandler extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        CMResultSet result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);
        String actionCode = null;
        if (result != null)
            actionCode = result.getHeaderField(Fields.ACTION_CODE);

        if (log.isDebugEnabled()) log.debug("actionCode:" + actionCode);

        if (actionCode == null) {
            actionCode = ActionCode.GENERAL_ERROR;
            log.error("Returned ActionCode is null");
        }


        if (actionCode.equals(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED)){
            msg.setAttribute(Fields.IS_FARAGIR,false);
            msg.setAttribute(Fields.IS_POSHTIBAN,Constants.IS_NOT_BACKUP_ACCOUNT);
            actionCode = ActionCode.APPROVED;
            msg.setAttribute(Fields.ACTION_CODE, actionCode);
            return;
        }

        msg.setAttribute(Fields.ACTION_CODE, actionCode);

        try {
            if (!actionCode.startsWith("00"))
                return;

            result.moveFirst();
            result.next();

            msg.setAttribute(Fields.IS_FARAGIR,true);
            msg.setAttribute(Fields.IS_POSHTIBAN,result.getString("BACKUPSTATUS"));


        } catch (Exception e) {
            log.error("ERROR :::Inside FaragirToEMFHandler.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

