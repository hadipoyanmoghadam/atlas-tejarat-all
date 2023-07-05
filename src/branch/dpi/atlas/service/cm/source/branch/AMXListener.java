package branch.dpi.atlas.service.cm.source.branch;

import branch.dpi.atlas.service.cm.source.branch.message.AmxMessage;
import branch.dpi.atlas.service.cm.source.branch.message.FormatException;
import branch.dpi.atlas.service.cm.source.branch.message.TouristMessage;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchSource;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandler;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.source.CMSource;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * User: R.Nasiri
 * Date: Dec 5, 2018
 * Time: 11:39 AM
 */
public class AMXListener implements BranchRequestListener {
     private CMHandler cmHandler;
     private static Log log = LogFactory.getLog(AMXListener.class);

     public boolean process(BranchSource source, String amxMsgStr) {

        CMMessage msg = new CMMessage();
        HashMap holder = new HashMap();
        try{
        // Parse MessageStr

            amxMsgStr = amxMsgStr.replaceAll("'", " ");
            amxMsgStr = amxMsgStr.replaceAll("//", "  ");
            amxMsgStr = amxMsgStr.replaceAll("/", "-");
            amxMsgStr = amxMsgStr.replaceAll("http://", "http___");
            amxMsgStr = amxMsgStr.replace('|', ' ');
            amxMsgStr = amxMsgStr.replace(':', ' ');

            AmxMessage amxMessage = AmxMessage.parseMessage(amxMsgStr);
            msg.setAttribute(CMMessage.COMMAND_OBJ, amxMessage);
            msg.setAttribute(CMMessage.MESSAGE_SOURCE, source);
            if (amxMessage.getTerminalId().equalsIgnoreCase(Constants.TERMINAL_ID_AMX))
                msg.setAttribute(CMMessage.SERVICE_TYPE, Constants.AMX_SERVICE);
            else if (amxMessage.getTerminalId().equalsIgnoreCase(Constants.TERMINAL_ID_SAFE_BOX))
                msg.setAttribute(CMMessage.SERVICE_TYPE, Constants.SAFE_BOX_SERVICE);
            else if (amxMessage.getTerminalId().equalsIgnoreCase(Constants.TERMINAL_ID_MARHONAT_INSURANCE))
                msg.setAttribute(CMMessage.SERVICE_TYPE, Constants.MARHONAT_SERVICE);
            else
                throw new FormatException("Channel Code is not valid");
            msg.setAttribute(Fields.PIN, amxMessage.getPin());

        // Put feilds into  cm_msg
        getChainHandler().process(msg, holder);

          } catch (CMFault fault) {

            CMSource cmSource = getChainHandler().getCMSource();
            CMHandler faultHandler = cmSource.getFaultHandler(fault.getFaultCode());

            try {
                msg.setAttribute(CMMessage.FAULT, fault);
                msg.setAttribute(CMMessage.FAULT_CODE, fault.getFaultCode());
                faultHandler.process(msg, holder);
            } catch (CMFault cmFault) {
                //TODO: handle Exception
                log.error(cmFault);
                try {
                    source.sendBranch(amxMsgStr);
                } catch (IOException e) {
                     log.error("Can not send response to Amx web: " + e);
                }
            }
        }
        catch (FormatException e)
        {
            log.error("Incoming AMX message has invalid format::"+amxMsgStr);
            try {
                amxMsgStr = amxMsgStr.substring(0, 38) + ActionCode.FORMAT_ERROR;
                source.sendBranch(amxMsgStr);
            } catch (IOException e1) {
                log.error("Can not send response to Amx web: " + e1);
            }
        }

        catch (Exception e) {
            //TODO: handle Exception
            try {
                log.error("Incoming AMX message has invalid format::"+amxMsgStr);
                amxMsgStr = amxMsgStr.substring(0, 38) + ActionCode.FORMAT_ERROR;
                log.fatal("Exception in AMX Listener");
                source.sendBranch(amxMsgStr);
            } catch (IOException e1) {
                log.error("Can not send response to Amx web: " + e1);
            }
        }

        return true;
    }

    public void setChainHandler(CMHandler cmHandler) {
        this.cmHandler = cmHandler;
    }
    public CMHandler getChainHandler() {
        return cmHandler;
    }


}
