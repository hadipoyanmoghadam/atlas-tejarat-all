package branch.dpi.atlas.service.cm.source.branch;

import branch.dpi.atlas.service.cm.source.branch.message.CreditsMessage;
import branch.dpi.atlas.service.cm.source.branch.message.FormatException;
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
 * Date: Jun 13, 2015
 * Time: 11:46 AM
 */
public class CreditsListener implements BranchRequestListener {
     private CMHandler cmHandler;
     private static Log log = LogFactory.getLog(CreditsListener.class);

     public boolean process(BranchSource source, String creditMsgStr) {

        CMMessage msg = new CMMessage();
        HashMap holder = new HashMap();
        try{
        // Parse branchMessageStr

            creditMsgStr = creditMsgStr.replaceAll("'", " ");
            creditMsgStr = creditMsgStr.replaceAll("//", "  ");
            creditMsgStr = creditMsgStr.replaceAll("/", "-");
            creditMsgStr = creditMsgStr.replaceAll("http://", "http___");
            creditMsgStr = creditMsgStr.replace('|', ' ');
            creditMsgStr = creditMsgStr.replace(':', ' ');

            CreditsMessage creditMessage = CreditsMessage.parseMessage(creditMsgStr);
            creditMessage.setTerminalId(Constants.TERMINAL_ID_CREDITS);
            msg.setAttribute(CMMessage.COMMAND_OBJ, creditMessage);
            msg.setAttribute(CMMessage.MESSAGE_SOURCE, source);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Constants.CREDITS_SERVICE);
            msg.setAttribute(Fields.PIN, creditMessage.getPin());

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
                    source.sendBranch(creditMsgStr);
                } catch (IOException e) {
                     log.error("Can not send response to Credit Web Service: " + e);
                }
            }
        }
        catch (FormatException e)
        {
            log.error("Incoming Credits message has invalid format::"+creditMsgStr);
            try {
                creditMsgStr = creditMsgStr.substring(0, 70) + ActionCode.FORMAT_ERROR;
                source.sendBranch(creditMsgStr);
            } catch (IOException e1) {
                log.error("Can not send response to Credit Web Service: " + e1);
            }
        }

        catch (Exception e) {
            //TODO: handle Exception
            try {
                log.error("Incoming Credits message has invalid format::"+creditMsgStr);
                creditMsgStr = creditMsgStr.substring(0, 70) + ActionCode.FORMAT_ERROR;
                log.fatal("Exception in Credit Web Service");
                source.sendBranch(creditMsgStr);
            } catch (IOException e1) {
                log.error("Can not send response to Credit Web Service: " + e1);
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
