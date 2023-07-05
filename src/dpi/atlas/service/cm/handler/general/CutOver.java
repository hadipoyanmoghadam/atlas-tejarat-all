package dpi.atlas.service.cm.handler.general;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Sep 23, 2007
 * Time: 1:48:35 PM
 * To change this template use File | Settings | File Templates.
 */

import branch.dpi.atlas.service.cm.job.FeeSharingJob;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Batch;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;

import dpi.atlas.service.cfs.common.CFSConstants;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;


public class CutOver extends CMHandlerBase implements Configurable {
    private static Log log = LogFactory.getLog(CutOver.class);

    public synchronized void doProcess(CMMessage msg, Map holder) throws CMFault {

        Batch cutOverBatch = null;
        try {
            try {
                cutOverBatch = ChannelFacadeNew.getBatch(new Integer(CFSConstants.CUT_OVER_BATCH), Constants.OPERATOR_OR);

            } catch (SQLException e) {
                log.error(e);
                throw new CMFault(CFSFault.FLT_GENERAL_DATABASE_ERROR, new Exception(ActionCode.DATABASE_ERROR));
            }

            System.out.println("CutOver Job DID NOT Complete!!!!!!!!!!!!!!!, generation of last 24E has not been done for at least one of the cores(CFS,SGB,FARA)...");

            // TODO more finer fault such as "CFSFault.FLT_CUTOVER_BATCH_ALREADY_EXISTS" 
            throw new CMFault(CMFault.FAULT_EXTERNAL, new Exception(ActionCode.CUTOVER_BATCH_ALREADY_EXISTS));

        } catch (NotFoundException e) {
            try {
                cutOverBatch = ChannelFacadeNew.txnCutOverJob();

            } catch (Exception e1) {
                log.error(e);
            }
            System.out.println("cutOverBatch = " + cutOverBatch);
            if (log.isInfoEnabled()) log.info("CutOver Batch is " + cutOverBatch);
            if (log.isInfoEnabled()) log.info("CutOver Job Completed Successfully  - CutOver Batch is " + cutOverBatch);

            try {
            Map batchMap = FeeSharingJob.getBatchMap();
            if (batchMap != null)
                batchMap.clear();
            }catch (Exception exception){
                log.error("Exception in Clear Batch Map for FeeSharingJob::"+exception.getMessage());
            }
        }
    }


}
