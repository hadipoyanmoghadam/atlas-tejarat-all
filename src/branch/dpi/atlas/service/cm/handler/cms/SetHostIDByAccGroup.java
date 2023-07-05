package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import branch.dpi.atlas.util.ImmediateCardUtil;
import dpi.atlas.util.Constants;

import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: jan 12, 2015
 * Time: 9:24:03 PM
 */
public class SetHostIDByAccGroup extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            String requestType = msg.getAttributeAsString(CMMessage.REQUEST_TYPE);
            if (requestType.equals(Constants.TOURISTCARD_ELEMENT) || requestType.equals(Constants.JOINTACCOUNT_ELEMENT)) {
                msg.setAttribute(Fields.HOST_ID, Constants.HOST_ID_CFS);
                return;
            }

            CUSTOMERType customerMsg = (CUSTOMERType) msg.getAttribute(CMMessage.COMMAND_OBJ);
            String accountGroup = customerMsg.getAccountgroup().trim();
            if (accountGroup.equalsIgnoreCase(Constants.GIFT_CARD_007)) {
                msg.setAttribute(Params.ACTION_CODE, ActionCode.INVALID_OPERATION);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
            }

            if (ImmediateCardUtil.faraGroup(accountGroup, customerMsg.getAccountno()))
                msg.setAttribute(Fields.HOST_ID, Constants.HOST_ID_FARAGIR);
            else if (accountGroup.equalsIgnoreCase(Constants.POSHTIBAN_ACCOUNT_GROUP)) {

                String hostId = ChannelFacadeNew.findAccountHost(customerMsg.getAccountno());
                if (hostId.equalsIgnoreCase(Constants.HOST_ID_UNKNOWN)) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.NO_ACCOUNT_OF_TYPE_REQUESTED));
                } else if (hostId.equalsIgnoreCase(Constants.HOST_ID_SGB)) {
                    msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_IS_OFFLINE);
                    throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_IS_OFFLINE));
                }
                msg.setAttribute(Fields.HOST_ID, hostId);

            } else
                msg.setAttribute(Fields.HOST_ID, Constants.HOST_ID_CFS);

        } catch (CMFault e) {
            throw e;
        } catch (Exception e) {
            log.error("ERROR :::Inside SetHostIDByAccGroup.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
