package branch.dpi.atlas.service.cm.handler.branch;

import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.connector.Connector;
import branch.dpi.atlas.service.cm.source.branch.socket.BranchSource;

import java.io.IOException;
import java.util.Map;

/**
 * User: H.Ghayoumi
 * Date: Apr 3, 2013
 * Time: 9:36:48 AM
 */
public class BranchLoopBackSender extends CMHandlerBase implements Connector {


    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        if (msg.getAttribute(CMMessage.RESPONSE) != null) {
            try {
                BranchSource source = (BranchSource) msg.getAttribute(CMMessage.MESSAGE_SOURCE);
                String branchResponse = msg.getAttributeAsString(CMMessage.RESPONSE);
                source.sendBranch(branchResponse);
            } catch (IOException e) {
                log.error("***  ***" + e);
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.SOCKET_CLOSED); 
                throw new CMFault(e);
            }
        } else {
            if (log.isInfoEnabled()) log.info("nothing to send back...");
        }
    }

    public void sendAsync(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendAsyncText(Object obj) throws Exception {
        this.process((CMMessage) obj, null);
    }

    public String sendSyncACHText(Object obj) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendASyncACHText(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object sendSyncText(Object obj) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendAsyncByte(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object sendSyncByte(Object obj) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendAsyncObject(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object sendSyncObject(Object obj) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendASyncACHBytes(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void sendACHAsync(Object obj) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
