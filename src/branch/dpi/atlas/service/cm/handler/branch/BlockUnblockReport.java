package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.model.tj.entity.BlockReport;
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
 * Date: April 5 2015
 * Time: 09:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BlockUnblockReport extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        BranchMessage bm = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        List<BlockReport> reports = new ArrayList<BlockReport>();
        try {
            if (bm.getRequestType().equals("0")|| bm.getRequestType().equals("1")){
                reports = ChannelFacadeNew.getBlockUnBlockReport(bm.getAccountNo(), bm.getRequestType(),bm.getPin());
            map.put(Constants.BLOCK_LIST, reports);
            }else
            {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.UNSUPPORTED_MESSAGE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
            }
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_BLCK_UNBLCK_FOR_THIS_ACCOUNT);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_BLCK_UNBLCK_FOR_THIS_ACCOUNT));
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside BlockUnblockReport.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);
    }
}
