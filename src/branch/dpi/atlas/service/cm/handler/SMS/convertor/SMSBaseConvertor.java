package branch.dpi.atlas.service.cm.handler.SMS.convertor;

import branch.dpi.atlas.service.cm.source.branch.message.SMSMessage;
import dpi.atlas.service.cm.ib.format.CMCommand;
import dpi.atlas.service.cm.imf.Fields;


/**
 * User: f.heydari
 */
public class SMSBaseConvertor {

    CMCommand format(SMSMessage smsMessage)
    {
        CMCommand command = new CMCommand();

        command.addParam(Fields.REQUEST_DATE, smsMessage.getRequestDate());
        command.addParam(Fields.REQUEST_TIME, smsMessage.getRequestTime());
      //  command.addParam(Fields.BRANCH_CODE, smsMessage.getBranchCode());
        command.addParam(Fields.MN_RRN, smsMessage.getMessageSequence());

        return command;
    }

}
