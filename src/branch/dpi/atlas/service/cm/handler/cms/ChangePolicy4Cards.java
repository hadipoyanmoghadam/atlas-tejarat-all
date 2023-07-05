package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.groupCharge.CardNoList;
import branch.dpi.atlas.service.cm.handler.cms.groupCharge.CardPolicy;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.DateUtil;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: september 04, 2017
 * Time: 13:04:03
 */
public class ChangePolicy4Cards extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        CardPolicy existPolicy;
        String endDate;
        String nextDate;
        try {

            CardPolicy policyMsg = (CardPolicy) msg.getAttribute(Fields.POLICY_CLASS);
            CardNoList cardNoList = policyMsg.getCardnoList();
            String amount = policyMsg.getAmount();
            String type = policyMsg.getType().trim();
            String startDate = policyMsg.getStartDate().trim();
            int count = policyMsg.getCount();
            int interval = policyMsg.getInterval();
            String  intervalType = policyMsg.getIntervalType();
            int currentDate = Integer.parseInt(DateUtil.getSystemDate());

            existPolicy = ChannelFacadeNew.getPolicy(cardNoList.getCardNo().get(0));
            if (existPolicy == null || existPolicy.equals(""))
                throw new NotFoundException(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GROUPCARD_POLICY_NOT_FOUND));

            endDate = DateUtil.calculateDateByIntervalType(startDate, ((count-1) * interval),intervalType);

            if (Integer.parseInt(startDate) >= currentDate)
                nextDate = startDate;
            else {
                nextDate = DateUtil.calculateDateByIntervalType(startDate, interval,intervalType);

                while (Integer.parseInt(nextDate) < currentDate && Integer.parseInt(nextDate) < Integer.parseInt(endDate)) {
                    nextDate = DateUtil.calculateDateByIntervalType(nextDate, interval,intervalType);
                }

            }

            if (!startDate.equalsIgnoreCase(existPolicy.getStartDate().trim()) && (Integer.parseInt(existPolicy.getStartDate().trim()) < currentDate || Integer.parseInt(nextDate) < currentDate))
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_START_DATE_OF_GROUPCARD_POLICY));

            if (count != existPolicy.getCount() && Integer.parseInt(endDate) < currentDate)
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_COUNT_OF_GROUPCARD_POLICY));

            if (interval != existPolicy.getInterval() && Integer.parseInt(endDate) < currentDate)
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_INTERVAL_OF_GROUPCARD_POLICY));

            ChannelFacadeNew.UpdatePolicy4CardNoList(msg.getAttributeAsString(Fields.SESSION_ID), type, startDate, endDate, nextDate, count, amount, interval,intervalType, msg.getAttributeAsString(CMMessage.SERVICE_TYPE), cardNoList);
        } catch (CMFault e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_START_DATE_OF_GROUPCARD_POLICY);
            throw e;
        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GROUPCARD_POLICY_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.GROUPCARD_POLICY_NOT_FOUND);
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside ChangePolicy4Cards.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

