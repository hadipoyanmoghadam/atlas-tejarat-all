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
import org.jpos.core.Configurable;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOUtil;

import java.sql.SQLException;
import java.util.Map;


/**
 * User: F.Heydari
 * Date: Jun 20, 2020
 * Time: 03:23 AM
 */
public class CheckBlockExist extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        BranchMessage bm = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        try {
            String blockNo = "";
            String account_no = "";
            try {
                account_no = ISOUtil.zeropad(bm.getAccountNo().trim(), 13);
            } catch (ISOException e) {
                log.error("Error::in CheckBlockExist(),Can not zeropad accountNo::" + account_no);
            }
            try {
                blockNo = ISOUtil.zeropad(bm.getBlockRow().trim(), 13);
            } catch (ISOException e) {
                log.error("Error::in CheckBlockExist(),Can not zeropad blockNo::" + blockNo);
            }
            if (ChannelFacadeNew.isExistBlockNo(blockNo, account_no) == false) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_BLOCK_ROW);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_BLOCK_ROW));
            }

        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside CheckBlockExist.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

}
