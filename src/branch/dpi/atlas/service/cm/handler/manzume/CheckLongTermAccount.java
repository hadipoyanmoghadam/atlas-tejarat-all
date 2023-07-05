package branch.dpi.atlas.service.cm.handler.manzume;

import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: NOV 1 2022
 * Time: 10:37 AM
 */
public class CheckLongTermAccount extends CMHandlerBase {


    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        ManzumeMessage manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String accountGroup = (String) msg.getAttribute(Fields.ACCOUNT_GROUP);

        try {
            if (ChannelFacadeNew.getLongTermGroup(Constants.MANZUME_LONGTERM_CMPARAM_MODIFIRE, accountGroup) == true) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
            }
        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CheckLongTermAccount.doProcess(): " + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

}
