package branch.dpi.atlas.service.cm.handler.SMS;

import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * User:F.Heydari
 * Date:Nov 16 2019
 * Time:11:22 AM
 */
public class ValidateMessage  extends TJServiceHandler implements Configurable {

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        SMSMessage smsMessage;
        smsMessage = (SMSMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String pin = smsMessage.getPin();

        if ((pin.equalsIgnoreCase(Constants.PIN_GET_BALANCE_SMS) || pin.equalsIgnoreCase(Constants.PIN_WAGE_SMS)||
                pin.equalsIgnoreCase(Constants.PIN_ACTIVE_SMS) || pin.equalsIgnoreCase(Constants.PIN_DISABLE_SMS)||
                pin.equalsIgnoreCase(Constants.PIN_FOLLOWUP_SMS))
                && smsMessage.getAccountNo() != null && ISOUtil.isZero(smsMessage.getAccountNo())) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
        }

        if (pin.equalsIgnoreCase(Constants.PIN_GET_BALANCE_SMS) || pin.equalsIgnoreCase(Constants.PIN_WAGE_SMS)||
                pin.equalsIgnoreCase(Constants.PIN_ACTIVE_SMS) || pin.equalsIgnoreCase(Constants.PIN_DISABLE_SMS)||
                pin.equalsIgnoreCase(Constants.PIN_FOLLOWUP_SMS)) {


            if (smsMessage.getAccountNo() == null ) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }

            if (ISOUtil.isZero(smsMessage.getAccountNo())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if(smsMessage.getRequestId() != null && ISOUtil.isZero(smsMessage.getAccountNo()))
            {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (smsMessage.getMessageSequence() == null ) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            }
        if(pin.equalsIgnoreCase(Constants.PIN_FOLLOWUP_SMS)){
            if(smsMessage.getOrigMessageData()== null)
            {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }
        }
    }


