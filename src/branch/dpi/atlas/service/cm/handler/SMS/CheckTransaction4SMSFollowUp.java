package branch.dpi.atlas.service.cm.handler.SMS;

import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * User:F.Heydari
 * Date:10 Dec 2019
 * Time:5:21 PM
 */


public class CheckTransaction4SMSFollowUp extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        SMSMessage smsMessage = (SMSMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        if (!smsMessage.getPin().equalsIgnoreCase(Constants.PIN_FOLLOWUP_SMS))
            return;

        String serviceType = msg.getAttributeAsString(CMMessage.SERVICE_TYPE);

        try {
            String origMessageSequence = smsMessage.getOrigMessageData().substring(0, 12);
            List<String> branchLogSet = ChannelFacadeNew.findServiceLogs(origMessageSequence, serviceType);
            if (branchLogSet.size() < 1)
                throw new NotFoundException(ActionCode.FLW_HAS_NO_ORIGINAL);

        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.FLW_HAS_NO_ORIGINAL);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.FLW_HAS_NO_ORIGINAL);
        } catch (SQLException e) {
            log.error(e);
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, ActionCode.DATABASE_ERROR);
        } catch (Exception e) {
            log.error(":::Inside CheckTransaction4sSMSFollowUp.doProcess >>" + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.GENERAL_ERROR);
        }
    }

}
