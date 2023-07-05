package branch.dpi.atlas.service.cm.handler.cms.jointAccountInfo;

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
 * Date: july 12, 2017
 * Time: 10:04:03 AM
 */
//TODO:: Moshtarak 960426
public class jointAccountInfoResponse extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            String actionCode = msg.getAttributeAsString(Params.ACTION_CODE);
            String actionMsg = msg.getAttributeAsString(Params.ACTION_MESSAGE);
            StringWriter responseTypeXml = new StringWriter();

            JointAccountInfoReq accountInfoMsg = (JointAccountInfoReq) msg.getAttribute(CMMessage.COMMAND_OBJ);

            JAXBContext context = JAXBContext.newInstance(JointAccountInfoResp.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JointAccountInfoResp accountInforesponseType = new JointAccountInfoResp();
            accountInforesponseType.setActioncode(actionCode);
            accountInforesponseType.setDesc(actionMsg);
            accountInforesponseType.setRrn(accountInfoMsg.getRrn());
            accountInforesponseType.setAccountno(accountInfoMsg.getAccountno());
            accountInforesponseType.setRespdatetime(DateUtil.getSystemDate() + DateUtil.getSystemTime());

            if (actionCode.equals(ActionCode.APPROVED)) {
                Account account = (Account) holder.get(Constants.CUSTOMER_ACCOUNT);
                Customer customer = (Customer) holder.get(Constants.CUSTOMER);
               String  accountTitle = (String ) holder.get(Fields.ACCOUNT_TITLE);

                accountInforesponseType.setAccountgroup(account.getAccountType()!=null ? account.getAccountType().trim() : "");
                accountInforesponseType.setAccounttype( accountTitle!=null ? accountTitle : "");
                accountInforesponseType.setBirthdate((customer.getBirthDate()!=null ? customer.getBirthDate().trim() : "" ));
                accountInforesponseType.setFamilypersian(customer.getLastName()!=null ? customer.getLastName().trim() : "");
                accountInforesponseType.setFathername((customer.getFatherName()!=null ? customer.getFatherName().trim() :""));
                accountInforesponseType.setIdnumber((customer.getIdNumber()!=null ? customer.getIdNumber().trim() : ""));
                accountInforesponseType.setIssueplace((customer.getIdIssueCode()!=null ? customer.getIdIssueCode().trim() : "0000"));

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

                accountInforesponseType.setMobileno(mobileNo);


//                accountInforesponseType.setMobileno((customer.getCellPhone()!=null ? customer.getCellPhone().trim() : ""));
                accountInforesponseType.setNamepersian(customer.getFirstName() !=null ? customer.getFirstName().trim() : "");
                accountInforesponseType.setNationalcode(customer.getNationalCode() !=null ? customer.getNationalCode().trim() : "");
                accountInforesponseType.setFrgCodecode((customer.getExternalIdNumber()!=null ? customer.getExternalIdNumber().trim() : ""));
                accountInforesponseType.setSex(String.valueOf(customer.getGender()).trim());
                accountInforesponseType.setBranchCode(account.getSgb_branch_id()!=null ? account.getSgb_branch_id().trim() : "");
            }
            marshaller.marshal(accountInforesponseType, responseTypeXml);

            msg.setAttribute(CMMessage.RESPONSE, responseTypeXml.toString());

        } catch (Exception e) {
            log.error("ERROR :::Inside jointAccountInfoResponse.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CHANNEL_CAN_NOT_CREATE_RESPONSE));
        }


    }
}

