package branch.dpi.atlas.service.cfs.handler.branch;

import dpi.atlas.service.cfs.common.CFSFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import org.jpos.core.Configurable;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Nov 10, 2013
 * Time: 03:53:39 PM
 */
public class FindRequestTypeByAccountStatus extends CMHandlerBase implements Configurable {

    public void doProcess(CMMessage cmMessage, Map map) throws CFSFault {
        try {
            String ResponseID = "RequestType";
            String serviceType = (String) cmMessage.getAttribute(Fields.SERVICE_TYPE);
            String accountStatus = cmMessage.getAttributeAsString(Fields.ACCOUNT_STATUS);
            if (log.isDebugEnabled()) log.debug("accountStatus= " + accountStatus);
            String flow = null;
            if (serviceType.equalsIgnoreCase(Constants.AMX_SERVICE)) {
                char requestNumber = accountStatus.charAt(0);
                if (flow == null) {
                    switch (requestNumber) {
                        case '1':
                            // revoke inquiry
                            flow = "1";
                            break;
                        case '2':
                            // revoke
                            flow = "2";
                            break;
                        case '3':
                            // Unconditional revoke
                            flow = "3";
                            break;
                        case '4':
                            // revival
                            flow = "4";
                            break;
                        default: {
                            flow = null;
                        }
                    }
                }
            } else {
                if (accountStatus.equalsIgnoreCase("0") || accountStatus.equalsIgnoreCase("1"))
                    //block or unblock account
                    flow = "1";
                else if (accountStatus.equalsIgnoreCase("6"))
                    //block amount
                    flow = "4";
                else if (accountStatus.equalsIgnoreCase("5"))
                    //unblock amount
                    flow = "5";
                else if (accountStatus.equalsIgnoreCase("8"))
                    //Financial block amount
                    flow = "6";
                else if (accountStatus.equalsIgnoreCase("7"))
                    //Financial Unblock amount
                    flow = "7";
                else if (serviceType.equals(Constants.BRANCH_SERVICE)) {

                    if (accountStatus.equalsIgnoreCase("2"))
                        //revoke account
                        flow = "2";
                    else if (accountStatus.equalsIgnoreCase("3"))
                        //revival account
                        flow = "3";
                    else if (accountStatus.equalsIgnoreCase("4"))
                        //block amount
                        flow = "4";
                }
            }

            if (log.isDebugEnabled()) log.debug("RequestType = " + flow);
            if (flow != null) cmMessage.setAttribute(ResponseID, flow);
            else {
                throw new CFSFault(CFSFault.FLT_UNSUPPORTED_MESSAGE, new Exception(ActionCode.UNSUPPORTED_MESSAGE));
            }
        } catch (CFSFault e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inside:: FindRequestTypeByAccountStatus:: " + e);
            cmMessage.setAttribute(Params.ACTION_CODE, ActionCode.GENERAL_DATA_ERROR);
            throw new CFSFault(CFSFault.FLT_GENERAL_DATA_ERROR, new Exception(ActionCode.GENERAL_DATA_ERROR));
        }


    }

}
