package branch.dpi.atlas.service.cm.handler;

import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 *User:F.Heydari
 *Date:10 Dec 2019
 *Time:4:37 PM
 */

public class GetSGBDescription extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String sgbDesc = "";
        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);

        try {
            String txCode = command.getParam(Fields.SGB_TX_CODE);

            sgbDesc = ChannelFacadeNew.getSgbDescription(txCode);
            if (log.isDebugEnabled()) log.debug("sgbDescription =" + sgbDesc);
            command.addParam(Fields.SGB_DESCRIPTION, sgbDesc.trim());
            command.addParam(Fields.DOCUMENT_DESCRIPTION, sgbDesc.trim());

        } catch (SQLException e) {
            log.error("Data Base error in retrieving SGBDescription with code from GetSGBDescription");
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
        } catch (Exception e) {
            log.error("Exception in GetSGBDescription:" + e.getMessage());
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}