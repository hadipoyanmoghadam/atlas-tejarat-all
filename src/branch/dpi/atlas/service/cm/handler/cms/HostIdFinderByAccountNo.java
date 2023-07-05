package branch.dpi.atlas.service.cm.handler.cms;

import branch.dpi.atlas.service.cm.handler.cms.accountInfo.ACCOUNTINFOType;
import branch.dpi.atlas.service.cm.handler.cms.customer.CUSTOMERType;
import branch.dpi.atlas.util.ImmediateCardUtil;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.entity.Card;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.ib.Params;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;
import dpi.atlas.util.Constants;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: SH.Behnaz
 * Date: Feb 21, 2015
 * Time: 9:24:03 PM
 */
public class HostIdFinderByAccountNo extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            ACCOUNTINFOType accInfoMsg = (ACCOUNTINFOType) msg.getAttribute(CMMessage.COMMAND_OBJ);

            String hostId = ChannelFacadeNew.findAccountHost(accInfoMsg.getAccountno());
            msg.setAttribute(Fields.HOST_ID, hostId);
            msg.setAttribute(Fields.ACCOUNT_NO, accInfoMsg.getAccountno());

        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));

        } catch (Exception e) {
            log.error("ERROR :::Inside HostIdFinderByAccountNo.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}
