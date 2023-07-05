package branch.dpi.atlas.service.cm.handler.AMX;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: May 2, 2017
 * Time: 9:45 PM
 */
public class ValidateMessage extends TJServiceHandler implements Configurable {

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        AmxMessage amxMessage;
        amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String pin = amxMessage.getPin();

        if ((pin.equalsIgnoreCase(Constants.PIN_CREATE_NAC_AMX) || pin.equalsIgnoreCase(Constants.PIN_CHANGE_ACCOUNT_STATUS_AMX))
                && amxMessage.getAccountNo() != null && ISOUtil.isZero(amxMessage.getAccountNo())) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
        }

        if (pin.equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_SAFE_BOX) || pin.equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_REVERSE_SAFE_BOX)) {
            if (amxMessage.getSrcAccount() == null || amxMessage.getDestAccount() == null) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }

            if (ISOUtil.isZero(amxMessage.getSrcAccount()) || ISOUtil.isZero(amxMessage.getDestAccount())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }

            if (amxMessage.getSrcAccount().trim().equalsIgnoreCase(amxMessage.getDestAccount().trim())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (amxMessage.getAmount() ==null || amxMessage.getAmount().trim().equalsIgnoreCase("") || ISOUtil.isZero(amxMessage.getAmount())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (amxMessage.getRequestType() ==null || amxMessage.getRequestType().trim().equalsIgnoreCase("") || ISOUtil.isZero(amxMessage.getRequestType())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (pin.equals(Constants.PIN_CREATE_NAC_AMX)) {
                if (amxMessage.getAccountNo().compareTo(Constants.START_OMID_RANGE) >= 0 && (amxMessage.getAccountNo().compareTo(Constants.END_OMID_RANGE) <= 0)) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                }
            }
        }
    }
}
