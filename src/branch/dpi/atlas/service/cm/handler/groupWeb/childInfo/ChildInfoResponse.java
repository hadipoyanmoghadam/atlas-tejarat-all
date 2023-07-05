package branch.dpi.atlas.service.cm.handler.groupWeb.childInfo;

import branch.dpi.atlas.service.cm.handler.pg.chargeRecords.Charge;
import dpi.atlas.model.tj.entity.Customer;
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
 * Time: 08:30 PM
 */

public class ChildInfoResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String rrn = msg.getAttributeAsString(Fields.RRN);
            String pan = msg.getAttributeAsString(Fields.PAN);
            StringBuilder responseStr = new StringBuilder();

            //CHILDINFO_RESPONSE#ActionCode#RRN#CardNo#AccountNo#NamePersian#FamilyPersian#NationalCode#frgCode#AvailableBalance
            // #AvailableBalanceSign#ChargeType#ChAmount#ChargeDate#EfectiveDate#Respdatetime

            responseStr.append("CHILDINFO_RESPONSE")
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(actionCode)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(rrn)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(pan)
                    .append(Constants.GROUP_WEB_MEG_SEPARATOR).append(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                Customer customer = (Customer) msg.getAttribute(Constants.CUSTOMER);
                Charge charge = (Charge) msg.getAttribute(Fields.CHARGE_LIST);

                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(msg.getAttributeAsString(Fields.ACCOUNT_NO));
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(customer.getFirstName() != null ? customer.getFirstName().trim() : "");
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(customer.getLastName() != null ? customer.getLastName().trim() : "");
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(customer.getNationalCode() != null ? customer.getNationalCode().trim() : "");
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(customer.getExternalIdNumber() != null ? customer.getExternalIdNumber().trim() : "");
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(msg.getAttributeAsString(Fields.AVAILABLE_BALANCE).trim());
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(msg.getAttributeAsString(Fields.AVAILABLE_BALANCE_SIGN).trim());
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(charge.getType() != null ? charge.getType().trim() : "");
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(charge.getAmount() != null ? charge.getAmount().trim() : "0");
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(charge.getChargeDate() != null ? charge.getChargeDate().trim() : "");
                responseStr.append(Constants.GROUP_WEB_MEG_SEPARATOR).append(charge.getEfectiveDate() != null ? charge.getEfectiveDate().trim() : "");
            }

            msg.setAttribute(CMMessage.RESPONSE, responseStr.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside ChildInfoResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

