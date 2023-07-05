package branch.dpi.atlas.service.cm.handler.branch;

import branch.dpi.atlas.service.cm.source.branch.message.BranchMessage;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.ib.format.CMResultSet;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
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
public class UpdateBlockUnBlockStatus extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map map) throws CMFault {
        BranchMessage bm = (BranchMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
        CMResultSet result = (CMResultSet) msg.getAttribute(CMMessage.RESPONSE);

        try {
            String accountStatus = bm.getAccountStatus().trim();
            String blockNo = "";
            String process_status = "";
            String account_no = "";
            String BlockStatus = "";
            String unblockDescription = bm.getDocumentDescription();
            int batchpk;

            try {
                account_no = ISOUtil.zeropad(bm.getAccountNo().trim(), 13);
            } catch (ISOException e) {
                log.error("Error::in UpdateBlockUnBlockStatus(),Can not zeropad accountNo::" + account_no);
            }
            try {
                blockNo = ISOUtil.zeropad(bm.getBlockRow().trim(), 13);
            } catch (ISOException e) {
                log.error("Error::in UpdateBlockUnBlockStatus(),Can not zeropad blockNo::" + blockNo);
            }



            String actionCode = null;
            if (result != null) actionCode = result.getHeaderField(Fields.ACTION_CODE);

            String[] fields = ChannelFacadeNew.getBatchPkProcessStatus(account_no, blockNo);
            if (fields[0] == null || fields[1] == null) {
                throw new Exception("Exeption in UpdateBlockUnBlockStatus().doProcess()!!!");
            } else {
                String oldProcessStatus = fields[0];
                batchpk = Integer.parseInt(fields[1]);
                actionCode = result.getHeaderField(Fields.ACTION_CODE);
                if (accountStatus.equals(Constants.UNBLOCK_ACCOUNT_STATUS)) {
                    if (actionCode.equals(ActionCode.APPROVED) || actionCode.equals(ActionCode.DUPLICATE_BLCK_NO) || actionCode.equals(ActionCode.NO_REALTED_BLCK_EXIST)) {
                        process_status = Constants.SUCCESS_UNBLOCK_PROCESS_STATUS;
                    } else {
                        process_status = Constants.FAILED_UNBLOCK_PROCESS_STATUS;
                    }
                } else if (accountStatus.equals(Constants.BLOCK_ACCOUNT_STATUS)) {
                    if (actionCode.equals(ActionCode.APPROVED) || actionCode.equals(ActionCode.DUPLICATE_BLCK_NO)) {
                        process_status = Constants.SUCCESS_BLOCK_PROCESS_STATUS;
                    } else {
                        process_status = Constants.FAILED_BLOCK_PROCESS_STATUS;
                    }
                    BlockStatus = ChannelFacadeNew.getBlockStatus(batchpk);
                }
               ChannelFacadeNew.updateBlockValues(account_no, process_status, actionCode, batchpk, blockNo, accountStatus,unblockDescription);



                if ((actionCode.equals(ActionCode.APPROVED) && accountStatus.equals(Constants.BLOCK_ACCOUNT_STATUS)) ||
                        (actionCode.equals(ActionCode.DUPLICATE_BLCK_NO) && accountStatus.equals(Constants.BLOCK_ACCOUNT_STATUS))) {
                    if (oldProcessStatus.equals(Constants.FAILED_BLOCK_PROCESS_STATUS)) { //**4 check this blockNo failed befor(block)
                        ChannelFacadeNew.updateResultStatus(account_no, batchpk);
                        if (BlockStatus.equals(Constants.UNBLOCK_SERVICE_FINISH_STATUS)) {
                            ChannelFacadeNew.updateResultStatusUnblock(account_no, batchpk, Constants.AFTER_UNBLOCK_JOB);
                        }
                    }

                } else if ((actionCode.equals(ActionCode.APPROVED) && accountStatus.equals(Constants.UNBLOCK_ACCOUNT_STATUS)) ||
                        (actionCode.equals(ActionCode.NO_REALTED_BLCK_EXIST) && accountStatus.equals(Constants.UNBLOCK_ACCOUNT_STATUS))) {
                    if (oldProcessStatus.equals(Constants.FAILED_UNBLOCK_PROCESS_STATUS))   //**4 check this blockNo failed befor(unblock)

                    {
                        ChannelFacadeNew.updateResultStatusUnblock(account_no, batchpk, Constants.BEFORE_UNBLOCK_JOB);
                    }
                }
                if (accountStatus.equals(Constants.UNBLOCK_ACCOUNT_STATUS) && actionCode.equals(ActionCode.APPROVED)) {
                    ChannelFacadeNew.updateUnBlockDate(batchpk, blockNo);
                }
            }

        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside UpdateBlockUnBlockStatus.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

}
