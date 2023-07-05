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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Sep 25 2017
 * Time: 03:15 PM
 */
public class PinpadAccountInquiry extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        List<String> account = new ArrayList<String>();
        BranchMessage branchMsg = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            account = ChannelFacadeNew.getPinpadAccountList(branchMsg.getPinpadBranch());
            map.put(Constants.ACCOUNT_LIST, account);
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.BRANCH_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.BRANCH_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside AccountBlockReport.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
