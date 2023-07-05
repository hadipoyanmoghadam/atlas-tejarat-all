package dpi.atlas.service.cfs.job.refresh;

import dpi.atlas.model.tj.facade.CFSFacadeNew;
import dpi.atlas.quartz.BaseQuartzJob;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Map;
import java.util.HashMap;

/**
 * User: Administrator
 * Date: Feb 2, 2008
 * Time: 11:57:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class RefreshJob extends BaseQuartzJob {//implements RefreshJobMBean {
    private static Log log = LogFactory.getLog(RefreshJob.class);

    public void setConfiguration(Configuration cfg) throws ConfigurationException {
    }

    protected void doExecute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            Map accountTypeMap = CFSFacadeNew.getAccountTypeMap();
            accountTypeMap.clear();

            Map minBalanceMap = CFSFacadeNew.getAccountTypeMinBalanceMap();
            minBalanceMap.clear();

            Map customerTmplSrvTxValueMap = CFSFacadeNew.getCustomerTmplSrvTxValueMap();
            customerTmplSrvTxValueMap.clear();

            Map deviceMap = CFSFacadeNew.getDeviceMap();
            deviceMap.clear();

            Map sgbTxCodeMap = CFSFacadeNew.getTxTypeSgbTxCodeMap();
            sgbTxCodeMap.clear();

            Map SPWChannelsgbTxCodeMap = CFSFacadeNew.getTxTypeSPWChannelCodeMap();
            SPWChannelsgbTxCodeMap.clear();

            Map imdMap = CFSFacadeNew.getImdMap();
            imdMap.clear();

            Map branchMap = CFSFacadeNew.getBranchMap();
            if (branchMap != null)
                branchMap.clear();

            Map giftAccountsMap = CFSFacadeNew.getGiftAccountsMap();
            if (giftAccountsMap != null)
                giftAccountsMap.clear();

            Map giftCardMap = CFSFacadeNew.getGiftCardMap();
            if (giftCardMap != null)
                giftCardMap.clear();

            Map giftAccCustomerIdMap = CFSFacadeNew.getGiftAccCustomerIdMap();
            if (giftAccCustomerIdMap != null)
                giftAccCustomerIdMap.clear();

            Map cmParamMap = CFSFacadeNew.getCmParamMap();
            if (cmParamMap != null)
                cmParamMap.clear();

            Map minBalance = CFSFacadeNew.getMinBalance();
            if (minBalance != null)
                minBalance.clear();

            Map accRangeMap = CFSFacadeNew.getAccountRangeMap();
            accRangeMap.clear();
        } catch (Exception e) {
            log.error("Exception in RefreshJob>> Can not Clear cash::" + e.getMessage());
        }

    }

}
