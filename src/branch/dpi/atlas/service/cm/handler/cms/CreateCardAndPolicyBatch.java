package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.createBatch.CreateGroupCardReq;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.CardPolicy;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;
import branch.dpi.atlas.service.cm.handler.cms.childCard.ChildCardReq;
import branch.dpi.atlas.service.cm.handler.cms.childCard.ChildInfo;
import branch.dpi.atlas.util.ImmediateCardUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: December 18, 2017
 * Time: 12:04:03
 */
public class CreateCardAndPolicyBatch extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String endDate;
        String nextDate;
        long maxTransLimit = 0;
        String latinName = "";

        try {

            ChildInfo custInfo = new ChildInfo();
            CreateGroupCardReq cardMsg = (CreateGroupCardReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
            custInfo.setAccountno(cardMsg.getAccountNo());
            custInfo.setAccountgroup(cardMsg.getAccountgroup().trim());
            custInfo.setAccountType(cardMsg.getAccountType().trim());
            custInfo.setBirthDate(cardMsg.getBirthDate());
            custInfo.setFamily(cardMsg.getFamily());
            custInfo.setFrgCode(cardMsg.getFrgCode());
            custInfo.setFatherName(cardMsg.getFatherName());
            custInfo.setIDNumber(cardMsg.getIDNumber());
            custInfo.setIssuePlace(cardMsg.getIssuePlace());
            custInfo.setMobileNo(cardMsg.getMobileNo());
            custInfo.setName(cardMsg.getName());
            custInfo.setNationalCode(cardMsg.getNationalCode());
            custInfo.setSex(cardMsg.getSex());
            latinName = cardMsg.getNamefamilylatin();
            String cardNo = cardMsg.getCardno();
            String editDate = cardMsg.getEditdate();
            String row = cardMsg.getRow().trim();
            String amount = cardMsg.getAmount();
            String type = cardMsg.getType().trim();
            String startDate = cardMsg.getStartDate().trim();
            int count = cardMsg.getCount();
            int interval = cardMsg.getInterval();
            String  intervalType = cardMsg.getIntervalType();
            String currentDate = DateUtil.getSystemDate();
            String accountNo=cardMsg.getAccountNo();


            if (Integer.parseInt(startDate) < Integer.parseInt(currentDate)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_START_DATE_OF_GROUPCARD_POLICY);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_START_DATE_OF_GROUPCARD_POLICY));

            }

            if (ChannelFacadeNew.ExistPolicyInDB(cardNo)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.POLICY_HAS_BEEN_DEFINED_BEFORE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.POLICY_HAS_BEEN_DEFINED_BEFORE));
            }

            if (ChannelFacadeNew.ExistCardInDB(cardNo)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CARD_HAS_BEEN_DEFINED_BEFORE));
            }


            endDate = DateUtil.calculateDateByIntervalType(startDate, ((count-1) * interval),intervalType);
            nextDate = startDate;

            String[] custacc = ChannelFacadeNew.findCustomerID(accountNo,"","");
            if (custacc[0] == null || custacc[0].equals("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_FOUND);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_FOUND));
            }
            if (!(custacc[3].equals(Constants.GROUP_CARD_TYPE_STR)  && ImmediateCardUtil.isChild(Integer.parseInt(row)))) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
            }

            ChannelFacadeNew.InsertCardAndPolicy(cardNo, msg.getAttributeAsString(Fields.SESSION_ID), type, startDate, endDate, nextDate, count, amount, interval,intervalType, editDate, row, maxTransLimit,latinName, custInfo,custacc[1]);

        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CreateCardAndPolicyBatch.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }

}

