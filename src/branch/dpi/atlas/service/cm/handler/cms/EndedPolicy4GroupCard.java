package branch.dpi.atlas.service.cm.handler.cms;

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

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: August 04, 2019
 * Time: 09:04:03
 */
public class EndedPolicy4GroupCard extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        CardPolicy existPolicy;
        try {

            String cardNo = msg.getAttributeAsString(Fields.PAN);

            existPolicy = ChannelFacadeNew.getPolicy(cardNo);
            if (existPolicy == null || existPolicy.equals(""))
                throw new NotFoundException(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GROUPCARD_POLICY_NOT_FOUND));

            ChannelFacadeNew.EndedPolicy4GroupCard(cardNo, msg.getAttributeAsString(CMMessage.SERVICE_TYPE),existPolicy)  ;

        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.GROUPCARD_POLICY_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, ActionCode.GROUPCARD_POLICY_NOT_FOUND);
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside EndedPolicy4GroupCard.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

