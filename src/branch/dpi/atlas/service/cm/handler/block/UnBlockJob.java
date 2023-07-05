package branch.dpi.atlas.service.cm.handler.block;

import dpi.atlas.model.entity.log.Block;
import dpi.atlas.model.entity.log.RowOfBlock;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.quartz.BaseQuartzJob;
import dpi.atlas.service.cm.block.UnBlockHandler;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.jpos.iso.ISOUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.SQLException;
import java.util.*;

/**
 * User: F.Heydari
 * Date: Jun 9, 2020
 * Time: 09:27 AM
 */
public class UnBlockJob extends BaseQuartzJob {

    private static Log log = LogFactory.getLog(UnBlockJob.class);

    private int pickNum;
    private Configuration cfg;

    UnBlockHandler unBlockHandler;
    boolean getFromCash;

    protected void doExecute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        try {
            log.error("UnBlock job started in Date::"+ DateUtil.getSystemDate()+"and time::"+DateUtil.getSystemTime());
            Collection row = ChannelFacadeNew.findAllUnBlocksByPriority(pickNum);
            String returned_actionCode;
            String blockNumber = "";
            String amount = "";
            int batchpk = 0;
            int i = 1;

            for (Iterator it = row.iterator(); it.hasNext(); i++) {
                Block blockMsg = (Block) it.next();
                batchpk = blockMsg.getBatchPk();
                try {
                    Collection rowOfEachBlock = ChannelFacadeNew.findEachUnBlockRows(batchpk, pickNum);
                    for (Iterator rows = rowOfEachBlock.iterator(); rows.hasNext(); i++) {

                        RowOfBlock blc = (RowOfBlock) rows.next();
                        blockNumber = blc.getBlockNo();
                        amount = blc.getAmount();
                        returned_actionCode = validateFields(blockNumber, amount);

                        if (!returned_actionCode.equals(ActionCode.FORMAT_ERROR)) {
                            returned_actionCode = handle(blockMsg, blc, blockNumber);
                            if (returned_actionCode.equals("")) {
                                returned_actionCode = ActionCode.INVALID_SRC_HOST;
                            }
                        }

                        String processStatus = "";
                        if (returned_actionCode.equals(ActionCode.APPROVED) || returned_actionCode.equals(ActionCode.NO_REALTED_BLCK_EXIST)
                                ||returned_actionCode.equals(ActionCode.DUPLICATE_BLCK_NO)) {
                            processStatus = Constants.SUCCESS_UNBLOCK_PROCESS_STATUS;
                        } else {
                            processStatus = Constants.FAILED_UNBLOCK_PROCESS_STATUS;
                        }
                        ChannelFacadeNew.updateFinUnblockRow(blockNumber, batchpk,returned_actionCode,processStatus);

                    }
                     ChannelFacadeNew.unblockErrorUpdate(batchpk);
                    ChannelFacadeNew.changeStatusUnblock(batchpk, Constants.UNBLOCK_SERVICE_FINISH_STATUS);

                } catch (SQLException e) {
                    log.error("SQLException in UnBlockJob>>>>" + e.getMessage());
                } catch (Exception e) {
                    log.error("Exception in UBlockJob>>>>" + e.getMessage());
                }
            }
        } catch (Exception e) {
            log.fatal("***Exception in unBlockJob>>can not fetch record!!!***" + e.getMessage());
        }
        log.error("UnBlock job finished in Date::"+ DateUtil.getSystemDate()+"and time::"+DateUtil.getSystemTime());
    }


    public String handle(Block block, RowOfBlock blockRow, String blockNo) throws Exception {

        if (blockRow.getHostId().equals(Constants.HOST_ID_CFS)) {
            return unBlockHandler.handleCFS(block, blockRow, blockNo);
        } else if (blockRow.getHostId().equals(Constants.HOST_ID_FARAGIR)) {
            return unBlockHandler.handleFaragir(block, blockRow, blockNo);
        } else
            return "";
    }

    public String validateFields(String blockNumber, String amount) throws Exception {
        String actionCode = "";

        if (ISOUtil.isZero(amount)) {
            actionCode = ActionCode.FORMAT_ERROR;
        }
        if (blockNumber == null || blockNumber.trim().equalsIgnoreCase("") || ISOUtil.isZero(blockNumber)) {
            actionCode = ActionCode.FORMAT_ERROR;
        }
        return actionCode;
    }

    public void setConfiguration(Configuration cfg) throws ConfigurationException {

        pickNum = Integer.parseInt(cfg.get("pick-num"));
        String getBatchFromCash = cfg.get("getBatchFromCash");
        getFromCash = getBatchFromCash.equals("yes");
        unBlockHandler = new UnBlockHandler();
        unBlockHandler.setConfiguration(cfg);

    }
}
