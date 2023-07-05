package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.childCard.ChildCardReq;
import branch.dpi.atlas.service.cm.handler.cms.childCard.ChildInfo;
import branch.dpi.atlas.util.ImmediateCardUtil;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: Nov 3, 2018
 * Time: 15:04:03 PM
 */
public class CreateChildCardInCFS extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        String accountNo;
        String accountGroup;
        String cardNo;
        String editDate;
        String row = "";
        String latinName = "";
        long maxTransLimit = 0;


        try {

            ChildInfo custInfo = new ChildInfo();
            ChildCardReq customerMsg = (ChildCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
            custInfo.setAccountgroup(customerMsg.getAccountgroup().trim());
            custInfo.setAccountno(customerMsg.getAccountno());
            custInfo.setBirthDate(customerMsg.getBirthDate());
            custInfo.setFamily(customerMsg.getFamily());
            custInfo.setFrgCode(customerMsg.getFrgCode());
            custInfo.setFatherName(customerMsg.getFatherName());
            custInfo.setIDNumber(customerMsg.getIDNumber());
            custInfo.setIssuePlace(customerMsg.getIssuePlace());
            custInfo.setMobileNo(customerMsg.getMobileNo());
            custInfo.setName(customerMsg.getName());
            custInfo.setNationalCode(customerMsg.getNationalCode());
            custInfo.setSex(customerMsg.getSex());
            custInfo.setAccountType(customerMsg.getAccount_type());
            cardNo = customerMsg.getCardno();
            editDate = customerMsg.getEditdate();
            latinName = customerMsg.getNamefamilylatin();
            row = customerMsg.getRow().trim();
            accountNo = customerMsg.getAccountno();
            accountGroup = customerMsg.getAccountgroup().trim();

            if (ChannelFacadeNew.ExistCardInDB(cardNo)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE));
            } else {
                String[] custacc = ChannelFacadeNew.findCustomerID(accountNo, "", "");
                if (custacc[0] == null || custacc[0].equals("")) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
                }

                if (!(custacc[3].equals(Constants.GROUP_CARD_TYPE_STR)  && ImmediateCardUtil.isChild(Integer.parseInt(row)))) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
                }
                    ChannelFacadeNew.InsertChildCardAndAccount(cardNo, accountNo, editDate, accountGroup, row, maxTransLimit, latinName, custInfo, custacc[1]);
            }


        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CreateChildCardInCFS.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

