package branch.dpi.atlas.service.cm.handler.SMS;

import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;

import java.sql.SQLException;
import java.util.Map;

/**
 * user:F.Heydari
 * 10 Dec 2019
 * 11:15 AM
 */
public class GetWageAccount extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map map) throws CMFault {
        try {
            Map<Long, String> SMSmap = ChannelFacadeNew.fillCMParamMap("SMSWageAccountNumber");
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            command.addParam(Fields.DEST_ACCOUNT, SMSmap.get("DESCRIPTION"));
            command.addParam(Fields.AMOUNT, SMSmap.get("ID"));

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside GetWageAccount.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }
}

