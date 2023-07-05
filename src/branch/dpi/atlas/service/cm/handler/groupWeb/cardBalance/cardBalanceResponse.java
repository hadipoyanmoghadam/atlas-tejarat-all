package branch.dpi.atlas.service.cm.handler.groupWeb.cardBalance;

import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 13, 2020
 * Time: 07:51 PM
 */
public class cardBalanceResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String rrn = msg.getAttributeAsString(Fields.RRN);
            String pan = msg.getAttributeAsString(Fields.PAN);
            StringBuilder responseStr = new StringBuilder();

            //BALANCE_GROUPCARD_RESPONSE#ActionCode#RRN#CardNo#Respdatetime#AvailableBalance#AvailableBalanceSign#ActualBalance#ActualBalanceSign

            responseStr.append("BALANCE_GROUPCARD_RESPONSE")
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(actionCode)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(rrn)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(pan)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(msg.getAttributeAsString(Fields.AVAILABLE_BALANCE).trim());
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(msg.getAttributeAsString(Fields.AVAILABLE_BALANCE_SIGN).trim());
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(msg.getAttributeAsString(Fields.ACTUAL_BALANCE).trim());
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(msg.getAttributeAsString(Fields.ACTUAL_BALANCE_SIGN).trim());
            }

            msg.setAttribute(CMMessage.RESPONSE, responseStr.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside cardBalanceResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

