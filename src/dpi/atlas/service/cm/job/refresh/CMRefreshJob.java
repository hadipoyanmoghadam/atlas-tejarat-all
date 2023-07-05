package dpi.atlas.service.cm.job.refresh;

import dpi.atlas.quartz.BaseQuartzJob;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.model.facade.CommonFacade;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;

/**
 * Created by Behnaz
 * Date: Nov 23, 2011
 * Time: 11:57:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class CMRefreshJob extends BaseQuartzJob {
    private static Log log = LogFactory.getLog(CMRefreshJob.class);

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
    }

    protected void doExecute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {

            Map customerTemplateByIDMap = ChannelFacadeNew.getCustomerTemplateByIDMap();
            customerTemplateByIDMap.clear();

            Map cmServerServicesByIDMap = ChannelFacadeNew.getCmServerServicesByIDMap();
            cmServerServicesByIDMap.clear();

            Map opCodeMap = ChannelFacadeNew.getOpCodeMap();
            opCodeMap.clear();

            Map allTemplateByIDMap = ChannelFacadeNew.getAllTemplateByIDMap();
            allTemplateByIDMap.clear();

            Map billpaymentAccountByIDMap = ChannelFacadeNew.getBillpaymentAccountByIDMap();
            billpaymentAccountByIDMap.clear();

            Map cmSourceServicesByIDMap = ChannelFacadeNew.getCmSourceServicesByIDMap();
            cmSourceServicesByIDMap.clear();

            Map cmDeviceMap = ChannelFacadeNew.getDeviceMap();
            cmDeviceMap.clear();

            Map cmTxTypeSgbTxCodeMap = ChannelFacadeNew.getTxTypeSgbTxCodeMap();
            cmTxTypeSgbTxCodeMap.clear();

            Map cmtxTypeSPWChannelCodeMap = ChannelFacadeNew.getTxTypeSPWChannelCodeMap();
            cmtxTypeSPWChannelCodeMap.clear();

            Map cmServerMap = ChannelFacadeNew.getCmServerByIDMap();
            cmServerMap.clear();

            Map cmCustomerTmplSrvTxValueMap = ChannelFacadeNew.getCustomerTmplSrvTxValueMap();
            cmCustomerTmplSrvTxValueMap.clear();

            Map intermediateAccountByIDMap = ChannelFacadeNew.getIntermediateAccountByIDMap();
            intermediateAccountByIDMap.clear();

            Map intermediateAccountByTypeMap = ChannelFacadeNew.getIntermediateAccountByTypeMap();
            intermediateAccountByTypeMap.clear();

            Map cmImdMap = ChannelFacadeNew.getImdMap();
            cmImdMap.clear();

            if(ChannelFacadeNew.getBankValidCodeslist()!= null)
                ChannelFacadeNew.getBankValidCodeslist().clear();

            CommonFacade.getAccountTypeMap().clear();
            CommonFacade.getBillpaymentAccountByIDMap().clear();
            CommonFacade.getDeviceMap().clear();
            CommonFacade.getImdMap().clear();
            CommonFacade.getTxTypeSgbTxCodeMap().clear();
            CommonFacade.getTxTypeMap().clear();
            CommonFacade.getCustomerTmplSrvTxValueMap().clear();

            Map cmParamMap = ChannelFacadeNew.getCmParamMap();
            if (cmParamMap != null)
                cmParamMap.clear();

            Map branchMap = ChannelFacadeNew.getBranchMap();
            if (branchMap != null)
                branchMap.clear();

            Map descriptionMap = ChannelFacadeNew.getDescriptionMap();
            if (descriptionMap != null)
                descriptionMap.clear();

            Map cmParametersByIDMap = ChannelFacadeNew.getCmParametersByIDMap();
            if (cmParametersByIDMap != null)
                cmParametersByIDMap.clear();

            Map manzumeBranchCodeMap = ChannelFacadeNew.getManzumeBranchCodeMap();
            if (manzumeBranchCodeMap != null)
                manzumeBranchCodeMap.clear();

            Map virtualAccMap = ChannelFacadeNew.getVirtualAcc();
            if (virtualAccMap != null)
                virtualAccMap.clear();

            Map manzumeLongTermAccGroupMap = ChannelFacadeNew.getManzumeLongTermAccGroupMap();
            if (manzumeLongTermAccGroupMap != null)
                manzumeLongTermAccGroupMap.clear();

        } catch (Exception e) {
            log.error("Exception in CMRefreshJob>> Can not Clear cash::" + e.getMessage());
        }

    }

}
