package branch.dpi.atlas.service.cm.handler.groupWeb.chargeRecords;

import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.Charge;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.ChargeList;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.ChargeRecordsReq;
import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.ChargeRecordsResp;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: March 13, 2020
 * Time: 07:51 PM
 */
public class chargeRecordsResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String rrn = msg.getAttributeAsString(Fields.RRN);
            String pan = msg.getAttributeAsString(Fields.PAN);
            String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);
            StringBuilder responseStr = new StringBuilder();

            //CHARGE_PRESENTATION_RESPONSE#ActionCode#RRN#CardNo#AccountNo#Respdatetime#
            // ChargeCount#ChargeActionCode#ChargeType#ChAmount#ChargeDate#EfectiveDate

            responseStr.append("CHARGE_PRESENTATION_RESPONSE")
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(actionCode)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(rrn)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(pan)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(accountNo)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                ChargeList chargeList = (ChargeList) msg.getAttribute(Fields.CHARGE_LIST);
                List<Charge> list = chargeList.getCharge();
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.size());
                for (int i = 0; i < list.size(); i++) {
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getChargeActionCode());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getType());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getAmount());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getChargeDate());
                    responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(list.get(i).getEfectiveDate());
                }
            }

            msg.setAttribute(CMMessage.RESPONSE, responseStr.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside chargeRecordsResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }

    }
}

