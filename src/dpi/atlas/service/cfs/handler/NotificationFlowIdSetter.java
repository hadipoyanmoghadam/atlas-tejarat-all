package dpi.atlas.service.cfs.handler;


import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.util.Constants;

import java.util.Map;

/**
 * User: H.Ghayoumi
 * Date: Jul 13, 2013
 * Time: 10:05:52 AM
 */
public class NotificationFlowIdSetter  extends CFSHandlerBase {
    
    public void doProcess(CMMessage cmMessage, Map map) {

        String  flowId = "";
        String serviceType = cmMessage.getAttributeAsString(Fields.SERVICE_TYPE);
        if(serviceType.equals(Constants.ISO_SERVICE))
            flowId = "1";
        else if(serviceType.equals(Fields.SERVICE_NASIM)  || serviceType.equals(Fields.SERVICE_TOURIST_CARD) ||
                serviceType.equals(Fields.SERVICE_CREDITS) || serviceType.equals(Fields.SERVICE_SAFE_BOX)||
                serviceType.equals(Fields.SERVICE_REG_SMS) || serviceType.equals(Fields.SERVICE_MARHONAT)|| serviceType.equals(Fields.SERVICE_MANZUME) )
            flowId = "2";
//        else if(serviceType.equals(Fields.SERVICE_BRANCH))
//            flowId = "4";
        else if(serviceType.equals(Fields.SERVICE_SGB) || serviceType.equals(Fields.SERVICE_CFD)  || serviceType.equals(Fields.SERVICE_CMS)
                || serviceType.equals(Fields.SERVICE_AMX)
                || serviceType.equals(Fields.SERVICE_GROUP_WEB)|| serviceType.equals(Fields.SERVICE_BRANCH_WAGE)
                || serviceType.equals(Fields.SERVICE_FINANCIAL_BLOCK)||serviceType.equals(Fields.SERVICE_FINANCIAL_UNBLOCK))
            flowId = "4";
        else if (serviceType.equals(Fields.SERVICE_PG)) {
            CMCommand command = (CMCommand) cmMessage.getAttribute(CMMessage.REQUEST);
            String commandName = command.getCommandName();
            if (commandName != null &&
                    (commandName.equalsIgnoreCase(TJCommand.CMD_CMS_CROUPCARD_DCHARGE) ||
                            commandName.equalsIgnoreCase(TJCommand.CMD_CMS_CROUPCARD_IMMEDIATE_CHARGE)))
                flowId = "7";
            else
                flowId = "6";
        } else if (serviceType.equals(Fields.SERVICE_CMS)) {
            CMCommand command = (CMCommand) cmMessage.getAttribute(CMMessage.REQUEST);
            String commandName = command.getCommandName();
            if (commandName != null &&
                    (commandName.equalsIgnoreCase(TJCommand.CMD_CMS_CROUPCARD_DCHARGE) ||
                            commandName.equalsIgnoreCase(TJCommand.CMD_CMS_CROUPCARD_IMMEDIATE_CHARGE)))
                flowId = "7";
            else
                flowId = "4";
        } else if (serviceType.equals(Fields.SERVICE_SIMIN)) {
            CMCommand command = (CMCommand) cmMessage.getAttribute(CMMessage.REQUEST);
            String commandName = command.getCommandName();
            if (commandName != null &&
                    (commandName.equalsIgnoreCase(TJCommand.CMD_SIMIN_CHANGE_CBI_STATUS) ||
                            commandName.equalsIgnoreCase(TJCommand.CMD_SIMIN_CHANGE_ACCOUNT_STATUS)))
                flowId = "8";
            else
                flowId = "4";
        }else
            flowId = "5";     // 5  Means  khadamate Novin: IVR, SMS, ...
        cmMessage.setAttribute("flw", flowId);
    }
}
