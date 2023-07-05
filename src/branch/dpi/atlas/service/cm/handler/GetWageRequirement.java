package branch.dpi.atlas.service.cm.handler;

import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import dpi.atlas.model.tj.facade.ChannelFacadeNew;
import dpi.atlas.service.cm.CMFault;
import dpi.atlas.service.cm.CMMessage;
import dpi.atlas.service.cm.handler.TJServiceHandler;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;
import dpi.atlas.service.cm.iso2000.ActionCode;
import org.jpos.core.Configurable;

import java.sql.SQLException;
import java.util.Map;

/**
 * User:F.Heydari
 * Date:24 Dec 2019
 * time:9:40 AM
 */
public class GetWageRequirement extends TJServiceHandler implements Configurable {
    public void doProcess(CMMessage msg, Map holder) throws CMFault {
        try {
            CMCommand command = (CMCommand) msg.getAttribute(CMMessage.REQUEST);
            SMSMessage smsMessage = (SMSMessage) msg.getAttribute(CMMessage.COMMAND_OBJ);
            String smsWageNumber= ChannelFacadeNew.getCMParam("SMSWageNumber", "0").trim();
            String smsWageAmount= ChannelFacadeNew.getCMParam("SMSWageAmount", "0").trim();
            String smsBranchCode= ChannelFacadeNew.getCMParam("SMSBranchCode", "0").trim();
            msg.setAttribute(Fields.SMS_WAGE_NUMBER,smsWageNumber);
            msg.setAttribute(Fields.AMOUNT,smsWageAmount);
            command.addParam(Fields.AMOUNT,smsWageAmount);
            msg.setAttribute(Fields.SMS_BRANCH_CODE,smsBranchCode);
            command.addParam(Fields.BRANCH_CODE,smsBranchCode);
            command.addParam(Fields.DEST_ACCOUNT,smsWageNumber);
            smsMessage.setAmount(smsWageNumber);




        } catch (SQLException e) {
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.DATABASE_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.DATABASE_ERROR));
        } catch (Exception e) {
            log.error("ERROR :::Inside GetWageRequirement.doProcess(): " + e.getMessage());
            msg.setAttribute(Fields.ACTION_CODE, ActionCode.GENERAL_ERROR);
            throw new CMFault(CMFault.FAULT_INTERNAL, new Exception(ActionCode.GENERAL_ERROR));
        }

    }

}
