package branch.dpi.atlas.service.cm.handler.SMS;

import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
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
 * Date: NOv 17 2019
 * Time: 1:30 PM
 */
public class DisableSMS extends CMHandlerBase {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        log.debug("inside DisableSMS.doProcess()");
        SMSMessage smsMessage = null;
        smsMessage = (SMSMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        String accountNumber = smsMessage.getAccountNo().trim();
        try {
             ChannelFacadeNew.disableSMS(accountNumber);

        } catch (SQLException e) {
            log.error("SQException :::Inside disableSMS.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND );
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (Exception e) {
            log.error("ERROR :::Inside disableSMS.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }


    }

}

