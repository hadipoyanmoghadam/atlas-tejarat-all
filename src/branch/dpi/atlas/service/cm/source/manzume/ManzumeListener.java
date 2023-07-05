package branch.dpi.atlas.service.cm.source.manzume;


import branch.dpi.atlas.service.cm.source.branch.BranchRequestListener;
import branch.dpi.atlas.service.cm.source.branch.message.FormatException;
import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchSource;
import branch.dpi.atlas.service.cm.source.manzume.message.ManzumeMessage;
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
public class ManzumeListener implements BranchRequestListener {
    private CMHandler cmHandler;
    private static Log log = LogFactory.getLog(ManzumeListener.class);

    public boolean process( BranchSource source, String manzumeMsgStr) {

        CMMessage msg = new CMMessage();
        HashMap holder = new HashMap();
        try {
            // Parse SMSMessageStr

            manzumeMsgStr = manzumeMsgStr.replaceAll("'", " ");
            manzumeMsgStr = manzumeMsgStr.replaceAll("//", "  ");
            manzumeMsgStr = manzumeMsgStr.replaceAll("/", "-");
            manzumeMsgStr = manzumeMsgStr.replaceAll("http://", "http___");
            manzumeMsgStr = manzumeMsgStr.replace('|', ' ');
            manzumeMsgStr = manzumeMsgStr.replace(':', ' ');
            ManzumeMessage manzumeMessage= ManzumeMessage.parseMessage(manzumeMsgStr);
            manzumeMessage.setTerminalId(Constants.TERMINAL_ID_MANZUME);
            msg.setAttribute(CMMessage.COMMAND_OBJ, manzumeMessage);
            msg.setAttribute(CMMessage.MESSAGE_SOURCE, source);
            msg.setAttribute(CMMessage.SERVICE_TYPE, Constants.MANZUME_SERVICE);

            msg.setAttribute(Fields.PIN,manzumeMessage.getPin());

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
                //log.
                try {
                    source.sendBranch(manzumeMsgStr);
                } catch (IOException e) {
                    log.error("Can not send response to Manzume webservice: " + e);
                }
            }
        } catch (FormatException e) {
            log.error("Incoming Manzume message has invalid format::" + manzumeMsgStr);
            try {
                manzumeMsgStr = manzumeMsgStr.substring(0, 55) + ActionCode.FORMAT_ERROR;
                source.sendBranch(manzumeMsgStr);
            } catch (IOException e1) {
                log.error("Can not send response to Manzume webservice: " + e1);
            }
        } catch (Exception e) {
            //TODO: handle Exception
            try {
                log.error("Incoming Manzume message has invalid format::" + manzumeMsgStr);
                manzumeMsgStr = manzumeMsgStr.substring(0, 55) + ActionCode.FORMAT_ERROR;
                log.fatal("Exception in ManzumeListener ");
                source.sendBranch(manzumeMsgStr);
            } catch (IOException e1) {
                log.error("Can not send response to Manzume webservice: " + e1);
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
