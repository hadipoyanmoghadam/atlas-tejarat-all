package branch.dpi.atlas.service.cm.handler.pg.childInfo;

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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by sh.Behnaz on 11/17/18.
 */
public class ChildInfoResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            ChildInfoReq childMsg = (ChildInfoReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(ChildInfoResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ChildInfoResp childResp = new ChildInfoResp();
            childResp.setActioncode(actionCode);
            childResp.setDesc(actionMsg);
            childResp.setRrn(childMsg.getRrn());
            childResp.setCardno(childMsg.getCardno());
            childResp.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                Customer customer = (Customer) msg.getAttribute(Constants.CUSTOMER);
                Charge charge = (Charge) msg.getAttribute(Fields.CHARGE_LIST);

                childResp.setAvailableBalance(msg.getAttributeAsString(Fields.AVAILABLE_BALANCE).trim());
                childResp.setAvailableBalanceSign(msg.getAttributeAsString(Fields.AVAILABLE_BALANCE_SIGN).trim());
                childResp.setName(customer.getFirstName() !=null ? customer.getFirstName().trim() : "");
                childResp.setFamily(customer.getLastName()!=null ? customer.getLastName().trim() : "");
                childResp.setNationalCode(customer.getNationalCode() != null ? customer.getNationalCode().trim() : "");
                childResp.setFrgCode(customer.getExternalIdNumber()!=null ? customer.getExternalIdNumber().trim() : "");
                childResp.setAmount(charge.getAmount()!=null ? charge.getAmount().trim() : "0");
                childResp.setAccountno( msg.getAttributeAsString(Fields.ACCOUNT_NO));
                childResp.setChargeDate(charge.getChargeDate()!=null ? charge.getChargeDate().trim() : "");
                childResp.setEfectiveDate(charge.getEfectiveDate()!=null ? charge.getEfectiveDate().trim() : "");
                childResp.setType(charge.getType()!=null ? charge.getType().trim() : "");
            }
            marshaller.marshal(childResp, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());
        } catch (Exception e) {
            log.error("ERROR :::Inside ChildInfoResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

