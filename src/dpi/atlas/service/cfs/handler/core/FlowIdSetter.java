package dpi.atlas.service.cfs.handler.core;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.core.TJCommand;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import org.jpos.core.Configurable;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * MesssageFlowIdSetter class
 * with Ahmadi
 *
 * @author <a href="mailto:A_H_Ahmadi@tyahoo.com">Amir hosein Ahmadi</a>
 * @version $Revision: 1.8 $ $Date: 2007/10/29 14:04:20 $
 */
public class FlowIdSetter extends CMHandlerBase implements Configurable {

    public void doProcess(CMMessage cmMessage, Map map) throws CMFault {
        String ResponseID = "FlowID";

        String command = getAttribute(cmMessage, map, "command");
        if (log.isDebugEnabled()) log.debug("Command= " + command);
        String flow = null;

        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CMS_BALANCE))
                flow = "80";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CMS_GIFTCARD_CHARGE))
                flow = "81";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CMS_GIFTCARD_DISCHARGE))
                flow = "82";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CMS_CANCELLATION))
                flow = "83";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CMS_GIFTCARD_GET_DEPOSIT))
                flow = "84";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CMS_BALANCE_BATCH))
                flow = "85";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CMS_CHARGE_TRANSACTION) || checkCase(command, TJCommand.CMD_CMS_WFP_STATEMENT))
                flow = "86";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CMS_CROUPCARD_DCHARGE))
                flow = "87";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CMS_CROUPCARD_ALL_DCHARGE))
                flow = "88";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CMS_CROUPCARD_IMMEDIATE_CHARGE))
                flow = "89";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_GROUP_ACCOUNT_STATEMENT))
                flow = "107";

        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CARD_BALANCE))
                flow = "5";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_CARD_STATEMENT))
                flow = "7";

        if (flow == null)
            if (checkCase(command, TJCommand.CMD_DEPOSIT_STOCK_PG))
                flow = "90";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_DEPOSIT_STOCK_REVERSE_PG))
                flow = "115";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_FOLLOW_UP_PG))
                flow = "104";
        if(flow==null)
            if (checkCase(command, TJCommand.CMD_BRANCH_BALANCE+","+ TJCommand.CMD_CREDITS_BALANCE+","+
                    TJCommand.CMD_MARHOONAT_INSURANCE_BALANCE)|| checkCase(command, TJCommand.CMD_SMS_BALANCE))
                flow = "100";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_DEPOSIT+","+TJCommand.CMD_CREDITS_DEPOSIT))
                flow = "101";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_WITHDRAW +","+ TJCommand.CMD_CREDITS_WITHDRAW))
                flow = "102";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_DEPOSIT_REVERSAL) ||
                    checkCase(command, TJCommand.CMD_BRANCH_WITHDRAW_REVERSAL)|| checkCase(command, TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD_REVERSAL +","+
                    TJCommand.CMD_CREDITS_DEPOSIT_REVERSAL +","+ TJCommand.CMD_CREDITS_WITHDRAW_REVERSAL))
                flow = "103";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_FOLLOWUP+","+ TJCommand.CMD_CREDITS_FOLLOWUP +","+
                    TJCommand.CMD_FOLLOW_UP_SAFE_BOX+","+ TJCommand.CMD_FOLLOW_UP_SMS+","+ TJCommand.CMD_FOLLOW_UP_MARHOONAT_INSURANCE
                    +","+ TJCommand.CMD_MANZUME_FOLLOWUP))
                flow = "104";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_CHANGE_ACCOUNT_STATUS))
                flow = "105";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_RESET_ACCOUNT))
                flow = "106";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_STATEMENT +","+ TJCommand.CMD_CREDITS_STATEMENT)|| checkCase(command, TJCommand.CMD_SIMIN_STATEMENT))
                flow = "107";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_DEPOSIT_GIFTCARD))
                flow = "108";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_WITHDRAW_GIFTCARD))
                flow = "109";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_CANCELLATION_GIFTCARD))
                flow = "110";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_DISCHARGE_GIFTCARD))
                flow = "111";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_AMX_CHANGE_ACCOUNT_STATUS))
                flow = "112";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_WITHDRAW_REMITTANCE))
                flow = "113";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_FUND_TRANSFER_SAFE_BOX + "," + TJCommand.CMD_FUND_TRANSFER_MARHOONAT_INSURANCE))
                flow = "114";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_FUND_TRANSFER_REVERSE_SAFE_BOX + "," + TJCommand.CMD_FUND_TRANSFER_REVERSE_MARHOONAT_INSURANCE +","+ TJCommand.CMD_MANZUME_DEPOSIT_REVERSAL))
                flow = "115";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_WAGE_SMS))
                flow = "117";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_WITHDRAW_ATM))
                flow = "118";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_DEPOSIT_ATM))
                flow = "119";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_ATM_WITHDRAW_REVERSAL + "," + TJCommand.CMD_BRANCH_ATM_DEPOSIT_REVERSAL))
                flow = "103";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_SIMIN_BALANCE))
                flow = "120";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_SIMIN_DELETE_DOCUMENT))
                flow = "121";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_PAYA_DEPOSIT))
                flow = "122";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_RESEN_PAYA))
                flow = "123";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_SIMIN_CHANGE_CBI_STATUS))
                flow = "124";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_SIMIN_CHANGE_ACCOUNT_STATUS + "," + TJCommand.CMD_FINANCIAL_GROUP_BLOCK +"," + TJCommand.CMD_FINANCIAL_GROUP_UNBLOCK))
                flow = "125";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_WITHDRAW_ACH_WAGE + "," + TJCommand.CMD_BRANCH_WITHDRAW_RTGS_WAGE))
                flow = "128";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_BRANCH_WITHDRAW_WAGE_REVERSAL))
                flow = "129";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_TOURIST_BALANCE))
                flow = "130";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_TOURIST_CHARGE))
                flow = "131";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_TOURIST_DISCHARGE))
                flow = "132";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_TOURIST_CARD_REVOKE))
                flow = "133";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_TOURIST_FOLLOWUP))
                flow = "104";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_TOURIST_CARD_STATEMENT))
                flow = "134";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_TOURIST_CHARGE_STATEMENT))
                flow = "135";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_TOURIST_FUND_TRANSFER))
                flow = "136";
        if (flow == null)
            if (checkCase(command, TJCommand.CMD_MANZUME_DEPOSIT))
                flow = "140";

        if (log.isDebugEnabled()) log.debug("flow = " + flow);
        if (flow != null) cmMessage.setAttribute(ResponseID, flow);
    }

    private boolean checkCase(String key, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, ",");
        boolean existanceFlag = false;
        while (tokenizer.hasMoreTokens()) {
            String val = tokenizer.nextToken().trim();
            if (val.equalsIgnoreCase(key)) {
                existanceFlag = true;
                break;
            }
        }
        return existanceFlag;
    }

    private String getAttribute(CMMessage cmMessage, Map map, String key) {
        String str = (String) cmMessage.getAttribute(key);
        if (str == null)
            str = (String) map.get(key);
        return str;
    }

}
