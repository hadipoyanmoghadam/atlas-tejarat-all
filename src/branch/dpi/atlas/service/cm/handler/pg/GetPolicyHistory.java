package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.pg.policyHistory.Policy;
import branch.dpi.atlas.service.cm.handler.pg.policyHistory.PolicyList;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by sh.Behnaz on 8/28/18.
 */
public class GetPolicyHistory extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {

            String accountNo = msg.getAttributeAsString(Fields.ACCOUNT_NO);
            String cardNo =msg.getAttributeAsString(Fields.PAN);
            String fromDate = msg.getAttributeAsString(Fields.FROM_DATE);
            String toDate = msg.getAttributeAsString(Fields.TO_DATE);


            List<Policy> policies = ChannelFacadeNew.getPolicyHistory(accountNo, cardNo, fromDate, toDate);
            if (policies == null || policies.equals("") || policies.size()==0)
                throw new NotFoundException(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GROUPCARD_POLICY_NOT_FOUND));


            PolicyList list = new PolicyList();
            list.setPolicy(policies);
            msg.setAttribute(Fields.POLICY_LIST, list);


        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GROUPCARD_POLICY_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.GROUPCARD_POLICY_NOT_FOUND);
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside GetPolicyHistory.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

