package branch.dpi.atlas.service.cm.handler.AMX;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Sep 17, 2019
 * Time: 9:37 PM
 */

public class FollowUpFlowId extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        log.debug("inside FollowUpFlowId.doProcess(CMMessage msg, Map holder)");

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String SrcHostIdStr = command.getParam(Constants.SRC_HOST_ID);
        String DestHostIdStr = command.getParam(Constants.DEST_HOST_ID);
        String pin = msg.getAttributeAsString(Fields.ORIG_PIN);
        String followId = null;
        String serviceType = msg.getAttributeAsString(CMMessage.SERVICE_TYPE);

        if (serviceType.equalsIgnoreCase(Constants.SAFE_BOX_SERVICE)) {
            if (pin.equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_SAFE_BOX) || pin.equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_SAFE_BOX_RESPONSE)) {
                // transaction is FT then we have C2C or F2C or C2O or F2o
                if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    followId = "1";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    followId = "2";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_SGB))
                    followId = "1";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_SGB))
                    followId = "3";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    followId = "4";
                else {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                }
            } else if (pin.equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_REVERSE_SAFE_BOX) || pin.equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_REVERSE_SAFE_BOX_RESPONSE)) {
                // transaction is FTR then we have C2C or F2C or C2O or F2o
                if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    followId = "1";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    followId = "4";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_SGB) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    followId = "1";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_SGB) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    followId = "3";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    followId = "2";
                else {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                }
            } else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
            }
        } else if (serviceType.equalsIgnoreCase(Constants.MARHONAT_SERVICE)) {
            if (pin.equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_SAFE_BOX) || pin.equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_SAFE_BOX_RESPONSE)) {
                // transaction is FT then we have C2C or F2C or C2F or F2F

                if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    followId = "1";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    followId = "2";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    followId = "3";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    followId = "4";
                else {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                }
            } else if (pin.equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_REVERSE_SAFE_BOX) || pin.equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_REVERSE_SAFE_BOX_RESPONSE)) {
                // transaction is FTR then we have C2C or F2C or C2F or F2F

                if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    followId = "1";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    followId = "2";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    followId = "3";
                else if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    followId = "4";
                else {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                }
            } else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
            }

        } else {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
        }
        if (followId != null)
            msg.setAttribute(Constants.FOLLOW_TYPE, followId);
    }
}


