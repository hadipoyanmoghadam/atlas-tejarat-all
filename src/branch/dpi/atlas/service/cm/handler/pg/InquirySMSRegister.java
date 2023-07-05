package branch.dpi.atlas.service.cm.handler.pg;

import branch.dpi.atlas.service.cm.handler.pg.smsRegister.SMSInquiryReq;
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
 * Date: October 28, 2018
 * Time: 11:14:03
 */
public class InquirySMSRegister extends CMHandlerBase {

    public void doProcess(CMMessage msg, Map holder) throws CMFault {

        try {

            SMSInquiryReq inquiryMsg = (SMSInquiryReq) msg.getAttribute(CMMessage.COMMAND_OBJ);
            String accountNo = inquiryMsg.getAccountNo();
            String cardNo = inquiryMsg.getCardno();

            String smsRegistrstion = ChannelFacadeNew.getSMSRegistration(accountNo, cardNo);

            msg.setAttribute(Fields.SMS_NOTIFICATION, smsRegistrstion);

        } catch (NotFoundException e) {
            msg.setAttribute(Params.ACTION_CODE, ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.ACCOUNT_NOT_ASSIGNED_TO_CARD));
        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            msg.setAttribute(Fields.ACTION_MESSAGE, ActionCodeMsg.ERROR_SQL_EXCEPTION_MESSAGE);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside InquirySMSRegister.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }
    }
}

