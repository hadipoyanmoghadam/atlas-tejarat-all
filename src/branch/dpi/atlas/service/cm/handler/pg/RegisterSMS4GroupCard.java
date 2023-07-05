package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSRegisterReq;
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
 * User: SH.Behnaz
 * Date: september 18, 2018
 * Time: 14:04:03
 */
public class RegisterSMS4GroupCard extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            SMSRegisterReq registerMsg = (SMSRegisterReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
           String  accountNo = registerMsg.getAccountNo();
           String  cardNo = registerMsg.getCardNo();
           String  chargeN = registerMsg.getChargeNotify();
           String  parentN = registerMsg.getParentNotify();
           String  transN = registerMsg.getTransNotify();
           String  childN = registerMsg.getChildNotify();
           String registerStr=parentN+childN+transN+chargeN;

              int updateCount=  ChannelFacadeNew.UpdateSMSRegistration4GroupCard( accountNo,cardNo,registerStr);
            if (updateCount < 1) {
                msg.setAttribute(Fields.ACTION_CODE, ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD);
                throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD));
            }

        } catch (CMFault e) {
            throw e;
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside RegisterSMS4GroupCard.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

