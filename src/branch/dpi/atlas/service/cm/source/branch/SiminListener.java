package branch.dpi.atlas.service.cm.source.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
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
public class SiminListener implements BranchRequestListener {
     private CMHandler cmHandler;
     private static Log log = LogFactory.getLog(SiminListener.class);

     public boolean process(BranchSource source, String branchMsgStr) {

        CMMessage msg = new CMMessage();
        HashMap holder = new HashMap();
        try{
        // Parse branchMessageStr

            branchMsgStr = branchMsgStr.replaceAll("'", " ");
            branchMsgStr = branchMsgStr.replaceAll("//", "  ");
            branchMsgStr = branchMsgStr.replaceAll("/", "-");
            branchMsgStr = branchMsgStr.replaceAll("http://", "http___");
            branchMsgStr = branchMsgStr.replace('|', ' ');
            branchMsgStr = branchMsgStr.replace(':', ' ');

            BranchMessage branchMsg = BranchMessage.parseMessage(branchMsgStr);
            branchMsg.setTerminalId(Constants.TERMINAL_ID_SIMIN);
            msg.setAttribute(CMMessage.COMMAND_OBJ, branchMsg);
            msg.setAttribute(CMMessage.MESSAGE_SOURCE, source);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Constants.SIMIN_SERVICE);
            msg.setAttribute(Fields.PIN, branchMsg.getPin());
            msg.setAttribute(Fields.SIMIN_USER_ID, branchMsg.getUserId());

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
                    source.sendBranch(branchMsgStr);
                } catch (IOException e) {
                     log.error("Can not send response to Branch Manager: " + e);
                }
            }
        }
        catch (FormatException e)
        {
            log.error("Incoming Branch message has invalid format::"+branchMsgStr);
            try {
                branchMsgStr = branchMsgStr.substring(0, 60) + ActionCode.FORMAT_ERROR;
                source.sendBranch(branchMsgStr);
//                source.sendBranch(ActionCode.FORMAT_ERROR);
            } catch (IOException e1) {
                log.error("Can not send response to Branch Manager: " + e1);
            }
        }

        catch (Exception e) {
            //TODO: handle Exception
            try {
                log.error("Incoming Branch message has invalid format::"+branchMsgStr);
                branchMsgStr = branchMsgStr.substring(0, 60) + ActionCode.FORMAT_ERROR;
                log.fatal("Exception in Branch Listener");
                source.sendBranch(branchMsgStr);
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
