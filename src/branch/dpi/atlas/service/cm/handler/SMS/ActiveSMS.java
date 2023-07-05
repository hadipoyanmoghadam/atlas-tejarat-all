package branch.dpi.atlas.service.cm.handler.SMS;

import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: F.Heydari
 * Date: Dec 07 2019
 * Time: 2:16 PM
 */
public class ActiveSMS extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        log.debug("inside ActiveSMS.doProcess()");


        SMSMessage smsMessage = null;
        smsMessage = (SMSMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);


        CMResultSet result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);

        String actionCode = null;
        if (result != null) actionCode = result.getHeaderField(Fields.ACTION_CODE);
        else
            actionCode = "";


        String accountNumber = smsMessage.getAccountNo().trim();
        String pin = smsMessage.getPin();

        try {

            if ((actionCode.equals(ActionCode.APPROVED) && pin.equals(Constants.PIN_WAGE_SMS)) || (!pin.equals(Constants.PIN_WAGE_SMS)))
                ChannelFacadeNew.activeSMS(accountNumber);


        } catch (SQLException e) {
            log.error("SQLException:: "+ e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (Exception e) {
            log.error("ERROR :::Inside ActiveSMS.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }


    }

}

