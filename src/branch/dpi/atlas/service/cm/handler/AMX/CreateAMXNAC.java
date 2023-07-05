package branch.dpi.atlas.service.cm.handler.AMX;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
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
 * Date: Dec 10, 2018
 * Time: 4:46 PM
 */
public class CreateAMXNAC extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            String origAccountTitle="";
            if(msg.hasAttribute(Fields.ORIG_ACCOUNT_TYPE))
                origAccountTitle=msg.getAttributeAsString(Fields.ORIG_ACCOUNT_TYPE);

            String sessionId=msg.getAttributeAsString(Fields.SESSION_ID);
            String channelType=msg.getAttributeAsString(CMMessage.SERVICE_TYPE);
            ChannelFacadeNew.createAMXNAC(amxMessage,origAccountTitle,sessionId,channelType);
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
