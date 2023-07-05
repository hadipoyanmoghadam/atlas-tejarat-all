package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
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

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Dec 24 2019
 * Time: 04:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlowIdFinderByStatus extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        BranchMessage bm = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);

        try {

            String accountStatus = bm.getAccountStatus();
            String host_id=msg.getAttributeAsString(Fields.HOST_ID);
            String flowID = null;

            if(host_id.equalsIgnoreCase(Constants.HOST_ID_FARAGIR) &&
                    ((accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_ALL_REQUEST) ||
                        accountStatus.equalsIgnoreCase(Constants.UNBLOCK_DEPOSIT_BRANCH_REQUEST) ||
                        accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_BRANCH_REQUEST) ||
                        accountStatus.equalsIgnoreCase(Constants.BLOCK_DEPOSIT_ALL_REQUEST)))){
                // First it must be sent to Faragir then it will do in tbcustomersrv
                flowID="1";
            }else{
                flowID="2";
            }

            if (flowID != null)
                msg.setAttribute(Fields.STATUS_D_FLOW_ID, flowID);

        } catch (Exception e) {
            log.error("ERROR :::Inside SetServiceStatus.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
