package branch.dpi.atlas.service.cm.source.branch;


import branch.dpi.atlas.service.cm.source.branch.message.FormatException;
import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
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
 * User: F.Heydari
 * Date: NOV ,13, 2019
 * Time: 9:33:33 AM
 */
public class SMSListener implements BranchRequestListener {
    private CMHandler cmHandler;
    private static Log log = LogFactory.getLog(SMSListener.class);

    public boolean process(BranchSource source, String smsMsgStr) {

        CMMessage msg = new CMMessage();
        HashMap holder = new HashMap();
        try {
            // Parse SMSMessageStr

            smsMsgStr = smsMsgStr.replaceAll("'", " ");
            smsMsgStr = smsMsgStr.replaceAll("//", "  ");
            smsMsgStr = smsMsgStr.replaceAll("/", "-");
            smsMsgStr = smsMsgStr.replaceAll("http://", "http___");
            smsMsgStr = smsMsgStr.replace('|', ' ');
            smsMsgStr = smsMsgStr.replace(':', ' ');
            SMSMessage smsMessage= SMSMessage.parseMessage(smsMsgStr);
            smsMessage.setTerminalId(Constants.TERMINAL_ID_SMS);
            msg.setAttribute(CMMessage.COMMAND_OBJ, smsMessage);
            msg.setAttribute(CMMessage.MESSAGE_SOURCE, source);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Constants.SMS_SERVICE);

            msg.setAttribute(Fields.PIN,smsMessage.getPin());

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
                    source.sendBranch(smsMsgStr);
                } catch (IOException e) {
                    log.error("Can not send response to Branch Manager: " + e);
                }
            }
        } catch (FormatException e) {
            log.error("Incoming SMS message has invalid format::" + smsMsgStr);
            try {
                smsMsgStr = smsMsgStr.substring(0, 50) + ActionCode.FORMAT_ERROR;
                source.sendBranch(smsMsgStr);
            } catch (IOException e1) {
                log.error("Can not send response to Branch Manager: " + e1);
            }
        } catch (Exception e) {
            //TODO: handle Exception
            try {
                log.error("Incoming SMS message has invalid format::" + smsMsgStr);
                smsMsgStr = smsMsgStr.substring(0, 50) + ActionCode.FORMAT_ERROR;
                log.fatal("Exception in SMS Listener");
                source.sendBranch(smsMsgStr);
            } catch (IOException e1) {
                log.error("Can not send response to Branch Manager: " + e1);
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
