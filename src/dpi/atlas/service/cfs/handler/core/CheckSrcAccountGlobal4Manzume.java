package dpi.atlas.service.cfs.handler.core;


import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import org.jpos.core.Configuration;
import org.jpos.core.ConfigurationException;

import java.util.Map;
import java.util.StringTokenizer;

// Global cheking for FT; FT can be between various cores(CFS,SGB and FARAGIR) or the accounts of the same core
// On the other hand, ServiceType producing messages can be Sparrow or EBanking-related producers, so depending on the core(s)
// and the server, various kind of checking must be done on SrcAccount

public class CheckSrcAccountGlobal4Manzume extends CFSHandlerBase implements Configurable {
    public void setConfiguration(Configuration cfg) throws ConfigurationException {
        super.setConfiguration(cfg);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String transactionSource = (String) msg.getAttribute(Fields.SERVICE_TYPE);
//        String srcHostId = (String) msg.getAttribute(Constants.SRC_HOST_ID);

//        boolean isSrcHostCFS = srcHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);
   boolean isServiceTypeISO = transactionSource.equalsIgnoreCase(Constants.ISO_SERVICE);

        String command = msg.getAttributeAsString(CMMessage.COMMAND);

//        if (isSrcHostCFS) {
//            if (isServiceTypeISO) {
//
//                dpi.atlas.service.cfs.handler.core.CheckCardNew checkCardNew = new dpi.atlas.service.cfs.handler.core.CheckCardNew();
//                checkCardNew.doProcess(msg, holder);
//
//                dpi.atlas.service.cfs.handler.core.CheckCardAccountNew checkCardAccountNew = new dpi.atlas.service.cfs.handler.core.CheckCardAccountNew();
//                checkCardAccountNew.doProcess(msg, holder);
//            }

            dpi.atlas.service.cfs.handler.core.CheckAccountNew checkAccountNew = new dpi.atlas.service.cfs.handler.core.CheckAccountNew();
            checkAccountNew.doProcess(msg, holder);

            dpi.atlas.service.cfs.handler.core.GetFTAccountRangeNew getFTAccountRange = new dpi.atlas.service.cfs.handler.core.GetFTAccountRangeNew();
            getFTAccountRange.setAccountField(Fields.SRC_ACCOUNT);
            getFTAccountRange.doProcess(msg, holder);

            dpi.atlas.service.cfs.handler.core.GetAccountLimits getAccountLimits = new dpi.atlas.service.cfs.handler.core.GetAccountLimits();
            getAccountLimits.doProcess(msg, holder);

            dpi.atlas.service.cfs.handler.core.CheckAccountStatus checkAccountStatus = new dpi.atlas.service.cfs.handler.core.CheckAccountStatus();
            checkAccountStatus.doProcess(msg, holder);

            dpi.atlas.service.cfs.handler.core.CheckAccountBalance checkAccountBalance = new dpi.atlas.service.cfs.handler.core.CheckAccountBalance();
            checkAccountBalance.doProcess(msg, holder);

            if (!(checkCase(command, TJCommand.CMD_BILL_PAYMENT + "," + TJCommand.CMD_PAYMENT_GATEWAY_BLPY_LOCAL + "," + TJCommand.CMD_POS_BLPY_LOCAL + "," + TJCommand.CMD_MOBILE_PAYMENT_BLPY_LOCAL) && isServiceTypeISO)) {
                dpi.atlas.service.cfs.handler.core.CheckCustomerService checkCustomerService = new dpi.atlas.service.cfs.handler.core.CheckCustomerService();
                checkCustomerService.doProcess(msg, holder);
            }
        }
   // }


    private boolean checkCase(String key, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, ",");
        boolean existanceFlag = false;
        while (tokenizer.hasMoreTokens()) {
            String val = tokenizer.nextToken();
            if (val.equalsIgnoreCase(key)) {
                existanceFlag = true;
                break;
            }
        }
        return existanceFlag;
    }
}
