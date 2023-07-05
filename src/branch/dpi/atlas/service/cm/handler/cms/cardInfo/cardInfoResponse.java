package branch.dpi.atlas.service.cm.handler.cms.cardInfo;

import dpi.atlas.model.tj.entity.Account;
import dpi.atlas.model.tj.entity.Customer;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import org.jpos.iso.ISOUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: jan 12, 2015
 * Time: 10:04:03 PM
 */
public class cardInfoResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            CardInfoReq cardInfoMsg = (CardInfoReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(CardInfoResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            CardInfoResp cardInforesponseType = new CardInfoResp();
            cardInforesponseType.setActioncode(actionCode);
            cardInforesponseType.setDesc(actionMsg);
            cardInforesponseType.setRrn(cardInfoMsg.getRrn());
            cardInforesponseType.setCardno(cardInfoMsg.getCardno());
            cardInforesponseType.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                Account account = (Account) holder.get(Constants.CUSTOMER_ACCOUNT);
                Customer customer = (Customer) holder.get(Constants.CUSTOMER);
                cardInforesponseType.setBirthdate((customer.getBirthDate()!=null ? customer.getBirthDate().trim() : "" ));
                cardInforesponseType.setFamilypersian(customer.getLastName()!=null ? customer.getLastName().trim() : "");
                cardInforesponseType.setFathername((customer.getFatherName()!=null ? customer.getFatherName().trim() :""));
                cardInforesponseType.setIdnumber((customer.getIdNumber()!=null ? customer.getIdNumber().trim() : ""));
                cardInforesponseType.setIssueplace((customer.getIdIssueCode()!=null ? customer.getIdIssueCode().trim() : "0000"));

                String mobileNo= customer.getCellPhone();
                if (mobileNo != null) {
                    mobileNo = ISOUtil.zeroUnPad(mobileNo.trim());
                    if (mobileNo.length() < 11)
                        mobileNo = ISOUtil.padleft(mobileNo, 11, '0');
                    else
                        mobileNo = mobileNo.substring(0, 11);
                } else {
                    mobileNo = ISOUtil.padleft("", 11, '0');
                }
                cardInforesponseType.setMobileno(mobileNo);

                String cardType= Constants.NORMAL_CARD_TYPE;
                if(msg.getAttributeAsString(Fields.CARD_TYPE)!=null &&
                        msg.getAttributeAsString(Fields.CARD_TYPE).trim().equalsIgnoreCase(Constants.GROUP_CARD_TYPE_STR) )
                    cardType= Constants.GROUP_CARD_TYPE;

                cardInforesponseType.setCardtype(cardType);
                cardInforesponseType.setNamepersian(customer.getFirstName() !=null ? customer.getFirstName().trim() : "");
                cardInforesponseType.setNationalcode(customer.getNationalCode()!=null ? customer.getNationalCode().trim() : "");
                cardInforesponseType.setAccounttype(account.getAccountTitle() != null ? account.getAccountTitle().trim() : "");
                if (account.getAccountTitle().trim().equalsIgnoreCase(Constants.SHARED_ACCOUNT_TYPE)) {
                    String nationalCode = customer.getNationalCode().trim();
                    if (!nationalCode.equalsIgnoreCase("0000000000") && nationalCode.length() == 10)
                        cardInforesponseType.setAccounttype("1");
                    else if (!nationalCode.equalsIgnoreCase("0000000000") && nationalCode.length() == 11)
                        cardInforesponseType.setAccounttype("2");
                    else
                        cardInforesponseType.setNationalcode(" ");
                }
                if (account.getAccountTitle().trim().equalsIgnoreCase(Constants.LEGAL_ACCOUNT_TYPE)) {
                    String nationalCode = customer.getNationalCode();
                    if (nationalCode != null && nationalCode.trim().length() < 11) {
                        nationalCode = ISOUtil.padleft(nationalCode.trim(), 11, '0');
                        cardInforesponseType.setNationalcode(nationalCode);
                    }
                }
                cardInforesponseType.setFrgCodecode((customer.getExternalIdNumber() != null ? customer.getExternalIdNumber().trim() : ""));
                cardInforesponseType.setSex(String.valueOf(customer.getGender()).trim());
                cardInforesponseType.setAccountgroup(account.getAccountType()!=null ? account.getAccountType().trim() : "");
                cardInforesponseType.setAccountno(account.getAccountNo()!=null ? account.getAccountNo().trim() : "");
                cardInforesponseType.setBranchCode(account.getSgb_branch_id() != null ? account.getSgb_branch_id().trim() : "");
            }
            marshaller.marshal(cardInforesponseType, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());

        } catch (Exception e) {
            log.error("ERROR :::Inside cardInfoResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

