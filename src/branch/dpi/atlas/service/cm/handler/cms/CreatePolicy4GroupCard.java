package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.groupCharge.CardPolicy;
import branch.dpi.atlas.util.ImmediateCardUtil;
import dpi.atlas.model.tj.entity.CardAccount;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;
import dpi.atlas.util.DateUtil;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: september 02, 2017
 * Time: 12:04:03
 */
public class CreatePolicy4GroupCard extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        String  endDate;
        String  nextDate;
        try {

            CardPolicy policyMsg = (CardPolicy) msg.getAttribute(Fields.POLICY_CLASS);
           String  accountNo = policyMsg.getAccountNo();
           String  cardNo = policyMsg.getCardno();
           String  amount = policyMsg.getAmount();
           String  type = policyMsg.getType().trim();
           String  startDate = policyMsg.getStartDate().trim();
           int count = policyMsg.getCount();
           int interval = policyMsg.getInterval();
            String  intervalType = policyMsg.getIntervalType();
           String  currentDate = DateUtil.getSystemDate();

            CardAccount cardAccount = ChannelFacadeNew.getCFSCardAccounts(cardNo,accountNo);
            if (cardAccount==null || cardAccount.equals("")) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_CARD_NUMBER);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_CARD_NUMBER));
            }
            if(cardAccount.getCardType().trim().equalsIgnoreCase(Constants.GROUP_CARD_TYPE_STR) && ImmediateCardUtil.isChild(cardAccount.getSeries())) {

            }else{
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
            }


            if (Integer.parseInt(startDate) < Integer.parseInt(currentDate)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_START_DATE_OF_GROUPCARD_POLICY);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_START_DATE_OF_GROUPCARD_POLICY));

            }

            if (ChannelFacadeNew.ExistPolicyInDB(cardNo)) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.POLICY_HAS_BEEN_DEFINED_BEFORE);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.POLICY_HAS_BEEN_DEFINED_BEFORE));
            } else {
                endDate =DateUtil.calculateDateByIntervalType(startDate,((count-1) * interval),intervalType);
                nextDate =startDate;
                ChannelFacadeNew.InsertPolicy4GroupCard(cardNo, accountNo, msg.getAttributeAsString(Fields.SESSION_ID), type, startDate, endDate, nextDate, count, amount, interval,intervalType, msg.getAttributeAsString(CMMessage.SERVICE_TYPE));
            }

        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CreatePolicy4GroupCard.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

