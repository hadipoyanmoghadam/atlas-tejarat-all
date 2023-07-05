package branch.dpi.atlas.service.cm.handler.manzume;


import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
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
 * Date:OCT 30 2022
 * Time:13:22 PM
 */
public class ValidateMessage  extends TJServiceHandler implements Configurable {

    public void setConfiguration(Configuration configuration) throws ConfigurationException {
    }

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        ManzumeMessage manzumeMessage;
        manzumeMessage = (ManzumeMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        String pin = manzumeMessage.getPin();

        if ((pin.equalsIgnoreCase(Constants.PIN_ACCOUNT_INFO_MANZUME) )
                && manzumeMessage.getAccountNo() != null && ISOUtil.isZero(manzumeMessage.getAccountNo())) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
        }

        if (pin.equalsIgnoreCase(Constants.PIN_ACCOUNT_INFO_MANZUME) || pin.equalsIgnoreCase(Constants.PIN_DEPOSIT_MANZUME)||
                pin.equalsIgnoreCase(Constants.PIN_DEPOSIT_REVERSE_MANZUME) || pin.equalsIgnoreCase(Constants.PIN_FOLLOWUP_MANZUME)
                ) {


            if (manzumeMessage.getAccountNo() == null ) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }

            if (ISOUtil.isZero(manzumeMessage.getAccountNo())) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if( ISOUtil.isZero(manzumeMessage.getAccountNo()))
            {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
            if (manzumeMessage.getMessageSequence() == null ) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }
        if ( pin.equalsIgnoreCase(Constants.PIN_DEPOSIT_MANZUME)||
                pin.equalsIgnoreCase(Constants.PIN_DEPOSIT_REVERSE_MANZUME) )
        {
            if (manzumeMessage.getAmount() == null ) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }

            if( ISOUtil.isZero(manzumeMessage.getAmount()))
            {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }
        if(pin.equalsIgnoreCase(Constants.PIN_FOLLOWUP_MANZUME)){
            if(manzumeMessage.getOrigMessageData()== null)
            {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.FORMAT_ERROR);
                throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.FORMAT_ERROR));
            }
        }
    }
}


