package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cfs.handler.CFSHandlerBase;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;

import java.util.Map;
import java.util.StringTokenizer;

// Global cheking for FT; FT can be between various cores(CFS,SGB and FARAGIR) or the accounts of the same core
// On the other hand, ServiceType producing messages can be Sparrow or EBanking-related producers, so depending on the core(s)
// and the server, various kind of checking must be done on SrcAccount

public class CheckDestAccountGlobal extends CFSHandlerBase implements Configurable {
    public void doProcess(CMMessage msg, Map holder) throws CFSFault {

        String transactionSource = (String) msg.getAttribute(Fields.SERVICE_TYPE);
        String destHostId = (String) msg.getAttribute(Constants.DEST_HOST_ID);

        if (log.isDebugEnabled()) log.debug("transactionSource:" + transactionSource);
        if (log.isDebugEnabled()) log.debug("destHostId:" + destHostId);
        String command = msg.getAttributeAsString(CMMessage.COMMAND);
        String realPayaDstHostId = null != msg.getAttributeAsString(Constants.REAL_DEST_HOST_ID) ?
                msg.getAttributeAsString(Constants.REAL_DEST_HOST_ID) : "";

        boolean isDestHostCFS = destHostId.equalsIgnoreCase(Constants.HOST_ID_CFS);
        boolean isServiceTypeISO = transactionSource.equalsIgnoreCase(Constants.ISO_SERVICE);
        boolean isServiceTypeSafeBox = transactionSource.equalsIgnoreCase(Constants.SAFE_BOX_SERVICE);
        boolean isServiceTypeMarhonat = transactionSource.equalsIgnoreCase(Constants.MARHONAT_SERVICE);
        boolean isServiceTypePG = transactionSource.equalsIgnoreCase(Fields.SERVICE_PG);
        boolean isServiceTypeManzume = transactionSource.equalsIgnoreCase(Fields.SERVICE_MANZUME);

        if (isDestHostCFS || realPayaDstHostId.equals(Constants.CFS_HOSTID)) {

            dpi.atlas.service.cfs.handler.core.CheckOperationValidity checkOperationValidity = new dpi.atlas.service.cfs.handler.core.CheckOperationValidity();
            checkOperationValidity.doProcess(msg, holder);

            if (!isServiceTypeISO && !isServiceTypeSafeBox && !isServiceTypePG && !isServiceTypeMarhonat && !isServiceTypeManzume) {
                dpi.atlas.service.cfs.handler.core.GetFTAccount getFTAccount = new dpi.atlas.service.cfs.handler.core.GetFTAccount();
                getFTAccount.doProcess(msg, holder);
            }

            dpi.atlas.service.cfs.handler.core.CheckNonCardAccDigits checkNonCardAccDigits = new dpi.atlas.service.cfs.handler.core.CheckNonCardAccDigits();
            checkNonCardAccDigits.doProcess(msg, holder);

            dpi.atlas.service.cfs.handler.core.GetFTAccountRangeNew getFTAccountRange = new dpi.atlas.service.cfs.handler.core.GetFTAccountRangeNew();
            getFTAccountRange.setAccountField(Fields.DEST_ACCOUNT);
            getFTAccountRange.doProcess(msg, holder);

            if(isServiceTypeSafeBox || isServiceTypePG || isServiceTypeMarhonat ||isServiceTypeManzume){

                dpi.atlas.service.cfs.handler.core.CheckAccountNew checkAccountNew = new dpi.atlas.service.cfs.handler.core.CheckAccountNew();
                checkAccountNew.setAccountField(Fields.DEST_ACCOUNT);
                checkAccountNew.doProcess(msg, holder);

                dpi.atlas.service.cfs.handler.core.CheckAccountStatus checkAccountStatus = new dpi.atlas.service.cfs.handler.core.CheckAccountStatus();
                checkAccountStatus.setAccountField(Fields.DEST_ACCOUNT);
                checkAccountStatus.doProcess(msg, holder);
            }

            if(isServiceTypePG) {
                dpi.atlas.service.cfs.handler.core.GetAccountLimits getAccountLimits = new dpi.atlas.service.cfs.handler.core.GetAccountLimits();
                getAccountLimits.setAccountField(Fields.DEST_ACCOUNT);
                getAccountLimits.doProcess(msg, holder);
            }
        }

    }

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
