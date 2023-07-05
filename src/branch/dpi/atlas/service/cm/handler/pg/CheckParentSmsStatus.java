package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSRegisterReq;
import dpi.atlas.model.NotFoundException;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.CMHandlerBase;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import dpi.atlas.service.cm.iso2000.ActionCodeMsg;

import java.sql.SQLException;
import java.util.Map;

/**
 * User: R.Nasiri
 * Date: Jun 16, 2020
 * Time: 10:12
 */
public class CheckParentSmsStatus extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            SMSRegisterReq registerMsg = (SMSRegisterReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
           String  accountNo = registerMsg.getAccountNo();

           boolean parentSmsStatus=  ChannelFacadeNew.checkParentSmsStatus(accountNo);
            if (parentSmsStatus==false) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.INVALID_OPERATION);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.INVALID_OPERATION));
            }

        } catch (CMFault e) {
            throw e;
        } catch (NotFoundException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.CUSTOMER_NOT_FOUND);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.CUSTOMER_NOT_FOUND);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.CUSTOMER_NOT_FOUND));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside CheckParentSmsStatus.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

