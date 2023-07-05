package branch.dpi.atlas.service.cm.handler;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Jul 13, 2019
 * Time: 3:14 PM
 */

public class getHostType extends TJServiceHandler implements Configurable {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        log.debug("inside getMessageType.doProcess(CMMessage msg, Map holder)");

        CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
        String SrcHostIdStr = command.getParam(Constants.SRC_HOST_ID);
        String DestHostIdStr = command.getParam(Constants.DEST_HOST_ID);
        AmxMessage amxMessage = (AmxMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        if (amxMessage.getPin().equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_SAFE_BOX)) {
            // transaction is FT then we have C2C or F2C or C2O or F2o

            if (amxMessage.getTerminalId().equalsIgnoreCase(Constants.TERMINAL_ID_MARHONAT_INSURANCE)) { //C2C or F2C or C2F or F2F
                // request type > 5 is Marhoonat insurance
                if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    msg.setAttribute(Constants.Host_Type, Constants.Host_Type_C2C);

                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    msg.setAttribute(Constants.Host_Type, Constants.Host_Type_F2C);

                else if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    msg.setAttribute(Constants.Host_Type, Constants.Host_Type_C2F);

                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    msg.setAttribute(Constants.Host_Type, Constants.Host_Type_F2F);

                else {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                }

            } else if (amxMessage.getTerminalId().equalsIgnoreCase(Constants.TERMINAL_ID_SAFE_BOX)) {

                if (!amxMessage.getRequestType().equalsIgnoreCase(Constants.STAMP_DOCUMENT)) { //C2C or F2C or C2F
                    // other request type is safe box
                    if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                        msg.setAttribute(Constants.Host_Type, Constants.Host_Type_C2C);

                    else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                        msg.setAttribute(Constants.Host_Type, Constants.Host_Type_F2C);

                    else if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                        msg.setAttribute(Constants.Host_Type, Constants.Host_Type_C2F);

                    else {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                    }
                } else {
                    //C2S or F2S
                    if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_SGB))
                        msg.setAttribute(Constants.Host_Type, Constants.Host_Type_C2S);

                    else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_SGB))
                        msg.setAttribute(Constants.Host_Type, Constants.Host_Type_F2S);
                    else {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));

                    }
                }
            } else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
            }
        } else if (amxMessage.getPin().equalsIgnoreCase(Constants.PIN_FUND_TRANSFER_REVERSE_SAFE_BOX)) {
            // transaction is FTR then we have C2C or F2C or C2O or F2o
            if (amxMessage.getTerminalId().equalsIgnoreCase(Constants.TERMINAL_ID_MARHONAT_INSURANCE)) { //C2C or F2C or C2F or F2F
                // request type > 5 is Marhoonat insurance
                if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    msg.setAttribute(Constants.Host_Type, Constants.Host_Type_C2C);

                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                    msg.setAttribute(Constants.Host_Type, Constants.Host_Type_F2C);

                else if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    msg.setAttribute(Constants.Host_Type, Constants.Host_Type_C2F);

                else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                    msg.setAttribute(Constants.Host_Type, Constants.Host_Type_F2F);

                else {
                    msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                }

            } else if (amxMessage.getTerminalId().equalsIgnoreCase(Constants.TERMINAL_ID_SAFE_BOX)) {
                if (!amxMessage.getRequestType().equalsIgnoreCase(Constants.STAMP_DOCUMENT)) { //C2C or C2F or F2C
                    if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                        msg.setAttribute(Constants.Host_Type, Constants.Host_Type_C2C);

                    else if (SrcHostIdStr.equals(Constants.HOST_ID_CFS) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                        msg.setAttribute(Constants.Host_Type, Constants.Host_Type_C2F);

                    else if (SrcHostIdStr.equals(Constants.HOST_ID_FARAGIR) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                        msg.setAttribute(Constants.Host_Type, Constants.Host_Type_F2C);

                    else {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                    }
                } else {
                    //O2C or O2F
                    if (SrcHostIdStr.equals(Constants.HOST_ID_SGB) && DestHostIdStr.equals(Constants.HOST_ID_CFS))
                        msg.setAttribute(Constants.Host_Type, Constants.Host_Type_S2C);

                    else if (SrcHostIdStr.equals(Constants.HOST_ID_SGB) && DestHostIdStr.equals(Constants.HOST_ID_FARAGIR))
                        msg.setAttribute(Constants.Host_Type, Constants.Host_Type_S2F);
                    else {
                        msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                        throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
                    }
                }
            } else {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));

            }
        } else {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.TRANSACTION_NOT_PERMITTED_TO_TERMINAL));
        }
    }
}
