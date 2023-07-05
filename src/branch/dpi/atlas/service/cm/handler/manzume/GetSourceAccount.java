package branch.dpi.atlas.service.cm.handler.manzume;


import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * User:F.Heydari
 * Date:28 JAN 2023
 * time:11:13 AM
 */
public class GetSourceAccount extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            ManzumeMessage manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            String manzumeSrcAccountNo = ChannelFacadeNew.getCMParam("ManzumeBrAccountNo", "0").trim();

            msg.setAttribute(Fields.SRC_ACCOUNT, manzumeSrcAccountNo);
            command.addParam(Fields.SRC_ACCOUNT, manzumeSrcAccountNo);


        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside GetWageRequirement.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

}
